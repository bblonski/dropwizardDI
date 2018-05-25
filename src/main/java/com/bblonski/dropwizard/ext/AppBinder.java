package com.bblonski.dropwizard.ext;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.inject.Inject;

public class AppBinder extends ApplicationBinder<Configuration> {

    @Inject
    MySubscriber mySubscriber;

    @Override
    void postRun(Environment environment, Configuration configuration, EventBus eventBus, DropwizardInterceptionService interceptionService) {
        getEnvironment().healthChecks().register("test", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
        eventBus.register(mySubscriber);
    }
}
