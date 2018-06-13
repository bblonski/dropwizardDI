package example;

import org.glassfish.hk2.extras.interception.InterceptionBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

//@Inherited
@Retention(RUNTIME)
@Target({METHOD, CONSTRUCTOR, TYPE})
@InterceptionBinder
//@Documented
public @interface TestIntercept {
}
