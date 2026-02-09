package com.meetkey.server.global.s3;

import com.meetkey.server.domain.member.dto.ProfileResDTO;
import com.meetkey.server.domain.member.entity.Member;
import com.meetkey.server.domain.member.entity.mapping.MemberPhoto;
import com.meetkey.server.domain.member.exception.MemberErrorStatus;
import com.meetkey.server.domain.member.exception.MemberException;
import com.meetkey.server.domain.member.repository.MemberPhotoRepository;
import com.meetkey.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final MemberRepository memberRepository;
    private final MemberPhotoRepository memberPhotoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // S3에 이미지 업로드용 Presigned url 발급
    public ProfileResDTO.MemberPhotoUrl generateMemberPhotoPresignedUrl(
            Long memberId,
            String originalFileName,
            String contentType
    ){
        // 유저별 폴더 구조 생성 : profiles/{memberId}/{UUID}-{fileName}
        String key = "profiles/" + memberId + "/" + UUID.randomUUID() + "-" + originalFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                r -> r.putObjectRequest(putObjectRequest)
                        .signatureDuration(Duration.ofMinutes(5))
        );

        return ProfileResDTO.MemberPhotoUrl.builder()
                .url(presignedPutObjectRequest.url().toString())
                .key(key)
                .build();
    }

    // S3 이미지 조회용 Presigned url 발급
    public String generateGetPresignedUrl(String key){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }

    // s3에서 이미지 삭제
    public void deleteFile(String key){
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build()
        );
    }

    @Transactional
    public void registerMemberPhotoKeys(Long memberId, List<String> s3Keys){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        List<MemberPhoto> oldPhotos = memberPhotoRepository.findAllByMember(member);

        if (!oldPhotos.isEmpty()) {
            oldPhotos.forEach(photo -> deleteFile(photo.getMemberPhotoUrl()));
            memberPhotoRepository.deleteAllInBatch(oldPhotos);
        }

        List<MemberPhoto> photos = s3Keys.stream()
                .map(key -> MemberPhoto.builder()
                        .member(member)
                        .memberPhotoUrl(key)
                        .build())
                .collect(Collectors.toList());

        memberPhotoRepository.saveAll(photos);
    }


    public List<String> getMemberPhotoUrls(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorStatus.MEMBER_NOT_FOUND));

        return memberPhotoRepository.findAllByMember(member).stream()
                .map(photo -> generateGetPresignedUrl(photo.getMemberPhotoUrl()))
                .collect(Collectors.toList());
    }
}
