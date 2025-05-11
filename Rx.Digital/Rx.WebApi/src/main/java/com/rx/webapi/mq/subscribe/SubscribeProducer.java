package com.rx.webapi.mq.subscribe;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class SubscribeProducer {

	@Autowired
	private AmqpTemplate rabbitmqTemplate;

	public void send(Student student) {
		rabbitmqTemplate.convertAndSend(RabbitMqConfiguration.SUBSCRIBE_EXCHANGE, "", student);
		System.out.println("send message " + student + " to SUBSCRIBE_EXCHANGE successfully");
	}
}
