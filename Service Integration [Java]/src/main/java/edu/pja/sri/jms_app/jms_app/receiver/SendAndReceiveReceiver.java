package edu.pja.sri.jms_app.jms_app.receiver;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import edu.pja.sri.jms_app.jms_app.config.JmsConfig;
import edu.pja.sri.jms_app.jms_app.model.DriverMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
@Component
@RequiredArgsConstructor
public class SendAndReceiveReceiver {
    private final JmsTemplate jmsTemplate;
    @JmsListener(destination = JmsConfig.QUEUE_SEND_AND_RECEIVE)
    public void receiveAndRespond(@Payload DriverMessage convertedMessage,
                                  @Headers MessageHeaders headers,
                                  Message message) throws JMSException {
        System.out.println("SendAndReceiveReceiver.receiveAndRespond message: "+convertedMessage);
                Destination replyTo = message.getJMSReplyTo();
        Random rand = new Random();
        int int_random=rand.nextInt(2);
        String message_to_send;
        if (int_random % 2==0) {
            message_to_send = "OK, drive into pit stop";
        }
        else {
            message_to_send = "No, continue the race";
        } //for testing & simulation purposes
        DriverMessage msg = DriverMessage.builder()
                .id(DriverMessage.nextId())
                .createdAt(LocalDateTime.now())
                .message(message_to_send)
                .build();
        jmsTemplate.convertAndSend(replyTo, msg);
    }
}