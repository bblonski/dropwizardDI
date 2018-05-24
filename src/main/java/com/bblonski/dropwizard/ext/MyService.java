package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.EventBus;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.hk2.extras.interception.Interceptor;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Intercepted
@Path("/")
public class MyService {

    public MyService() {
    }

    @Inject
    ApplicationTest test;

    @GET
    @Timed
    public String test() {
        return "Hello World";
    }

    public void testEvent(@SubscribeTo TestEvent event) {
        System.out.println("Test my event");
    }

}
