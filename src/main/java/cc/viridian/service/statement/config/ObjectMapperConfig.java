package cc.viridian.service.statement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper myObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRAP_EXCEPTIONS);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        //SimpleModule module = new SimpleModule();
        //module.addDeserializer(JobTemplate.class, new JobTemplateDeserializer());

        //objectMapper.registerModule(module);

        log.info("creating custom ObjectMapper disabling write_dates_as_timestamps");
        return objectMapper;
    }
}
