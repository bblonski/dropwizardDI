package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public abstract class Application<T extends Configuration> extends io.dropwizard.Application<T> {

    private HK2BridgeBundle bundle;
    private Bootstrap<T> bootstrap;

    @Override
    public void initialize(Bootstrap<T> bootstrap) {
        super.initialize(this.bootstrap);
        this.bootstrap = bootstrap;
        bundle = new HK2BridgeBundle(AppBinder.class);
        bootstrap.addBundle(bundle);
        ServiceLocatorUtilities.addClasses(bundle.getLocator(), MySubscriber.class);
        ServiceLocatorUtilities.bind(bundle.getLocator(),
                new AbstractBinder() {
                    @Override
                    protected void configure() {
//                        bind(MyInterceptor.class).to(MethodInterceptor.class).to(Interceptor.class).in(Singleton.class);
                        bindAsContract(MyService.class).in(Singleton.class);
                    }
                });
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
    }

    abstract void postRun(T configuration, Environment environment, ServiceLocator serviceLocator, InterceptionService interceptionService);
}
