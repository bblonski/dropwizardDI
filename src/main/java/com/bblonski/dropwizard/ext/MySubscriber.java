package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.Subscribe;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.glassfish.hk2.extras.interception.Intercepted;

@Intercepted
@MessageReceiver
@Immediate
public class MySubscriber {

    public MySubscriber() {
        System.out.println("Hi");
    }

    @MessageReceiver
    @Subscribe public void test(@SubscribeTo TestEvent event) {
        System.out.println("Subscriber");
    }
}
