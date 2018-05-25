package com.bblonski.dropwizard.ext;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.extras.interception.Intercepted;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class DropwizardInterceptionService implements InterceptionService {
    private Map<String, Boolean> map = new HashMap<>();
    private List<Pair<Method, MethodInterceptor>> methodInterceptors = new ArrayList<>();
    private List<Pair<Constructor, ConstructorInterceptor>> constructorInterceptors = new ArrayList<>();

    public static class Pair<V extends Executable, T extends Interceptor> {
        private Predicate<V> predicate;
        private T interceptor;

        public Pair(Predicate<V> predicate, T interceptor) {
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

    protected void addMethodInterceptor(Predicate<Method> predicate, MethodInterceptor interceptor) {
        methodInterceptors.add(new Pair<>(predicate, interceptor));
    }

    protected void addConstructorInterceptor(Predicate<Constructor> predicate, ConstructorInterceptor interceptor) {
        constructorInterceptors.add(new Pair<>(predicate, interceptor));
    }
}
