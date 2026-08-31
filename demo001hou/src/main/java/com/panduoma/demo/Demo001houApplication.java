package com.panduoma.demo;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.panduoma.demo.mapper")
public class Demo001houApplication {

    public static void main(String[] args) {

        SpringApplication.run(Demo001houApplication.class, args);
        System.out.println("项目启动成功:"+SaManager.getConfig());
    }

}
