package com.aicommerce.common.config;

import java.util.Map;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;

/**
 * Provides a TaskDecorator that propagates MDC context (e.g. traceId) to asynchronous
 * task executions so logs keep the same trace information.
 */
@Configuration
public class MdcTaskDecoratorConfiguration {

    @Bean
    public TaskDecorator mdcTaskDecorator() {
        return runnable -> {
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> previous = MDC.getCopyOfContextMap();
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap);
                    }
                    else {
                        MDC.clear();
                    }
                    runnable.run();
                }
                finally {
                    if (previous != null) {
                        MDC.setContextMap(previous);
                    }
                    else {
                        MDC.clear();
                    }
                }
            };
        };
    }

}
