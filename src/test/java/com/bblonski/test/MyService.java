package com.bblonski.test;

import com.google.common.eventbus.EventBus;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class MyService {
    @Inject
    ApplicationTest test;

    @Inject
    EventBus eventBus;

    @GET
    public String test() {
        eventBus.post(new TestEvent());
        return "Hello World";
    }

}
