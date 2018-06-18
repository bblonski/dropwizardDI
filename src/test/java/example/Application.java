package example;

import com.bblonski.dropwizard.ext.HK2Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

public class Application extends io.dropwizard.Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new HK2Bundle<>(true));
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                addActiveDescriptor(MyManaged.class);
                addActiveDescriptor(MyHealthCheck.class);
                addActiveDescriptor(MyService.class);
                addActiveDescriptor(MyInterceptor.class);
                addActiveDescriptor(MyInterceptor2.class);
                addActiveDescriptor(MySubscriber.class);
                addActiveDescriptor(MyResource.class);
                addActiveDescriptor(MyTask.class);
            }
        });
        environment.jersey().register(MyResource.class);
    }
}
