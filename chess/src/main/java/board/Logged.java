package board;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 08.05.2020.
 *
 * @author unicorn
 */
public interface Logged {
    Logger getLogger();

    default Logger log() {
        if (LoggedVariables.top == this) {
            return LoggedVariables.stack;
        } else if (LoggedVariables.stack == null) {
            return getLogger();
        } else {
            return log(Stream.of(LoggedVariables.stack.getName(), getLogger().getName()));
        }
    }

    default void stack(Runnable runnable) {
        stack(() -> {
            runnable.run();
            return null;
        });
    }

    default <T> T stack(Supplier<T> runnable) {
        Logged top = LoggedVariables.top;
        Logger stack = LoggedVariables.stack;

        LoggedVariables.stack = log();
        LoggedVariables.top = this;

        try {
            return runnable.get();
        } finally {
            LoggedVariables.top = top;
            LoggedVariables.stack = stack;
        }
    }

    static Logger log(Logged... logs) {
        Stream<String> names = Arrays.stream(logs).
                filter(Objects::nonNull).map(l -> l.getLogger().getName()).
                filter(n -> !n.equals(""));
        return log(names);
    }

    static Logger log(Stream<String> names) {
        return LogManager.getLogger(names.collect(Collectors.joining(".")));
    }

    static Logged log(String name) {
        return () -> LogManager.getLogger(name);
    }

    static String tabs(Collection<?> list) {
        StringBuilder string = new StringBuilder();
        list.forEach(i -> string.append("\n").append(i));
        return string.toString().replace("\n", "\n\t");
    }

    static String tabs(String name, Collection<?> list) {
        if (list.isEmpty()) {
            return "";
        } else {
            StringBuilder string = new StringBuilder();
            string.append("\n").append("=== ").append(name).append(" ===");
            list.forEach(i -> string.append("\n").append(i));
            return string.toString().replace("\n", "\n\t");
        }
    }

    static String shortTabs(String name, Collection<?> list) {
        if (list.isEmpty()) {
            return "";
        } else {
            StringBuilder string = new StringBuilder();
            string.append("\n").append("=== ").append(name).append(" ===");
            list.forEach(i -> string.append("\n").append(i.toString().lines().findFirst().orElse("")));
            return string.toString().replace("\n", "\n\t");
        }
    }
}

class LoggedVariables {
    static Logged top = null;
    static Logger stack = null;
}
