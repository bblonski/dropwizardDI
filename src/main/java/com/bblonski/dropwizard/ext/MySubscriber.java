package com.bblonski.dropwizard.ext;

import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Singleton;

@Singleton
public class MySubscriber {

    public MySubscriber() {
    }

    public void test(@Observes TestEvent event) {
        System.out.println("Hello Subscriber");
    }

    public void test2(@ObservesAsync TestEvent event) {
        System.out.println("Hello Async");
    }

}
