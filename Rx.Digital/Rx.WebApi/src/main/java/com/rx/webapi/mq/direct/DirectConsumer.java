package com.rx.webapi.mq.direct;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rx.core.bean.Student;
import com.rx.webapi.configuration.RabbitMqConfiguration;
import com.rx.webapi.configuration.TomcatConfiguration;

@Component
public class DirectConsumer {

    @RabbitListener(queues = RabbitMqConfiguration.DIRECT_QUEUE)
    public void listen(Student student) {
        System.out.println("receive message from DIRECT_QUEUE: " + student);
    }

}
