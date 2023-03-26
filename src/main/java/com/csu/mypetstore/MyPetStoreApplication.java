package com.csu.mypetstore;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan(basePackages = {"com.csu.mypetstore.mapper"})
public class MyPetStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPetStoreApplication.class, args);
    }

}
