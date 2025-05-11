package com.rx.webapi.mq.worker;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;

@Component
public class WorkerProducer {

    @Autowired
    private AmqpTemplate rabbitmqTemplate;

	public void send(Student student) {
        rabbitmqTemplate.convertAndSend(RabbitMqConfiguration.WORKER_QUEUE, student);
        System.out.println("send message " + student + " to WORKER_QUEUE successfully");
    }
}
