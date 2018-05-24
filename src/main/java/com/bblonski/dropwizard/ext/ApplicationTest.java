package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.jboss.weld.environment.se.Weld;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.se.SeContainer;

@ApplicationScoped
class ApplicationTest extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        final SeContainer container = Weld.newInstance().addPackages(Package.getPackage("com.bblonski.dropwizard.ext")).initialize();

        container.getBeanManager().getBeans(ApplicationTest.class);
        new ApplicationTest().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        super.run(configuration, environment);
        environment.jersey().register(MyService.class);
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
//                bindAsContract(MyService.class);
            }
        });
    }

    @Override
    void postRun(Configuration configuration, Environment environment, ServiceLocator serviceLocator, InterceptionService interceptionService) {
    }

}