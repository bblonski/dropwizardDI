package example;

import com.codahale.metrics.annotation.Timed;
import org.glassfish.hk2.api.messaging.Topic;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
@Intercepted
@RequestScoped
public class MyResource {

    @Inject
    Topic<TestEvent> eventBus;

    @Inject
    MyService service;

    public MyResource() {
    }

    @GET
    @Timed
    @TestIntercept
    public String resourceTest() {
        eventBus.publish(new TestEvent());
        service.doThing();
        return "Hello World";
    }

}
