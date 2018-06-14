package com.bblonski.dropwizard.ext;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.lifecycle.JettyManaged;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.messaging.MessageReceiver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class AutoRegisterFeature implements Feature {

    @Inject
    private Environment environment;

    @Inject
    private ServiceLocator locator;

    @Inject
    private IterableProvider<HealthCheck> healthCheckProvider;

    @Inject
    private IterableProvider<Task> tasksProvider;

    @Inject
    private IterableProvider<Managed> managedProvider;

    @Inject
    private IterableProvider<LifeCycle> lifeCyclesProvider;

    @Override
    public boolean configure(FeatureContext context) {
        healthCheckProvider.forEach(healthCheck -> {
            String name;
            if (healthCheck.getClass().isAnnotationPresent(Named.class)) {
                name = healthCheck.getClass().getAnnotation(Named.class).value();
            } else {
                name = healthCheck.getClass().getName();
            }
            environment.healthChecks().register(name, healthCheck);
        });
        locator.getAllServiceHandles(MessageReceiver.class).forEach(ServiceHandle::getService);
        tasksProvider.forEach(task -> environment.admin().addTask(task));
        managedProvider.forEach(managed -> environment.getApplicationContext().addManaged(new JettyManaged(managed)));
        lifeCyclesProvider.forEach(managed -> environment.getApplicationContext().addManaged(managed));
        return true;
    }
}
