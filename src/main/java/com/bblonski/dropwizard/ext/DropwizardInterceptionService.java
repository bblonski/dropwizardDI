package com.bblonski.dropwizard.ext;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.extras.interception.Intercepted;

import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public class DropwizardInterceptionService implements InterceptionService {
    private Cache<Method, List<MethodInterceptor>> methodCache = CacheBuilder.newBuilder()
            .build();
    private Cache<Constructor, List<ConstructorInterceptor>> constructorCache = CacheBuilder.newBuilder()
            .build();

    private List<MethodBinding> methodInterceptors = new ArrayList<>();
    private List<ConstructorBinding> constructorInterceptors = new ArrayList<>();

    @Override
    public Filter getDescriptorFilter() {
        return x -> x.getQualifiers().contains(Intercepted.class.getName());
    }

    public void addMethodInterceptor(Predicate<Method> function, MethodInterceptor interceptor) {
        methodInterceptors.add(new MethodBinding(function, interceptor));
        methodInterceptors.sort((x, y) -> {
            final Class<? extends MethodInterceptor> xClass = x.interceptor.getClass();
            final Class<? extends MethodInterceptor> yClass = y.interceptor.getClass();
            int xRank = Integer.MAX_VALUE;
            int yRank = Integer.MAX_VALUE;
            if (xClass.isAnnotationPresent(Rank.class)) {
                xRank = xClass.getAnnotation(Rank.class).value();
            }
            if (yClass.isAnnotationPresent(Rank.class)) {
                yRank = yClass.getAnnotation(Rank.class).value();
            }
            return xRank - yRank;
        });
        methodCache.invalidateAll();
    }

    public void addConstructorInterceptor(Predicate<Constructor> predicate, ConstructorInterceptor interceptor) {
        constructorInterceptors.add(new ConstructorBinding(predicate, interceptor));
        constructorInterceptors.sort((x, y) -> {
            final Class<? extends ConstructorInterceptor> xClass = x.interceptor.getClass();
            final Class<? extends ConstructorInterceptor> yClass = y.interceptor.getClass();
            int xRank = Integer.MAX_VALUE;
            int yRank = Integer.MAX_VALUE;
            if (xClass.isAnnotationPresent(Rank.class)) {
                xRank = xClass.getAnnotation(Rank.class).value();
            }
            if (yClass.isAnnotationPresent(Rank.class)) {
                yRank = yClass.getAnnotation(Rank.class).value();
            }
            return xRank - yRank;
        });
        methodCache.invalidateAll();
    }

    private static class MethodBinding {

        private final Predicate<Method> predicate;
        private final MethodInterceptor interceptor;

        MethodBinding(Predicate<Method> predicate, MethodInterceptor interceptor) {
            this.predicate = predicate;
            this.interceptor = interceptor;
        }

        public Predicate<Method> getPredicate() {
            return predicate;
        }

        public MethodInterceptor getInterceptor() {
            return interceptor;
        }
    }

    private static class ConstructorBinding {

        private final Predicate<Constructor> predicate;
        private final ConstructorInterceptor interceptor;

        ConstructorBinding(Predicate<Constructor> predicate, ConstructorInterceptor interceptor) {
            this.predicate = predicate;
            this.interceptor = interceptor;
        }

        public Predicate<Constructor> getPredicate() {
            return predicate;
        }

        public ConstructorInterceptor getInterceptor() {
            return interceptor;
        }
    }

    @Override
    public List<MethodInterceptor> getMethodInterceptors(Method method) {
        try {
            return methodCache.get(method, () -> methodInterceptors.stream()
                    .filter(x -> x.predicate.test(method))
                    .map(x -> x.interceptor)
                    .collect(Collectors.toList()));
        } catch (ExecutionException e) {
            throw new RuntimeException("Could not return method interceptors", e);
        }
    }

    @Override
    public List<ConstructorInterceptor> getConstructorInterceptors(Constructor<?> constructor) {
        try {
            return constructorCache.get(constructor, () -> constructorInterceptors.stream()
                    .filter(x -> x.predicate.test(constructor))
                    .map(x -> x.interceptor)
                    .collect(Collectors.toList()));
        } catch (ExecutionException e) {
            throw new RuntimeException("Could not return constructor interceptors", e);
        }
    }
}
