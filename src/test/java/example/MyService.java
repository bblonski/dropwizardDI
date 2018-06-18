package example;

import org.glassfish.hk2.extras.interception.Intercepted;

@Intercepted
public class MyService {

    @Log
    @Log2
    public void doThing(String say) {
        System.out.println(say);
    }
}
