package com.bblonski.dropwizard.ext;

import com.google.common.eventbus.Subscribe;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Singleton;

@Intercepted
@Immediate
@MessageReceiver
//@Singleton
public class MySubscriber {
    @Subscribe public void test(@SubscribeTo TestEvent event) {
        System.out.println("Subscriber");
    }
}
