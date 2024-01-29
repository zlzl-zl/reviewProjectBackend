package com.hmdp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.sql.Connection;

/**
 * @Author: zl
 * @Date: 2023-12-31 11:15
 */
@Slf4j
@SpringBootConfiguration
//@Component
public class DataSourseConfig {

    @Component
    public  class CreateDataSoursePool implements ApplicationRunner, Ordered {
        @Resource
        DataSource dataSource;
        @Override
        public void run(ApplicationArguments args) throws Exception {  //hikari在第一次获得连接，才会初始化，配置在程序启动获得连接，初始化数据库连接池
            log.info("dataSource: {}", dataSource);
            Connection connection = dataSource.getConnection();
            log.info("connection: {}", connection);
        }

        @Override
        public int getOrder() {
            return 0;
        }
    }
}
