package com.bblonski.test;

import com.bblonski.dropwizard.ext.DropwizardDIBundle;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

class ApplicationTest extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new ApplicationTest().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        final DropwizardDIBundle<Configuration> bundle = new DropwizardDIBundle<>(AppInitializer.class);
        bundle.setInterceptionService(MyInterceptionService.class);
        bootstrap.addBundle(bundle);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(MyService.class);
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bindAsContract(MySubscriber.class);
            }
        });
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                System.out.println("start");
            }

            @Override
            public void stop() throws Exception {
                System.out.println("end");
            }
        });
    }
}