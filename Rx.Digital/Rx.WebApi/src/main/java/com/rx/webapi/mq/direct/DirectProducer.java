package com.rx.webapi.mq.direct;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class DirectProducer {

    @Autowired
    private AmqpTemplate rabbitmqTemplate;

    public void send(Student student) {
        rabbitmqTemplate.convertAndSend(RabbitMqConfiguration.DIRECT_QUEUE, student);
        System.out.println("send message " + student + " to DIRECT_QUEUE successfully");
    }
}
