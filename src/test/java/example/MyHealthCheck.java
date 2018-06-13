package example;

import com.codahale.metrics.health.HealthCheck;
import org.jvnet.hk2.annotations.ContractsProvided;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@ContractsProvided(HealthCheck.class)
@Named("My Named HealthCheck")
public class MyHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
