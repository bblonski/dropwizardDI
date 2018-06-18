package example;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.extras.interception.Interceptor;
import org.jvnet.hk2.annotations.ContractsProvided;

import java.util.Arrays;
import java.util.stream.Collectors;

@Interceptor
@ContractsProvided({MethodInterceptor.class})
@Log
@Rank(5)
public class MyInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println(invocation.getMethod().toGenericString());
        System.out.println("With arguments: " +
                Arrays.stream(invocation.getArguments())
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        return invocation.proceed();
    }
}
