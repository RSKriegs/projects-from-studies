package edu.pja.sri.jms_app.jms_app.receiver;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import edu.pja.sri.jms_app.jms_app.config.JmsConfig;
import edu.pja.sri.jms_app.jms_app.model.FMessage;
import javax.jms.Message;


@Component
public class LogsReceiver {
    @JmsListener(destination = JmsConfig.TOPIC_FMESSAGE, containerFactory =
            "topicConnectionFactory")
    public void receiveFMessage(@Payload FMessage convertedMessage,
                                    @Headers MessageHeaders messageHeaders,
                                    Message message) {
        System.out.println("LogsReceiver.receiveFMessage, message:"+convertedMessage);
    }
}
