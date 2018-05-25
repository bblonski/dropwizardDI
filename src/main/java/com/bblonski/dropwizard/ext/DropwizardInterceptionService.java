package com.bblonski.dropwizard.ext;

import com.codahale.metrics.annotation.Timed;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.jvnet.hk2.annotations.Service;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class DropwizardInterceptionService implements InterceptionService {
    private Map<String, Boolean> map = new HashMap<>();
    private List<Pair<Method, MethodInterceptor>> methodInterceptors = new ArrayList<>();
    private List<Pair<Constructor, ConstructorInterceptor>> constructorInterceptors = new ArrayList<>();


    private static class Pair<V extends Executable, T extends Interceptor> {
        private Predicate<V> predicate;
        private T interceptor;

        Pair(Predicate<V> predicate, T interceptor) {
            this.predicate = predicate;
            this.interceptor = interceptor;
        }

        public Predicate<V> getPredicate() {
            return predicate;
        }

        public T getInterceptor() {
            return interceptor;
        }
    }

    public DropwizardInterceptionService() {

        addMethodInterceptor(x -> x.getAnnotation(Timed.class) != null, x -> {
            System.out.println("Timed Interception 1");
            return x.proceed();
        });
        addMethodInterceptor(x -> x.getAnnotation(Timed.class) != null, x -> {
            System.out.println("Timed Interception 2");
            return x.proceed();
        });
        addMethodInterceptor(x -> x.getAnnotation(Timed.class) != null, x -> {
            System.out.println("Timed Interception 3");
            return x.proceed();
        });
        addMethodInterceptor(x -> x.getAnnotation(Timed.class) != null, x -> {
            System.out.println("Timed Interception 4");
            return x.proceed();
        });
    }

    @Override
    public Filter getDescriptorFilter() {
        return descriptor ->
                map.computeIfAbsent(descriptor.getImplementation(), className -> {
                    try {
                        return Arrays.stream(Class.forName(className).getAnnotations())
                                .map(it -> it.annotationType().getName()).anyMatch(it -> it.equals(Intercepted.class.getName()));
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                });
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        return methodInterceptors.stream().filter(it -> it.predicate.test(method)).map(it -> it.interceptor).collect(Collectors.toList());
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        return constructorInterceptors.stream().filter(it -> it.predicate.test(constructor)).map(it -> it.interceptor).collect(Collectors.toList());
    }

    public void addMethodInterceptor(Predicate<Method> predicate, MethodInterceptor interceptor) {
        methodInterceptors.add(new Pair<>(predicate, interceptor));
    }

    public void addConstructorInterceptor(Predicate<Constructor> predicate, ConstructorInterceptor interceptor) {
        constructorInterceptors.add(new Pair<>(predicate, interceptor));
    }
}
