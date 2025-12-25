package com.energy.management.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

// Pattern: Factory - 数据生成器工厂类
@Component
public class DataGeneratorFactory {

    private final Map<String, DataGenerator> generatorMap;

    @Autowired
    public DataGeneratorFactory(Map<String, DataGenerator> generatorMap) {
        this.generatorMap = generatorMap;
    }

    public DataGenerator getGenerator(String type) {
        String beanName = type.toLowerCase() + "Generator";
        DataGenerator generator = generatorMap.get(beanName);

        if (generator == null) {
            throw new IllegalArgumentException("不支持的数据生成器类型: " + type);
        }

        return generator;
    }
}