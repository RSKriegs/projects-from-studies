/*package edu.pja.sri.jms_app.jms_app.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import edu.pja.sri.jms_app.jms_app.config.JmsConfig;
import edu.pja.sri.jms_app.jms_app.model.FMessage;
import java.time.LocalDateTime;
import java.util.Random;
@Component
@RequiredArgsConstructor
public class QueueProducer {
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
        jmsTemplate.convertAndSend(JmsConfig.QUEUE_FMESSAGE, message);
        System.out.println("QueueProducer.sendMsg- sent message:"+message);
    }
}*/