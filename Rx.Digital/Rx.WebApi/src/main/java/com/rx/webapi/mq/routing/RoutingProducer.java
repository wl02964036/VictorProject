package com.rx.webapi.mq.routing;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class RoutingProducer {

    @Autowired
    private AmqpTemplate rabbitmqTemplate;

	public void send(Student student) {
        String routingKey = student.getClassroom();
        rabbitmqTemplate.convertAndSend(RabbitMqConfiguration.ROUTING_EXCHANGE, routingKey, student);
        System.out.println("send message " + student + " to ROUTING_EXCHANGE successfully");
    }
}
