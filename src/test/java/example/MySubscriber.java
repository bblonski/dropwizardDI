package example;

import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.jvnet.hk2.annotations.ContractsProvided;

import javax.inject.Singleton;

@MessageReceiver
@Singleton
@ContractsProvided(Object.class)
public class MySubscriber {

    public void testEvent(@SubscribeTo TestEvent event) {
        System.out.println("Subscriber");
    }
}
