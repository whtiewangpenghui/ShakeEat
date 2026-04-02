package com.wdd.shakeeat;

import com.wdd.shakeeat.support.ai.DeepSeekProperties;
import com.wdd.shakeeat.support.ai.OllamaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({DeepSeekProperties.class, OllamaProperties.class})
@SpringBootApplication
public class ShakeEatApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShakeEatApplication.class, args);
    }

}
