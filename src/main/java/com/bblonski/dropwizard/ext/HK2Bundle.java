package com.bblonski.dropwizard.ext;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.extras.events.internal.DefaultTopicDistributionService;
import org.glassfish.hk2.extras.interception.internal.DefaultInterceptionService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class HK2Bundle<T extends Configuration> implements ConfiguredBundle<T> {
    private final boolean autoRegister;
    private Bootstrap<?> bootstrap;

    public HK2Bundle(boolean autoRegister) {
        this.autoRegister = autoRegister;
    }

    @Override
    public void run(T configuration, Environment environment) {
        // Bind default services
        environment.jersey().register(new DefaultDropwizardBinder<>(configuration, environment, bootstrap));
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                addActiveDescriptor(DefaultInterceptionService.class);
                addActiveDescriptor(DefaultTopicDistributionService.class);
            }
        });
        if (autoRegister) {
            environment.jersey().register(AutoRegisterFeature.class);
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = bootstrap;
    }
}
