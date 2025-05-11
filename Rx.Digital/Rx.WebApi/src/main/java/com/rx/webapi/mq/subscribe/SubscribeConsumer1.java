package com.rx.webapi.mq.subscribe;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class SubscribeConsumer1 {

	@RabbitListener(queues = RabbitMqConfiguration.SUBSCRIBE_QUEUE_1)
	public void listen(Student student) {
		System.out.println("receive message from SUBSCRIBE_QUEUE_1: " + student);
	}

}
