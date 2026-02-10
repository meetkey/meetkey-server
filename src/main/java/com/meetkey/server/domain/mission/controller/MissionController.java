package com.meetkey.server.domain.mission.controller;

import com.meetkey.server.domain.mission.service.MissionService;
import com.meetkey.server.global.apiPayload.response.BasicResponse;
import com.meetkey.server.global.apiPayload.status.CommonSuccessStatus;
import com.meetkey.server.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.meetkey.server.domain.mission.dto.MissionResDTO.*;

@Tag(name = "Mission", description = "채팅방 미션 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat-rooms/{chatRoomId}/missions")
public class MissionController {

    private final MissionService missionService;

    @Operation(summary = "오늘의 미션 조회", description = "해당 채팅방의 현재 진행 중인 미션을 조회합니다. (없으면 미션 자동 생성)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = Info.class))),
            @ApiResponse(responseCode = "400", description = "CHAT2041 : 존재하지 않는 채팅방입니다. , MEMBER4041 : 존재하지 않는 사용자입니다." +
                    "MISSION4041 : 존재하지 않는 미션입니다.")
    })
    @GetMapping("/today")
    public BasicResponse<Info> getTodayMission(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Info response = missionService.getTodayMission(chatRoomId, userDetails.getMemberId());
        return BasicResponse.success(CommonSuccessStatus._OK, response);

    }

    @Operation(summary = "미션 완료 인증", description = "사용자가 채팅을 보내면 호출 -> 내부 로직으로 미션 성공 여부를 판단" +
            "미션 성공 로직 (내용은 신경쓰지 않음)" +
            "1. 미션 타입이 PHOTO인 경우는 사진을 하나 이상 보내야한다. " +
            "2. PHOTO가 아닌 다른 타입의 미션들은 텍스트 채팅을 한 번 이상 남겨야 한다." +
            "3. 미션 받은 이후에 해당 내용을 수행해야함.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = Completion.class))),
            @ApiResponse(responseCode = "400", description = "MEMBER4041 : 존재하지 않는 사용자입니다., MISSION4041 : 존재하지않는 미션입니다., MISSION4001 : 이미 완료된 미션입니다., " +
                    "MISSION4002 : 아직 미션을 수행하지 않았습니다., MISSION4042 : 참여정보를 찾을 수 없습니다.")
    })
    @PostMapping("/{missionId}/complete")
    public BasicResponse<Completion> completeMission(
            @Parameter(description = "채팅방 ID", required = true)
            @PathVariable Long chatRoomId,

            @Parameter(description = "오늘의 미션 조회 시 반환된 'missionId' 값 (ChatRoomMission ID). ※ 주의: 미션 내용 고유 번호(Mission ID)가 아닙니다.", required = true, example = "105")
            @PathVariable Long missionId, // ChatRoomMission의 ID (Mission ID 아님 주의)

            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Completion response = missionService.completeMission(chatRoomId, missionId, userDetails.getMemberId());
        return BasicResponse.success(CommonSuccessStatus._OK, response);
    }




}
