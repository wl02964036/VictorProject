package com.rx.webapi.configuration;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rx.core.bean.SystemMenu;
import com.rx.core.dao.SystemMenuDao;

/**
 * 其他雜七雜八的 bean 設定
 */
@Slf4j
@Configuration
public class WebApplicationConfiguration {

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @Bean
    public String profileName() {
        String profile = "";
        String[] profiles = activeProfiles.split(",");
        if (profiles.length > 0) {
            profile = profiles[0];
        }
        log.info("profileName : '{}'", profile);
        return profile;
    }

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