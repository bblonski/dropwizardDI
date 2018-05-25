package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.jvnet.hk2.annotations.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Service
public class DropwizardInterceptionService implements InterceptionService {
    @Override
    public Filter getDescriptorFilter() {
        return x ->  {
            try {
                return Arrays.asList(Class.forName(x.getImplementation()).getAnnotations()).stream()
                        .map(it -> it.annotationType()).anyMatch(it -> it.equals(Intercepted.class));
            } catch (ClassNotFoundException e) {
                return false;
            }
        };
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
