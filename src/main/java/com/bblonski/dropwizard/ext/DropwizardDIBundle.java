package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Singleton;
import java.util.Objects;

public class DropwizardDIBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final Class<? extends DropwizardPostRun> initializer;
    private Bootstrap<?> bootstrap;
    private DropwizardPostRun<T> initailizerInstance;
    private Class<? extends DropwizardInterceptionService> interceptionService;

    public DropwizardDIBundle(Class<? extends DropwizardPostRun<T>> initializer) {
        this.initializer = initializer;
    }

    public DropwizardDIBundle(DropwizardPostRun<T> initializer) {
        this.initailizerInstance = initializer;
        this.initializer = initializer.getClass();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setInterceptionService(Class<? extends DropwizardInterceptionService> interceptionService) {
        this.interceptionService = interceptionService;
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new DefaultDropwizardBinder<>(environment, bootstrap, configuration));
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(initializer);
                bind(EventBus.class).to(EventBus.class).in(Singleton.class);
                if (interceptionService != null) {
                    bind(interceptionService).to(InterceptionService.class).in(Singleton.class);
                }
            }

        });
        environment.jersey().register(new ApplicationEventListener() {
            @Override
            public void onEvent(ApplicationEvent event) {
                if (event.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED) {
                    final ServiceLocator serviceLocator = ((ServletContainer) Objects
                            .requireNonNull(environment.getJerseyServletContainer()))
                            .getApplicationHandler()
                            .getServiceLocator();
                    final EventBus eventBus = serviceLocator.getService(EventBus.class);
                    if (initailizerInstance != null) {
                        serviceLocator.inject(initailizerInstance);
                    } else {
                        initailizerInstance = serviceLocator.getService(initializer);
                    }
                    initailizerInstance.postRun(configuration, environment, eventBus);
                }
            }

            @Override
            public RequestEventListener onRequest(RequestEvent requestEvent) {
                return null;
            }
        });

    }
}
