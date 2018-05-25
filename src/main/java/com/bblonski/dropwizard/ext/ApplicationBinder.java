package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.inject.Inject;

public abstract class ApplicationBinder<T extends Configuration> {
    @Inject
    Application<T> application;
    @Inject
    T configuration;
    @Inject
    Environment environment;
    @Inject
    EventBus eventBus;
    @Inject
    DropwizardInterceptionService interceptionService;

    public Application getApplication() {
        return application;
    }

    public T getConfiguration() {
        return configuration;
    }

    public Environment getEnvironment() {
        return environment;
    }

    abstract void postRun(Environment environment, T configuration, EventBus eventBus, DropwizardInterceptionService interceptionService);
}
