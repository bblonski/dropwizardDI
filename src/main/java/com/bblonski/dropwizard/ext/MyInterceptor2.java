package com.bblonski.dropwizard.ext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.glassfish.hk2.extras.interception.Interceptor;

@Interceptor
@Intercepted
public class MyInterceptor2 implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("Intercepted 15");
        return invocation.proceed();
    }
}