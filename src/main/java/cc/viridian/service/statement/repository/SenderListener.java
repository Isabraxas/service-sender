package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.service.ProcessJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SenderListener {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private ProcessJobService processJobService;

    @Autowired
    public SenderListener(ProcessJobService processJobService) {
        this.processJobService = processJobService;
    }

    @KafkaListener(topics = "${topic.statement.sender}")
    public void receive(@Payload final SenderTemplate data,
                        @Headers final MessageHeaders headers) {
        log.info("received message from topic: " + headers.get("kafka_receivedTopic")
                     + " key:" + headers.get("kafka_receivedMessageKey")
                     + " partition:" + headers.get("kafka_receivedPartitionId")
                     + " offset:" + headers.get("kafka_offset"));

        processJobService.process(data);
    }
}
