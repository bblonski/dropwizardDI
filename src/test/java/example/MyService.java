package example;

import com.codahale.metrics.annotation.Timed;

public class MyService {

    @Timed
    public MyService() {
    }

    @Timed
    public void doThing() {
        System.out.println("noop");
    }
}
