package example;

import io.dropwizard.lifecycle.Managed;
import org.jvnet.hk2.annotations.ContractsProvided;

import javax.inject.Singleton;

@Singleton
@ContractsProvided(Managed.class)
public class MyManaged implements Managed {
    @Override
    public void start() throws Exception {
        System.out.println("Start");
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Stop");
    }
}
