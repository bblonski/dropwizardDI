package com.bblonski.dropwizard.ext;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Inject;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

public class HK2BridgeBundle implements Bundle {
    public static final String SERVICE_LOCATOR = HK2BridgeBundle.class.getName() + "_LOCATOR";
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        ExtrasUtilities.enableTopicDistribution(getLocator());
        ExtrasUtilities.enableDefaultInterceptorServiceImplementation(getLocator());
        ServiceLocatorUtilities.enableImmediateScope(getLocator());
        ServiceLocatorUtilities.addClasses(getLocator(), MySubscriber.class, MyInterceptor.class, MyService.class);
    }

    @Override
    public void run(Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getLocator()).to(ServiceLocator.class).named(SERVICE_LOCATOR);
            }
        });
        // Make the service locator available to the admin context too.
        environment.getAdminContext().setAttribute(SERVICE_LOCATOR, getLocator());
        // Finish configuring HK2 when Jetty starts (after the Application.run() method)
        environment.lifecycle().addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                if (event instanceof Server) {
                    ServiceLocatorUtilities.addOneConstant(getLocator(), event, null, Server.class);
                }
            }
        });
        environment.jersey().register(HK2BridgeFeature.class);

    }

    private ServiceLocator serviceLocator = ServiceLocatorFactory.getInstance().create(SERVICE_LOCATOR);

    public ServiceLocator getLocator() {
        return serviceLocator;
    }

    private static class HK2BridgeFeature implements Feature {

        private final ServiceLocator serviceLocator;

        @Inject
        private HK2BridgeFeature(ServiceLocator serviceLocator) {
            this.serviceLocator = serviceLocator;
        }

        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocator bundleLocator = serviceLocator.getService(ServiceLocator.class, SERVICE_LOCATOR);
            ExtrasUtilities.bridgeServiceLocator(serviceLocator, bundleLocator);
            ExtrasUtilities.bridgeServiceLocator(bundleLocator, serviceLocator);
            return true;
        }
    }
}
