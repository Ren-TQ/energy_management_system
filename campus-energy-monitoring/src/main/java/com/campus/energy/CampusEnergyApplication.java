package com.campus.energy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智慧校园能耗监测与管理平台 - 主启动类
 * 
 * @author Campus Energy Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class CampusEnergyApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CampusEnergyApplication.class, args);
    }
}

