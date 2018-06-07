package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.hk2.extras.interception.Intercepted;

@Intercepted
public class MyService {

    @Timed
    public MyService() {
    }

    @Timed
    public void doThing() throws InterruptedException {
        System.out.println("noop");
    }
}
