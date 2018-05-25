package com.bblonski.test;

import com.bblonski.dropwizard.ext.DropwizardPostRun;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.EventBus;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

public class AppInitializer extends DropwizardPostRun<Configuration> {

    @Inject
    MySubscriber mySubscriber;
    @Inject
    ObjectMapper mapper;
    @Inject
    ApplicationTest applicationTest;
    @Inject
    Application application;
    @Inject
    JerseyEnvironment jerseyEnvironment;
    @Inject
    AdminEnvironment adminEnvironment;

    @Override
    public void postRun(Configuration configuration, Environment environment, EventBus eventBus) {
        environment.healthChecks().register("test", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
        final ServiceLocator serviceLocator = getServiceLocator();
        eventBus.register(mySubscriber);
    }
}
