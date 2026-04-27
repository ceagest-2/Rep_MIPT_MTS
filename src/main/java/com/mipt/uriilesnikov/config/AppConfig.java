package com.mipt.uriilesnikov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import com.mipt.uriilesnikov.scope.PrototypeScopedBean;
import com.mipt.uriilesnikov.scope.RequestScopedBean;

/**
 * A configuration class for manually defining beans and configuring scopes.
 */
@Configuration
public class AppConfig {

    /**
     * Bean with the request scope.
     */
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST)
    public RequestScopedBean requestScopedBean() {
        return new RequestScopedBean();
    }

    /**
     * Bean with the prototype scope.
     */
    @Bean
    @Scope("prototype")
    public PrototypeScopedBean prototypeScopedBean() {
        return new PrototypeScopedBean();
    }
}
