package edu.pja.sri.jms_app.jms_app.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import edu.pja.sri.jms_app.jms_app.config.JmsConfig;
import edu.pja.sri.jms_app.jms_app.model.DriverMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.time.LocalDateTime;
import java.util.Random;
@Component
@RequiredArgsConstructor
public class SendAndReceiveProducer {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    @Scheduled(fixedRate = 12000)
    public void sendAndReceive() throws JMSException, JsonProcessingException {
        Random rand = new Random();
        int int_random=rand.nextInt(6); //for testing and simulation purposes
        if (int_random % 6==0) {
            DriverMessage message = DriverMessage.builder()
                    .id(DriverMessage.nextId())
                    .createdAt(LocalDateTime.now())
                    .message("Need to drop into pit stop")
                    .build();
            TextMessage responseMessage = (TextMessage) jmsTemplate.sendAndReceive(
                    JmsConfig.QUEUE_SEND_AND_RECEIVE, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            TextMessage plainMessage = session.createTextMessage();
                            try {
                                plainMessage.setText(objectMapper.writeValueAsString(message));
                                plainMessage.setStringProperty("_type",
                                        DriverMessage.class.getName());
                                return plainMessage;
                            } catch (JsonProcessingException e) {
                                throw new JMSException("conversion to json failed: " +
                                        e.getMessage());
                            }
                        }
                    });
            String responseText = responseMessage.getText();
            DriverMessage responseConverted = objectMapper.readValue(responseText,
                    DriverMessage.class);
            System.out.println("SendAndReceiveProducer.sendAndReceive got response: "
                    +responseText+"\n\tconvertedMessage: "+responseConverted);
        }
    }
}
