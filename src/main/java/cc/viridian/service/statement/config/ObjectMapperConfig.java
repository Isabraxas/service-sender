package cc.viridian.service.statement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@Slf4j
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        log.info("creating custom ObjectMapper disabling write_dates_as_timestamps");
        return objectMapper;
    }
}
