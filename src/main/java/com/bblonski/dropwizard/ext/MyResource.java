package com.bblonski.dropwizard.ext;

import org.glassfish.jersey.server.ManagedAsync;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.io.IOException;

@Path("/")
public class MyResource {

    @Inject
    Event<TestEvent> eventEvent;

    @Inject
    ApplicationTest application;

    @Inject
    MyService myService;

    public MyResource() {
    }

    @GET
    @TestIntercept
    @ManagedAsync
    public void test(@Suspended final AsyncResponse asyncResponse) throws IOException {
        eventEvent.fireAsync(new TestEvent());
        eventEvent.fire(new TestEvent());
        myService.doTest();
        asyncResponse.resume(application.getName());
    }

}
