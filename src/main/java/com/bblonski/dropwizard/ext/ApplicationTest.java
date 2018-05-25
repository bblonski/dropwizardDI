package com.bblonski.dropwizard.ext;

import com.codahale.metrics.health.HealthCheck;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;

class ApplicationTest extends Application<Configuration> {
    public static void main(String[] args) throws Exception {
        new ApplicationTest().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        super.run(configuration, environment);
        environment.jersey().register(MyService.class);
    }

    @Override
    void postRun(Configuration configuration, Environment environment, ServiceLocator serviceLocator, InterceptionService interceptionService) {
        environment.healthChecks().register("Test", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
    }

}