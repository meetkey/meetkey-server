package com.meetkey.server.global.config;

import feign.form.FormEncoder;
import org.springframework.context.annotation.Bean;

public class KakaoOauthConfig {
    @Bean
    FormEncoder formEncoder(){
        return new feign.form.FormEncoder();
    }
}
