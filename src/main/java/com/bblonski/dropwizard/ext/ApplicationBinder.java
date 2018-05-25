package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.inject.Inject;

public abstract class ApplicationBinder<T extends Configuration> {

    abstract void postRun(Environment environment, T configuration, EventBus eventBus, DropwizardInterceptionService interceptionService);
}
