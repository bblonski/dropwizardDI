package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@Intercepted
@RequestScoped
public class MyResource {

    @Timed
    public MyResource() {
        System.out.println("Constrcuted");
    }

    @Inject
    EventBus eventBus;

    @Inject
    MyService service;

    @GET
    @Timed
    public String test() throws InterruptedException {
        eventBus.post(new TestEvent());
        service.doThing();
        return "Hello World";
    }

}
