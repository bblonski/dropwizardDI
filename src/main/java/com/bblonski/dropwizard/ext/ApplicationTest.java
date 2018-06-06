package com.bblonski.dropwizard.ext;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
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
    public ApplicationTest(Event<TestEvent> event) {
        this.event = event;
    }

    public ApplicationTest() {
    }

    public static void main(String[] args) throws Exception {
        final SeContainer se = SeContainerInitializer.newInstance()
                .addPackages(ApplicationTest.class.getPackage())
                .initialize();
        se.select(ApplicationTest.class).get().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        super.run(configuration, environment);
        environment.jersey().register(MyService.class);
        event.fireAsync(new TestEvent());
    }

    @Override
    void postRun(Configuration configuration, Environment environment, ServiceLocator serviceLocator, InterceptionService interceptionService) {
    }

}