package com.bblonski.dropwizard.ext;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@TestIntercept
public class MyInterceptor {

    @AroundInvoke
    public Object invoke(InvocationContext invocation) throws Exception {
        System.out.println("Hello Interceptor");
        return invocation.proceed();
    }
}
