# Motivation

The goal is to improve the integration with the built-in HK2 DI framework built into the Jersey service inside Dropwizard.
However, one of Dropwizards strengths is the lack of "black magic" or "automagical" configuration common in Spring Boot and others.
Services and features are for the most part explicitly declared and the program flow is easily traceable.
We want to retain that simplicity while also enabling some of the more advanced features of HK2 such as AOP interceptors and cross-cutting Event services.

There are lots of other bundles which create and bridge external DI frameworks into Dropwizard.
These might provide more complete solutions if you're looking for more comprehensive DI.
I recommend [dropwizard-guicey](http://xvik.github.io/dropwizard-guicey/4.1.0/) or Weld (an example of Weld integration can be found on the Weld branch in this repo).
However, I found that these heavier DI integrations would create additional complexity.
You have to carefully track which services are created by HK2 and which ones are created by your external DI framework, since the advanced AOP features only affect beans created by said service.
Even the solutions out there leveraging HK2 as the DI framework create multiple ServiceLocators which will each behave differently depending on the configuration of Interceptors and Event services.

This solution aims to simplify by only using a single DI container.
This cuts out a lot of the guesswork and potential errors of Interceptors or Events not being invoked when mixing dependency injectors.

# Limitations

Because we have to wait for Jersey to start it's internal HK2 Service Locator, there are a number of limitations.

1) You cannot use this for DI injection into Commands.

1) You cannot inject any services before the server starts.

1) Resource classes must still be registered manually even if they are declared in your AbstractBinder. They must be declared in the AbstractBinder to enable the interception and event services.

# Usage
Simply add the HK2Bundle in your initialization.
The autoRegister boolean tells the bundle to automatically register HealthChecks, Tasks, Managed, and MessageReceivers that are declared in your AbstractBinder.
Leave false if you want to manually bind services.

```
    ...
    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        super.initialize(bootstrap);
        bootstrap.addBundle(new HK2Bundle<>(true));
    }
    ...
```

Afterwards explicitly register services you wish to inject with an AbstractBinder in your run method.

```
     environment.jersey().register(new AbstractBinder() {
                @Override
                protected void configure() {
                    addActiveDescriptor(MyManaged.class);
                    addActiveDescriptor(MyHealthCheck.class);
                    addActiveDescriptor(MyService.class);
                    addActiveDescriptor(MyInterceptor.class);
                    addActiveDescriptor(MySubscriber.class);
                    addActiveDescriptor(MyResource.class);
                    addActiveDescriptor(MyTask.class);
                }
            });

```

The `addActiveDescriptor()` method scans the class for annotations for exposed services.
You'll want to use the `@ConstractsProvided` annotation to declare binding to interfaces.
You can also manually bind services like so.

```
     environment.jersey().register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(MyServiceImpl.class).to(MyService.class).in(Singleton.class);
                }
``` 

However class annotations will be ignored and have to manually specified with the bind syntax.

Finally, register your Resources as normal.

```
     environment.jersey().register(MyResource.class);
```

Now your resource and all injected services will be enhanced with the InterceptionService and TopicDistribution features in HK2.

# AOP Method/Constructor Interceptors

The HK2Bundle enabled the HK2 DefaultInterceptionService.
This will intercept any classes with the @Intercepted and match interceptors based on declarative annotations.
You must create an annotation that itself is annotated with @InterceptionBinder.
This annotation must be on the class or methods you wish to intercept, as well as your Interceptor.
The interceptor must also implement MethodInterceptor or ConstructorInterceptor, have the @Interceptor annotation,
and be bound to the MethodInterceptor or ConstructorInterceptor interface in HK2 using @ContractsProvided or the manual binding syntax.

Examples are in the test resources.

# Events (Topic Distribution)

HK2 also has a DefaultTopicDistribution class for sending events between injected services.
Simply inject a topic like so.

```
    @Inject
    Topic<TestEvent> topic;
    
    public void testEvent() {
        topic.publish(new TestEvent());
    }
```

Subscribe to the event like so.

```
@MessageReceiver
@Singleton
@ContractsProvided(Object.class)
public class MySubscriber {

    public void testEvent(@SubscribeTo TestEvent event) {
        System.out.println("Subscriber");
    }
}

```

Topics will not create subscribers, so they must be created before events are published.
They should also likely be Singleton scope so that they are not garbage collected.
If you enabled autoRegister in the HK2Bundle, all Singleton classes with @MessageReceiver and bound to Object will be automatically created at startup.
Otherwise you will have to manage creation of your subscribers manually.
