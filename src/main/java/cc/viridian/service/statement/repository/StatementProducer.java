package cc.viridian.service.statement.repository;

import cc.viridian.provider.model.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatementProducer {

    @Autowired
    private KafkaTemplate<String, Statement> kafkaTemplate;

    @Autowired
    public StatementProducer(KafkaTemplate<String, Statement> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String messageKey, Statement data){
        log.debug("sending Statement for account  "+ data.getHeader().getAccountCode() + " with key " + messageKey);

        Message<Statement> message = MessageBuilder
            .withPayload(data)
            .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
            .build();

        kafkaTemplate.send(message);
    }
}
