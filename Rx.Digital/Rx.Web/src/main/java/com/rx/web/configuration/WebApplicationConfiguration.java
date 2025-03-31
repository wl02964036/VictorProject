package com.rx.web.configuration;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mitchellbosecke.pebble.extension.Extension;
import com.rx.core.bean.SystemMenu;
import com.rx.core.dao.SystemMenuDao;
import com.rx.core.support.Snowflake;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class WebApplicationConfiguration {

    /**
     * 使用 Twitter snowflake 的演算法，產生不重複的整數序號，每台機器必須使用不同的 nodeId
     * 這邊預設從 環境變數 (environment variables) APP_NODE_ID 中設定，部署時必須注意
     *
     * @param nodeId
     * @return
     */
    @Bean
    public Snowflake snowflake(@Value("${app.node.id}") int nodeId) {
        log.info("APP_NODE_ID: {}", nodeId);
        return new Snowflake(nodeId);
    }

//    @Bean
//    public Extension pebbleExtension() {
//        log.info("register pebble extension");
//        return new PebbleExtension();
//    }
//
//    @Bean
//    public EventBus eventBus() {
//        EventBus eventBus = EventBus.builder()
//                                    .throwSubscriberException(true)
//                                    .installDefaultEventBus();
//        return eventBus;
//    }
//
//    @Bean(initMethod = "init", destroyMethod = "destroy")
//    public RegisteredSubscriber registeredSubscriber(EventBus eventBus) {
//        return new RegisteredSubscriber(eventBus);
//    }
//
//    @Bean(initMethod = "init", destroyMethod = "destroy")
//    public UploadCloudSubscriber uploadCloudSubscriber (EventBus eventBus) {
//        return new UploadCloudSubscriber(eventBus);
//    }

    @Bean
    public List<Tuple3<String, String, String>> menuPathTuples(SystemMenuDao systemMenuDao) {
        List<SystemMenu> notFolderMenus = systemMenuDao.findByPathIsNotFolder();
        return Seq.seq(notFolderMenus)
                  .map((menu) -> Tuple.tuple(
                       menu.getUuid(),
                       menu.getPath(),
                       menu.getTitle())
                  )
                  .collect(Collectors.toList());
    }

}
