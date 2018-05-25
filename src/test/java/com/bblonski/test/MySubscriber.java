package com.bblonski.test;

import com.codahale.metrics.annotation.Timed;
import com.google.common.eventbus.Subscribe;
import org.glassfish.hk2.extras.interception.Intercepted;

@Intercepted
public class MySubscriber {

    @Subscribe
    @Timed
    public void test(TestEvent event) {
        System.out.println("Subscriber");
    }
}
