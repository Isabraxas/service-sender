package cc.viridian.service.statement.config;

import cc.viridian.service.statement.model.UpdateJobTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class UpdateJobProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${topic.statement.update}")
    private String topicStatementUpdate;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return props;
    }

    private ProducerFactory<String, UpdateJobTemplate> producerFactory() {
        DefaultKafkaProducerFactory<String, UpdateJobTemplate> producerFactory =
             new DefaultKafkaProducerFactory<>(producerConfigs(),
                                               new StringSerializer(),
                                               new JsonSerializer<UpdateJobTemplate>(objectMapper)
                                               );
         return producerFactory;
    }

    @Bean(name = "UpdateJobTemplate")
    public KafkaTemplate<String, UpdateJobTemplate> kafkaTemplate() {
        KafkaTemplate<String, UpdateJobTemplate> template = new KafkaTemplate<>(producerFactory());
        template.setDefaultTopic(topicStatementUpdate);
        log.info("creating kafka producer for topic: " + topicStatementUpdate);
        return template;
    }

}
