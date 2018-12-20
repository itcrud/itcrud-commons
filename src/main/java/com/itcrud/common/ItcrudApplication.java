package com.itcrud.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Author: Joker
 * @Desc:
 * @Date: 2018/12/7 13:57
 * @Modified By:
 * @Project_name: itcrud-commons
 * @Version 1.0
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class ItcrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItcrudApplication.class);
    }
}
