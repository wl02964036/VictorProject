package com.rx.webapi.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RabbitMqConfiguration {

	// Direct 模式
	public static final String DIRECT_QUEUE = "DIRECT_QUEUE";
	public static final String DIRECT_EXCHANGE = "DIRECT_EXCHANGE";
	public static final String DIRECT_ROUTING_KEY = "DIRECT_ROUTING";

	@Bean
	public Queue directQueue() {
		return new Queue(DIRECT_QUEUE);
	}

	@Bean
	public DirectExchange directExchange() {
		return new DirectExchange(DIRECT_EXCHANGE);
	}

	@Bean
	public Binding directBinding() {
		return BindingBuilder.bind(directQueue()).to(directExchange()).with(DIRECT_ROUTING_KEY);
	}

	// Worker 模式（多消費者）
	public static final String WORKER_QUEUE = "WORKER_QUEUE";

	@Bean
	public Queue workerQueue() {
		return new Queue(WORKER_QUEUE);
	}

	// Publish/Subscribe 模式（Fanout Exchange）
	public static final String SUBSCRIBE_EXCHANGE = "SUBSCRIBE_EXCHANGE";
	public static final String SUBSCRIBE_QUEUE_1 = "SUBSCRIBE_QUEUE_1";
	public static final String SUBSCRIBE_QUEUE_2 = "SUBSCRIBE_QUEUE_2";
	public static final String SUBSCRIBE_QUEUE_3 = "SUBSCRIBE_QUEUE_3";

	@Bean
	public FanoutExchange fanoutExchange() {
		return new FanoutExchange(SUBSCRIBE_EXCHANGE);
	}

	@Bean
	public Queue subscribeQueue1() {
		return new Queue(SUBSCRIBE_QUEUE_1);
	}

	@Bean
	public Queue subscribeQueue2() {
		return new Queue(SUBSCRIBE_QUEUE_2);
	}

	@Bean
	public Queue subscribeQueue3() {
		return new Queue(SUBSCRIBE_QUEUE_3);
	}

	@Bean
	public Binding bindingQueue1() {
		return BindingBuilder.bind(subscribeQueue1()).to(fanoutExchange());
	}

	@Bean
	public Binding bindingQueue2() {
		return BindingBuilder.bind(subscribeQueue2()).to(fanoutExchange());
	}

	@Bean
	public Binding bindingQueue3() {
		return BindingBuilder.bind(subscribeQueue3()).to(fanoutExchange());
	}

	// Routing 模式 (Direct Exchange with multiple routing keys)
	public static final String ROUTING_EXCHANGE = "ROUTING_EXCHANGE";
	public static final String ROUTING_QUEUE_1 = "ROUTING_QUEUE_1";
	public static final String ROUTING_QUEUE_2 = "ROUTING_QUEUE_2";
	public static final String ROUTING_KEY_1 = "ROOM_1";
	public static final String ROUTING_KEY_2 = "ROOM_2";

	@Bean
	public DirectExchange routingExchange() {
		return new DirectExchange(ROUTING_EXCHANGE);
	}

	@Bean
	public Queue routingQueue1() {
		return new Queue(ROUTING_QUEUE_1);
	}

	@Bean
	public Queue routingQueue2() {
		return new Queue(ROUTING_QUEUE_2);
	}

	@Bean
	public Binding routingBinding1() {
		return BindingBuilder.bind(routingQueue1()).to(routingExchange()).with(ROUTING_KEY_1);
	}

	@Bean
	public Binding routingBinding2() {
		return BindingBuilder.bind(routingQueue2()).to(routingExchange()).with(ROUTING_KEY_2);
	}

}