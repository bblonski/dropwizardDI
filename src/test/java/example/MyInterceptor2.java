package example;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.extras.interception.Interceptor;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Service;

@Interceptor
@Service
@TestIntercept
@ContractsProvided({MyInterceptor2.class, MethodInterceptor.class})
public class MyInterceptor2 implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("Intercepted 15");
        return invocation.proceed();
    }
}
