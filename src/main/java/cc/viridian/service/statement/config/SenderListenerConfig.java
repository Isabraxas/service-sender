package cc.viridian.service.statement.config;

import cc.viridian.service.statement.model.SenderTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class SenderListenerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${topic.statement.sender}")
    private String topicStatementSender;

    @Autowired
    private MappingJackson2HttpMessageConverter springMvcJacksonConverter;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "service-statement-sender");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                  "org.apache.kafka.clients.consumer.RoundRobinAssignor");

        log.info("listening kafka server: " + bootstrapServers);
        log.info("listening kafka  topic: " + topicStatementSender);
        return props;
    }

    @Bean
    public ConsumerFactory<String, SenderTemplate> consumerFactory() {
        log.info("ConsumerFactory : " + topicStatementSender);

        ObjectMapper objectMapper = springMvcJacksonConverter.getObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonDeserializer<SenderTemplate> jsonDeserializer = new JsonDeserializer(SenderTemplate.class, objectMapper);

        DefaultKafkaConsumerFactory<String, SenderTemplate> consumerFactory = new DefaultKafkaConsumerFactory<>(
            consumerConfigs(),
            new StringDeserializer(),
            jsonDeserializer);

        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SenderTemplate> kafkaListenerContainerFactory() {
        log.info("ConcurrentKafkaListenerContainerFactory : " + topicStatementSender);
        ConcurrentKafkaListenerContainerFactory<String, SenderTemplate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
