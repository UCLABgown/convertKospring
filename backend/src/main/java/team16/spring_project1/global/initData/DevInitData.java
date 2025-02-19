package team16.spring_project1.global.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import team16.spring_project1.standard.util.Ut;

@Profile("dev")
@Configuration
@RequiredArgsConstructor
public class DevInitData {
    @Bean
    public ApplicationRunner devInitDataApplicationRunner() {
        return args -> {
            Ut.file.downloadByHttp("http://localhost:8080/v3/api-docs", ".");

            String cmd = "yes | npx --package typescript --package openapi-typescript openapi-typescript api-docs.json -o ../frontend/src/lib/backend/apiV1/schema.d.ts";
            Ut.cmd.runAsync(cmd);
        };
    }
}
