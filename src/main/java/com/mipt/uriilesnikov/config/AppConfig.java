package com.mipt.uriilesnikov.config;

import com.mipt.uriilesnikov.repository.StubTaskRepository;
import com.mipt.uriilesnikov.repository.TaskRepository;
import com.mipt.uriilesnikov.scope.PrototypeScopedBean;
import com.mipt.uriilesnikov.scope.RequestScopedBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

/**
 * A configuration class for manually defining beans and configuring scopes.
 */
@Configuration
public class AppConfig {

    /**
     * Bean repository stubs.
     */
    @Bean
    public TaskRepository stubTaskRepository() {
        return new StubTaskRepository();
    }

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
