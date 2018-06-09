package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Inject;

@Intercepted
public class MyService {

    @Inject
    Topic<TestEvent> topic;

    @Timed
    public MyService() {
    }

    @Timed
    public void doThing() throws InterruptedException {
        topic.publish(new TestEvent());
        System.out.println("noop");
    }
}
