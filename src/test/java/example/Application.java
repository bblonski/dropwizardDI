package example;

import com.bblonski.dropwizard.ext.HK2Bundle;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.servlet.ServletProperties;

import javax.inject.Inject;

public class Application extends io.dropwizard.Application<io.dropwizard.Configuration> {

    private ServiceLocator serviceLocator;

    public static void main(String[] args) throws Exception {
        new Application().run(args);
    }

    @Inject
    MyHealthCheck myHealthCheck;
//    @Inject
//    DropwizardInterceptionService interceptionService;

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        bootstrap.addBundle(new HK2Bundle<>(serviceLocator));
    }

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        ServiceLocatorUtilities.bind(serviceLocator, new AbstractBinder() {
            @Override
            protected void configure() {
//                addActiveDescriptor(DropwizardInterceptionService.class);
                addActiveDescriptor(MyHealthCheck.class);
                addActiveDescriptor(MyService.class);
                addActiveDescriptor(MyInterceptor.class);
                addActiveDescriptor(MyInterceptor2.class);
                addActiveDescriptor(MySubscriber.class);
                addActiveDescriptor(MyResource.class);
            }
        });
        serviceLocator.inject(this);
        environment.getApplicationContext().setAttribute(ServletProperties.SERVICE_LOCATOR, serviceLocator);
        environment.getAdminContext().setAttribute(ServletProperties.SERVICE_LOCATOR, serviceLocator);
//        environment.jersey().packages("com.bblonski.dropwizard.ext");
        environment.jersey().register(MyResource.class);
//        environment.healthChecks().register("Hello", myHealthCheck);
//        interceptionService.addMethodInterceptor(x -> x.isAnnotationPresent(Timed.class),
//                interceptor1);
//        interceptionService.addMethodInterceptor(x -> x.isAnnotationPresent(Timed.class),
//                interceptor2);
//        interceptionService.addMethodInterceptor(x -> true, x -> {
//            System.out.println("Calling " + x.getMethod().getName());
//            return x.proceed();
//        });
//        interceptionService.addConstructorInterceptor(x -> true, x -> {
//            System.out.println("Building " + x.getConstructor().getName());
//            return x.proceed();
//        });
    }
}
