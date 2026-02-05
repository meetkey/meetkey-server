package com.meetkey.server.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("!test")
public class FcmConfig {

    @Value("${fcm.key.path}")
    private String fcmKeyPath;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAccount = getResourceStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("FCM 초기화 실패", e);
        }
    }

    private InputStream getResourceStream() throws IOException {
        // 경로가 /로 시작하면 외부 파일 시스템(prod), 아니면 클래스패스(local)에서 읽도록 구성
        if (fcmKeyPath.startsWith("/")) {
            return new FileInputStream(fcmKeyPath);
        }
        return new ClassPathResource(fcmKeyPath).getInputStream();
    }
}
