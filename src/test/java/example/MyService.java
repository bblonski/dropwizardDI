package example;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.hk2.api.messaging.Topic;

import javax.inject.Inject;

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
