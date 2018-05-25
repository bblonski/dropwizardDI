package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Intercepted
@Path("/")
public class MyService {
    @Inject
    ApplicationTest test;

    @Inject
    Topic<TestEvent> topic;

    @Inject
    EventBus eventBus;

    @GET
    @Timed
    public String test() {
//        topic.publish(new TestEvent());
        eventBus.post(new TestEvent());
        return "Hello World";
    }

}
