package example;


import org.glassfish.hk2.extras.interception.InterceptionBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD, CONSTRUCTOR, TYPE})
@InterceptionBinder
public @interface Log2 {
}
