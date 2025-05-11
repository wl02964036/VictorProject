package com.rx.webapi.mq.routing;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class RoutingConsumer2 {

	@RabbitListener(queues = RabbitMqConfiguration.ROUTING_QUEUE_2)
	public void listen(Student student) {
		System.out.println("receive message from ROUTING_QUEUE_2: " + student);
	}

}