package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class MyService {

    @Inject
    Event<TestEvent> eventEvent;

    @Inject
    ApplicationTest application;

    public MyService() {
    }

    @GET
    @Timed
    public String test() {
        eventEvent.fire(new TestEvent());
        return application.getName();
    }

}
