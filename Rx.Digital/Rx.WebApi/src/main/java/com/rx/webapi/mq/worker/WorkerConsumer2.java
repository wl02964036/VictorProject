package com.rx.webapi.mq.worker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class WorkerConsumer2 {

	@RabbitListener(queues = RabbitMqConfiguration.WORKER_QUEUE)
	public void listen(Student student) {
		System.out.println("WorkerConsumer2 receive message from WORKER_QUEUE: " + student);
	}

}
