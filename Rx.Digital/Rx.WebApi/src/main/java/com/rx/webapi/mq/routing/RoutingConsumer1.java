package com.rx.webapi.mq.routing;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class RoutingConsumer1 {

	@RabbitListener(queues = RabbitMqConfiguration.ROUTING_QUEUE_1)
	public void listen(Student student) {
        System.out.println("receive message from ROUTING_QUEUE_1: " + student);
	}

}
