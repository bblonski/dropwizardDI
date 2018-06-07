package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Inject;
import javax.inject.Singleton;

public class Application extends io.dropwizard.Application<io.dropwizard.Configuration> {

    private HK2BridgeBundle<io.dropwizard.Configuration> bundle;
    private Bootstrap<Configuration> bootstrap;

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Inject
    MyHealthCheck myHealthCheck;
    @Inject
    MySubscriber mySubscriber;
    @Inject
    DropwizardInterceptionService interceptionService;
    @Inject
    MyInterceptor interceptor1;
    @Inject
    MyInterceptor2 interceptor2;

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(this.bootstrap);
        this.bootstrap = bootstrap;
        bundle = HK2BridgeBundle.create()
                .withDefaultInterceptionService()
                .build();
        bootstrap.addBundle(bundle);
        ExtrasUtilities.enableTopicDistribution(this.bundle.getLocator());
        ServiceLocatorUtilities.addClasses(this.bundle.getLocator(), MyResource.class);
        ServiceLocatorUtilities.addClasses(this.bundle.getLocator(), MyHealthCheck.class);
        ServiceLocatorUtilities.addClasses(this.bundle.getLocator(), MySubscriber.class);
        ServiceLocatorUtilities.addClasses(this.bundle.getLocator(), MyService.class);
        ServiceLocatorUtilities.addClasses(this.bundle.getLocator(), MyInterceptor.class);
        ServiceLocatorUtilities.addClasses(this.bundle.getLocator(), MyInterceptor2.class);
        ServiceLocatorUtilities.bind(this.bundle.getLocator(), new AbstractBinder() {
            @Override
            protected void configure() {
                bind(DropwizardInterceptionService.class).to(InterceptionService.class).to(DropwizardInterceptionService.class).in(Singleton.class);
            }
        });
        this.bundle.getLocator().inject(this);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(DropwizardInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
            }
        });
        interceptionService.addMethodInterceptor(x -> true, x -> {
            System.out.println("Calling " + x.getMethod().getName());
            return x.proceed();
        });
        interceptionService.addConstructorInterceptor(x -> true, x -> {
            System.out.println("Building " + x.getConstructor().getName());
            return x.proceed();
        });
        environment.jersey().packages("com.bblonski.dropwizard.ext");
        environment.healthChecks().register("Hello", myHealthCheck);
        environment.jersey().register(new ApplicationEventListener() {
            @Override
            public void onEvent(ApplicationEvent event) {
                if (event.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED) {
                    final ServiceLocator serviceLocator = ((ServletContainer) environment.getJerseyServletContainer()).getApplicationHandler().getServiceLocator();
                    ServiceLocatorUtilities.addClasses(serviceLocator, MyInterceptor.class);
                    ServiceLocatorUtilities.addClasses(serviceLocator, MyInterceptor2.class);
                    interceptionService.addMethodInterceptor(x -> x.isAnnotationPresent(Timed.class),
                            serviceLocator.getService(MyInterceptor.class));
                    interceptionService.addMethodInterceptor(x -> x.isAnnotationPresent(Timed.class),
                            serviceLocator.getService(MyInterceptor2.class));
                }
            }

            @Override
            public RequestEventListener onRequest(RequestEvent requestEvent) {
                return null;
            }
        });
    }
}
