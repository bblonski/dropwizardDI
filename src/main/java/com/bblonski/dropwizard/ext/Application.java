package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.events.internal.DefaultTopicDistributionService;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.inject.Inject;

public class Application extends io.dropwizard.Application<io.dropwizard.Configuration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Inject
    MyHealthCheck myHealthCheck;
    @Inject
    DropwizardInterceptionService interceptionService;
    @Inject
    MyInterceptor interceptor1;
    @Inject
    MyInterceptor2 interceptor2;

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        final ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        ServiceLocatorUtilities.addClasses(serviceLocator, DefaultTopicDistributionService.class, MyResource.class);
        ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
            @Override
            protected void configure() {
                addActiveDescriptor(DropwizardInterceptionService.class);
                addActiveDescriptor(MyHealthCheck.class);
                addActiveDescriptor(MyService.class);
                addActiveDescriptor(MyInterceptor.class);
                addActiveDescriptor(MyInterceptor2.class);
                addActiveDescriptor(MySubscriber.class);
                addActiveDescriptor(MyResource.class);
                bind(new EventBus()).to(EventBus.class);
            }
        });
        serviceLocator.inject(this);
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
            }

            @Override
            public void stop() throws Exception {
            }
        });
        environment.getApplicationContext().setAttribute(ServletProperties.SERVICE_LOCATOR, serviceLocator);
        environment.getAdminContext().setAttribute(ServletProperties.SERVICE_LOCATOR, serviceLocator);
//        environment.jersey().packages("com.bblonski.dropwizard.ext");
        environment.jersey().register(MyResource.class);
        environment.healthChecks().register("Hello", myHealthCheck);
        interceptionService.addMethodInterceptor(x -> x.isAnnotationPresent(Timed.class),
                interceptor1);
        interceptionService.addMethodInterceptor(x -> x.isAnnotationPresent(Timed.class),
                interceptor2);
        interceptionService.addMethodInterceptor(x -> true, x -> {
            System.out.println("Calling " + x.getMethod().getName());
            return x.proceed();
        });
        interceptionService.addConstructorInterceptor(x -> true, x -> {
            System.out.println("Building " + x.getConstructor().getName());
            return x.proceed();
        });
    }
}
