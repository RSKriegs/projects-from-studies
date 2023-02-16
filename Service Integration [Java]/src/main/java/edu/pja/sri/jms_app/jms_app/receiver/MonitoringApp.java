package edu.pja.sri.jms_app.jms_app.receiver;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import edu.pja.sri.jms_app.jms_app.config.JmsConfig;
import edu.pja.sri.jms_app.jms_app.model.FMessage;
import edu.pja.sri.jms_app.jms_app.model.DriverMessage;
import javax.jms.Message;
@Component
class DriverReceiver {
    @JmsListener(destination = JmsConfig.TOPIC_FMESSAGE, containerFactory =
            "topicConnectionFactory")
    public void receiveFMessage(@Payload FMessage convertedMessage,
                                    @Headers MessageHeaders messageHeaders,
                                    Message message) {
        System.out.println("MonitoringApp.DriverReceiver.receiveFMessage, message:"+convertedMessage);
    }
    @JmsListener(destination = JmsConfig.QUEUE_OVER_LIMITS, containerFactory =
            "topicConnectionFactory")
    @JmsListener(destination = JmsConfig.TOPIC_OVER_LIMITS, containerFactory =
            "topicConnectionFactory")
    public void receiveDriverMessage(@Payload DriverMessage convertedMessage,
                                     @Headers MessageHeaders messageHeaders,
                                     Message message) {
        System.out.println("MonitoringApp.DriverReceiver.receiveDriverMessage, message:"+convertedMessage);
    }
}
@Component
class MechanicsReceiver {
    @JmsListener(destination = JmsConfig.TOPIC_OVER_LIMITS, containerFactory =
            "topicConnectionFactory")
    public void receiveDriverMessage(@Payload DriverMessage convertedMessage,
                                     @Headers MessageHeaders messageHeaders,
                                     Message message) {
        System.out.println("MonitoringApp.MechanicsReceiver.receiveDriverMessage, message:"+convertedMessage);
    }
}

