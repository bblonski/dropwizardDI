package com.bblonski.dropwizard.ext;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.extras.interception.Intercepted;
import org.jvnet.hk2.annotations.ContractsProvided;

import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * An HK2 Interception service which registers interceptors with imperative code instead of with declarative annotations.
 * This can be easier to implement and trace in simple scenarios.
 * <p>
 * Interceptors will be invoked in the order they were added, unless the interceptor has the {@link Rank} annotation,
 * which will order by the given rank value, with higher values being invoked before lower ones.
 */
@Singleton
@ContractsProvided({ImperativeInterceptionService.class, InterceptionService.class})
public class ImperativeInterceptionService implements InterceptionService {
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

    /**
     * Add a new method
     * @param function
     * @param interceptor
     */
    public void addMethodInterceptor(Predicate<Method> function, MethodInterceptor interceptor) {
        methodInterceptors.add(new MethodBinding(function, interceptor));
        methodInterceptors.sort((x, y) -> {
            final Class<? extends MethodInterceptor> xClass = x.interceptor.getClass();
            final Class<? extends MethodInterceptor> yClass = y.interceptor.getClass();
            Optional<Integer> xRank = xClass.isAnnotationPresent(Rank.class) ? Optional.of(xClass.getAnnotation(Rank.class).value()) : Optional.empty();
            Optional<Integer> yRank = yClass.isAnnotationPresent(Rank.class) ? Optional.of(yClass.getAnnotation(Rank.class).value()) : Optional.empty();
            if (xRank.isPresent() && yRank.isPresent()) {
                return yRank.get() - xRank.get();
            } else if (xRank.isPresent()) {
                return 1;
            } else if (yRank.isPresent()) {
                return -1;
            } else {
                return 0;
            }
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
        constructorCache.invalidateAll();
    }

    private static class MethodBinding {

        private final Predicate<Method> predicate;
        private final MethodInterceptor interceptor;

        MethodBinding(Predicate<Method> predicate, MethodInterceptor interceptor) {
            this.predicate = predicate;
            this.interceptor = interceptor;
        }
    }

    private static class ConstructorBinding {

        private final Predicate<Constructor> predicate;
        private final ConstructorInterceptor interceptor;

        ConstructorBinding(Predicate<Constructor> predicate, ConstructorInterceptor interceptor) {
            this.predicate = predicate;
            this.interceptor = interceptor;
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
