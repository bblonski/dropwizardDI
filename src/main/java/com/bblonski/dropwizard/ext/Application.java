package com.bblonski.dropwizard.ext;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Objects;

public abstract class Application<T extends Configuration> extends io.dropwizard.Application<T> {

    private Bootstrap<T> bootstrap;

    @Override
    public void initialize(Bootstrap<T> bootstrap) {
        super.initialize(this.bootstrap);
        this.bootstrap = bootstrap;
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new DefaultDropwizardBinder<>(environment, bootstrap, configuration));
        environment.jersey().register(new ApplicationEventListener() {
            @Override
            public void onEvent(ApplicationEvent event) {
                if (event.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED) {
                    final ServiceLocator serviceLocator = ((ServletContainer) Objects
                            .requireNonNull(environment.getJerseyServletContainer()))
                            .getApplicationHandler()
                            .getServiceLocator();
                    final InterceptionService interceptionService = serviceLocator.getService(InterceptionService.class);
                    postRun(configuration, environment, serviceLocator, interceptionService);
                }
            }

            @Override
            public RequestEventListener onRequest(RequestEvent requestEvent) {
                return null;
            }
        });
    }

    abstract void postRun(T configuration, Environment environment, ServiceLocator serviceLocator, InterceptionService interceptionService);
}
