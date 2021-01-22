package com.gs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author ：YoungSun
 * @date ：Created in 2021/1/14 8:57
 * @modified By：
 */
@SpringBootApplication
@MapperScan("com.gs.dao")
@EnableScheduling
@EnableSwagger2
@EnableAsync
public class DigitalTwinDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(DigitalTwinDataApplication.class, args);
    }
}
