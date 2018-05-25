package com.bblonski.test;

import com.bblonski.dropwizard.ext.DropwizardInterceptionService;
import com.codahale.metrics.Timer;
import com.codahale.metrics.annotation.Timed;
import io.dropwizard.setup.Environment;

import javax.inject.Inject;
import javax.ws.rs.GET;

public class MyInterceptionService extends DropwizardInterceptionService {

    @Inject
    public MyInterceptionService(Environment environment) {
        addMethodInterceptor(method -> method.getAnnotation(GET.class) != null,
                invocation -> {
                    System.out.println("Hello GET");
                    return invocation.proceed();
                });
        addMethodInterceptor(method -> method.getAnnotation(Timed.class) != null,
                invocation -> {
                    try (Timer.Context time = environment.metrics().timer(invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName()).time()) {
                        return invocation.proceed();
                    }
                });
        addConstructorInterceptor(method -> true, invocation -> {
            System.out.println("Constructing " + invocation.getConstructor());
            return invocation.proceed();
        });
    }
}
