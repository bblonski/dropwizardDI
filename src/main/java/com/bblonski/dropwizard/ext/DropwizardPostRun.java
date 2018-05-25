package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;

public abstract class DropwizardPostRun<T extends Configuration> {
    @Inject
    ServiceLocator serviceLocator;

    public ServiceLocator getServiceLocator() {
        return serviceLocator;
    }

    public abstract void postRun(T configuration, Environment environment, EventBus eventBus);
}
