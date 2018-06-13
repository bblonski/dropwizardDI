package com.bblonski.dropwizard.ext;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.servlet.ServletProperties;

public class HK2Bundle<T extends Configuration> implements ConfiguredBundle<T> {
    ServiceLocator serviceLocator;
    private Bootstrap<?> bootstrap;

    public HK2Bundle(ServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        ExtrasUtilities.enableDefaultInterceptorServiceImplementation(serviceLocator);
        ExtrasUtilities.enableTopicDistribution(serviceLocator);
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        // Bind Service locator
        environment.getApplicationContext().setAttribute(ServletProperties.SERVICE_LOCATOR, serviceLocator);
        environment.getAdminContext().setAttribute(ServletProperties.SERVICE_LOCATOR, serviceLocator);
        // Bind default services
        environment.jersey().register(new DefaultDropwizardBinder<>(configuration, environment, bootstrap));
        // Enable immediate scope
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
            }

            @Override
            public void stop() throws Exception {
            }
        });
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = bootstrap;
    }
}
