package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jboss.weld.environment.servlet.BeanManagerResourceBindingListener;
import org.jboss.weld.environment.servlet.Listener;

import javax.inject.Singleton;
import java.util.Objects;

public abstract class Application<T extends Configuration> extends io.dropwizard.Application<T> {

    private HK2BridgeBundle bundle;
    private Bootstrap<T> bootstrap;

    @Override
    public void initialize(Bootstrap<T> bootstrap) {
        super.initialize(this.bootstrap);
        this.bootstrap = bootstrap;
//        bundle = new HK2BridgeBundle();
//        bootstrap.addBundle(bundle);
        bootstrap.addBundle(new Bundle() {
            @Override
            public void initialize(Bootstrap<?> bootstrap) {

            }

            @Override
            public void run(Environment environment) {
                environment.getApplicationContext().addEventListener(new BeanManagerResourceBindingListener());
                environment.getApplicationContext().addEventListener(new Listener());
            }
        });
//        HK2Bundle.addTo(bootstrap);
//        ServiceLocatorUtilities.bind(bundle.getLocator(),
//                new AbstractBinder() {
//                    @Override
//                    protected void configure() {
//                        bind(MyInterceptor.class).to(MethodInterceptor.class).to(Interceptor.class).in(Singleton.class);
//                        bindAsContract(MyService.class).in(Singleton.class);
//                        bind(DropwizardInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
//                        bind(EventBus.class).to(EventBus.class).in(Singleton.class);
//                    }
//                });
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new DefaultDropwizardBinder<>(environment, bootstrap, configuration));
//        environment.jersey().register(DefaultTopicDistributionService.class);
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
