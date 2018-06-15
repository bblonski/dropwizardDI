package com.bblonski.dropwizard.ext;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.gf.cdi.internal.CdiComponentProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;

@ApplicationScoped
class ApplicationTest extends Application<Configuration> {

    @Inject
    Event<TestEvent> event;
    @Inject
    MySubscriber mySubscriber;
    @Inject
    @Any
    Instance<HealthCheck> healthCheckProvider;

    private Bootstrap<Configuration> bootstrap;

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        this.bootstrap = bootstrap;
    }

    public static void main(String[] args) throws Exception {
//        final Weld weld = new Weld();
//        WeldContainer container = weld.initialize();
//        container.select(ApplicationTest.class).get().run(args);
//
        final SeContainer se = SeContainerInitializer.newInstance()
                .disableDiscovery()
                .addExtensions(WeldDropwizardExtension.class, CdiComponentProvider.class)
                .addPackages(ApplicationTest.class.getPackage())
                .enableInterceptors(MyInterceptor.class)
                .initialize();
        se.select(ApplicationTest.class).get().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(MyResource.class);
        healthCheckProvider.forEach(x -> environment.healthChecks().register(x.getClass().getName(), x));
    }

}