package example;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.hk2.api.UseProxy;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;

@RequestScoped
@Intercepted
@TestIntercept
@UseProxy
public class MyService {

    @Inject
    Topic<TestEvent> topic;

    @Timed
    public MyService() {
    }

    @Timed
    public void doThing() {
        topic.publish(new TestEvent());
        System.out.println("noop");
    }
}
