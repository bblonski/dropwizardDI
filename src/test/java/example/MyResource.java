package example;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@RequestScoped
@TestIntercept
@Intercepted
@Service
public class MyResource {

    @Inject
    Topic<TestEvent> eventBus;

    @Inject
    MyService service;

    @GET
    @Timed
    @TestIntercept
    public String test() throws InterruptedException {
        eventBus.publish(new TestEvent());
        service.doThing();
        return "Hello World";
    }

}
