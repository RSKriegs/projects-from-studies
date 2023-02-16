package edu.pja.sri.jms_app.jms_app.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import edu.pja.sri.jms_app.jms_app.config.JmsConfig;
import edu.pja.sri.jms_app.jms_app.model.FMessage;
import edu.pja.sri.jms_app.jms_app.model.DriverMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class TopicProducer {
    private final JmsTemplate jmsTemplate;
    @Scheduled(fixedRate = 15000)
    public void sendMsg() {
        Random rand = new Random();
        float upperbound = 50;
        float upperbound_pressure = 2000;
        float float_random=rand.nextFloat(upperbound);
        float float_random_tiresPressure=rand.nextFloat(upperbound_pressure);
        float float_random_oilPressure=rand.nextFloat(upperbound_pressure);
        FMessage message = FMessage.builder()
                .id(FMessage.nextId())
                .createdAt(LocalDateTime.now())
                .engineTemperature(float_random)
                .tiresPressure(float_random_tiresPressure)
                .oilPressure(float_random_oilPressure)
                .build();
        jmsTemplate.convertAndSend(JmsConfig.TOPIC_FMESSAGE, message);
        System.out.println("TopicProducer.sendMsg- sent message:"+message);
        if (float_random>0.8*upperbound || float_random_tiresPressure>0.8*upperbound_pressure || float_random_oilPressure>0.8*upperbound_pressure) {
            DriverMessage message_1 = DriverMessage.builder()
                    .id(DriverMessage.nextId())
                    .createdAt(LocalDateTime.now())
                    .message("One of parameters is going over an acceptable limit")
                    .build();
            jmsTemplate.convertAndSend(JmsConfig.QUEUE_OVER_LIMITS, message_1);
            System.out.println("TopicProducer.sendMsg- sent message:"+message_1);
        }
        if (float_random>0.9*upperbound || float_random_tiresPressure>0.9*upperbound_pressure || float_random_oilPressure>0.9*upperbound_pressure) {
            DriverMessage message_2 = DriverMessage.builder()
                    .id(DriverMessage.nextId())
                    .createdAt(LocalDateTime.now())
                    .message("The vehicle is damaged. Drive into pit stop ASAP")
                    .build();
            jmsTemplate.convertAndSend(JmsConfig.TOPIC_OVER_LIMITS, message_2);
            System.out.println("TopicProducer.sendMsg- sent message:"+message_2);
        }
    }
}
