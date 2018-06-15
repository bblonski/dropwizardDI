package com.bblonski.dropwizard.ext;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.validation.Validator;

public class DefaultDropwizardBinder<T extends Configuration> extends AbstractBinder {

    private final Environment environment;
    private final Bootstrap bootstrap;
    private final T configuration;

    public DefaultDropwizardBinder(Environment environment, Bootstrap bootstrap, T configuration) {
        this.environment = environment;
        this.bootstrap = bootstrap;
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        bind(environment).to(Environment.class);
        bind(environment.healthChecks()).to(HealthCheckRegistry.class);
        bind(environment.lifecycle()).to(LifecycleEnvironment.class);
        bind(environment.metrics()).to(MetricRegistry.class);
        bind(environment.getValidator()).to(Validator.class);
        bind(configuration).to(bootstrap.getApplication().getConfigurationClass()).to(Configuration.class);
        bind(environment.getObjectMapper()).to(ObjectMapper.class);
        bind(bootstrap.getApplication()).to((Class) bootstrap.getApplication().getClass()).to(Application.class);
    }
}
