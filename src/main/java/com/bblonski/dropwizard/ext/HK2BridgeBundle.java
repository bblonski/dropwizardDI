package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.EventBus;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.LifeCycle;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import java.util.Objects;

public class HK2BridgeBundle<T extends Configuration> implements ConfiguredBundle<T> {
    public static final String SERVICE_LOCATOR = HK2BridgeBundle.class.getName() + "_LOCATOR";
    private EventBus eventBus;
    private InterceptionService interceptionService;
    private Bootstrap<?> bootstrap;
    private ServiceLocator serviceLocator;

    private HK2BridgeBundle(EventBus eventBus, InterceptionService interceptionService,
                            ServiceLocator serviceLocator) {
        this.eventBus = eventBus;
        this.interceptionService = interceptionService;
        this.serviceLocator = serviceLocator;
    }

    public static <T extends Configuration> HK2BridgeBundleBuilder<T> create() {
        return new HK2BridgeBundleBuilder<>();
    }

    public static class HK2BridgeBundleBuilder<T extends Configuration> {
        private EventBus eventBus;
        private InterceptionService interceptionService;
        private ServiceLocator serviceLocator;

        public HK2BridgeBundleBuilder<T> setEventBus(EventBus eventBus) {
            this.eventBus = eventBus;
            return this;
        }

        public HK2BridgeBundleBuilder<T> withDefaultInterceptionService() {
            this.interceptionService = new DropwizardInterceptionService();
            return this;
        }

        public HK2BridgeBundleBuilder<T> withCustomInterceptionService(InterceptionService interceptionService) {
            this.interceptionService = interceptionService;
            return this;
        }

        public HK2BridgeBundle<T> build() {
            if (this.serviceLocator == null) {
                this.serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator(SERVICE_LOCATOR);
            }
            return new HK2BridgeBundle<>(eventBus, interceptionService, serviceLocator);
        }

        public HK2BridgeBundleBuilder<T> withCustomServiceLocator(ServiceLocator serviceLocator) {
            ServiceLocatorFactory.getInstance().create(SERVICE_LOCATOR);
            return this;
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        this.bootstrap = bootstrap;
        ServiceLocatorUtilities.enableImmediateScope(getLocator());
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getLocator()).to(ServiceLocator.class).named(SERVICE_LOCATOR);
            }
        });
        // Make the service locator available to the admin context too.
        environment.getAdminContext().setAttribute(SERVICE_LOCATOR, getLocator());
        // Finish configuring HK2 when Jetty starts (after the Application.run() method)
        environment.lifecycle().addLifeCycleListener(new AbstractLifeCycle.AbstractLifeCycleListener() {
            @Override
            public void lifeCycleStarting(LifeCycle event) {
                if (event instanceof Server) {
                    ServiceLocatorUtilities.addOneConstant(getLocator(), event, null, Server.class);
                }
            }
        });
        environment.jersey().register(HK2BridgeFeature.class);

        environment.jersey().register(new DefaultDropwizardBinder<>(environment, bootstrap, configuration));
        ServiceLocatorUtilities.bind(getLocator(), new AbstractBinder() {
            @Override
            protected void configure() {
                install(new DefaultDropwizardBinder<>(environment, bootstrap, configuration));
                bind(DropwizardInterceptionService.class).to(DropwizardInterceptionService.class)
                        .to(InterceptionService.class).in(Singleton.class);
                bind(EventBus.class).to(EventBus.class).in(Singleton.class);
            }
        });

        environment.jersey().register(new ApplicationEventListener() {
            @Override
            public void onEvent(ApplicationEvent event) {
                if (event.getType() == ApplicationEvent.Type.INITIALIZATION_FINISHED) {
                    final ServiceLocator serviceLocator = ((ServletContainer) Objects
                            .requireNonNull(environment.getJerseyServletContainer()))
                            .getApplicationHandler()
                            .getServiceLocator();
                    ServiceLocatorUtilities.enableImmediateScope(serviceLocator);
                }
            }

            @Override
            public RequestEventListener onRequest(RequestEvent requestEvent) {
                return null;
            }
        });

    }

    public ServiceLocator getLocator() {
        return serviceLocator;
    }

    private static class HK2BridgeFeature implements Feature {

        private final ServiceLocator serviceLocator;

        @Inject
        private HK2BridgeFeature(ServiceLocator serviceLocator) {
            this.serviceLocator = serviceLocator;
        }

        @Override
        public boolean configure(FeatureContext context) {
            ServiceLocator bundleLocator = serviceLocator.getService(ServiceLocator.class, SERVICE_LOCATOR);
            ExtrasUtilities.bridgeServiceLocator(serviceLocator, bundleLocator);
            ExtrasUtilities.bridgeServiceLocator(bundleLocator, serviceLocator);
            ExtrasUtilities.enableTopicDistribution(serviceLocator);
            ExtrasUtilities.enableTopicDistribution(bundleLocator);
            ServiceLocatorUtilities.bind(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(DropwizardInterceptionService.class).to(InterceptionService.class).in(Singleton.class);
                }
            });
            return true;
        }
    }
}
