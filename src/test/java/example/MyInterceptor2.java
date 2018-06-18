package example;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.extras.interception.Interceptor;
import org.jvnet.hk2.annotations.ContractsProvided;

@Interceptor
@ContractsProvided(MethodInterceptor.class)
@Log2
@Rank(15)
public class MyInterceptor2 implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("Intercepted");
        return invocation.proceed();
    }
}
