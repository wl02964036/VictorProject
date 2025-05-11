package com.rx.webapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;
import com.rx.webapi.mq.direct.DirectProducer;
import com.rx.webapi.mq.routing.RoutingProducer;
import com.rx.webapi.mq.subscribe.SubscribeProducer;
import com.rx.webapi.mq.worker.WorkerProducer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MqController {

	@Autowired
	private DirectProducer directProducer;

	@Autowired
	private WorkerProducer workerProducer;

	@Autowired
	private SubscribeProducer subscribeProducer;

	@Autowired
	private RoutingProducer routingProducer;

	@GetMapping("/direct/send")
	public String sendDirect() {
		directProducer.send(new Student(1, "John"));
		return "success";
	}

	@GetMapping("/worker/send")
	public String sendWorker() {
		workerProducer.send(new Student(1, "John"));
		workerProducer.send(new Student(2, "Amy"));
		workerProducer.send(new Student(3, "Bob"));
		workerProducer.send(new Student(4, "Mike"));
		workerProducer.send(new Student(5, "Sharon"));
		return "send 5 messages to queue successfully";
	}

	@GetMapping("/subscribe/send")
	public String sendSubscribe() {
		subscribeProducer.send(new Student(1, "John"));
		return "success";
	}

	@GetMapping("/routing/send")
	public String sendRouting() {
		routingProducer.send(new Student(1, "John", RabbitMqConfiguration.ROUTING_KEY_1));
		routingProducer.send(new Student(2, "Amy", RabbitMqConfiguration.ROUTING_KEY_2));
		routingProducer.send(new Student(3, "Bob", RabbitMqConfiguration.ROUTING_KEY_1));
		routingProducer.send(new Student(4, "Mike", RabbitMqConfiguration.ROUTING_KEY_2));
		routingProducer.send(new Student(5, "Sharon", RabbitMqConfiguration.ROUTING_KEY_2));
		return "success";
	}
}
