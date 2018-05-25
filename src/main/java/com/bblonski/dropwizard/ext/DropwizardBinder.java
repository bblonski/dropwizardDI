package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public abstract class DropwizardBinder<T extends Configuration> {

    public abstract void postRun(Environment environment, T configuration, EventBus eventBus, DropwizardInterceptionService interceptionService);
}
