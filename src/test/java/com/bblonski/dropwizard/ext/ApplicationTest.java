package com.bblonski.dropwizard.ext;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

class ApplicationTest extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new ApplicationTest().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new DropwizardDIBundle<>(AppBinder.class));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(MyService.class);
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(MySubscriber.class).in(Singleton.class);
            }
        });
    }

}