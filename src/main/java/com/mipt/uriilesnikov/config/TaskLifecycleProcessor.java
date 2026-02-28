package com.mipt.uriilesnikov.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Bean Lifecycle Processor.
 * Logs the creation and initialization of the TaskService and TaskRepository beans.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof org.springframework.stereotype.Service || bean instanceof org.springframework.stereotype.Repository) {
            System.out.println(" [Lifecycle] Creating bean: " + beanName + " of type " + bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof org.springframework.stereotype.Service || bean instanceof org.springframework.stereotype.Repository) {
            System.out.println(" [Lifecycle] Initialized bean: " + beanName + " of type " + bean.getClass().getSimpleName());
        }
        return bean;
    }
}