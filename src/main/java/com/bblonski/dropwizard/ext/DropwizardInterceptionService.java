package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;

import javax.enterprise.inject.Intercepted;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class DropwizardInterceptionService implements InterceptionService {
    @Override
    public Filter getDescriptorFilter() {

        return x -> x.getQualifiers().contains(Intercepted.class.getName());
//        return BuilderHelper.allFilter();
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        if(method.isAnnotationPresent(Timed.class)) {
            return Lists.newArrayList(new MyInterceptor());
        } else {
            return null;
        }
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return null;
    }
}
