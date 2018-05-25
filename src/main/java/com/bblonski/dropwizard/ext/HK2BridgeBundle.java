package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Singleton;
import java.util.Objects;

public class HK2BridgeBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final Class<? extends ApplicationBinder> binder;
    private Bootstrap<?> bootstrap;

    public HK2BridgeBundle(Class<? extends ApplicationBinder<T>> binder) {
        this.binder = binder;
    }

    public HK2BridgeBundle(ApplicationBinder<T> binder) {
        this.binder = binder.getClass();
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new DefaultDropwizardBinder<>(environment, bootstrap, configuration));
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(DropwizardInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
                bindAsContract(binder);
                bind(EventBus.class).to(EventBus.class).in(Singleton.class);
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
                    ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
                    final EventBus eventBus = serviceLocator.getService(EventBus.class);
                    final DropwizardInterceptionService interceptionService = (DropwizardInterceptionService)serviceLocator.getService(InterceptionService.class);
                    serviceLocator.getService(binder).postRun(environment, configuration, eventBus, interceptionService);
                }
            }

            @Override
            public RequestEventListener onRequest(RequestEvent requestEvent) {
                return null;
            }
        });

    }
}
