package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import org.glassfish.hk2.api.messaging.Topic;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class MyResource {

    @Inject
    Topic<TestEvent> topic;

    @Inject
    EventBus eventBus;

    @Inject
    MyService service;

    @GET
    @Timed
    public String test() throws InterruptedException {
        topic.publish(new TestEvent());
        eventBus.post(new TestEvent());
        service.doThing();
        return "Hello World";
    }

}
