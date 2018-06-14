package example;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;
import org.jvnet.hk2.annotations.ContractsProvided;

import java.io.PrintWriter;

@ContractsProvided(Task.class)
public class MyTask extends Task {
    public MyTask() {
        super("my-task");
    }

    @Override
    public void execute(ImmutableMultimap<String, String> parameters, PrintWriter output) throws Exception {
        output.write("Hello World");
        output.flush();
    }
}
