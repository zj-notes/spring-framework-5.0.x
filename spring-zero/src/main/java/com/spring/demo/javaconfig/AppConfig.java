package com.spring.demo.javaconfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("com.spring.demo")
public class AppConfig {

}
