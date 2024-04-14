package teal.drawme.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.util.Collection;
import java.util.List;

public class BetterStringArgument implements ArgumentType<String> {

    private static final String safeFileCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890_-.";

    private final boolean ignoreSpace;
    private final boolean fileSafe;

    private BetterStringArgument(boolean ignoreSpace) {
        this(ignoreSpace, false);
    }

    private BetterStringArgument(boolean ignoreSpace, boolean fileSafe) {
        this.ignoreSpace = ignoreSpace;
        this.fileSafe = fileSafe;
    }

    public static BetterStringArgument string() {
        return new BetterStringArgument(false);
    }

    public static BetterStringArgument fileSafe() {
        return new BetterStringArgument(false, true);
    }

    public static BetterStringArgument paragraph() {
        return new BetterStringArgument(true);
    }

    public static <S> String getString(final CommandContext<S> context, final String name) {
        return context.getArgument(name, String.class);
    }

    private static final Collection<String> EXAMPLES = List.of(
        "hi",
        "hello",
        "ok"
    );

    @Override
    public String parse(StringReader reader) {
        int beginning = reader.getCursor();

        if (reader.canRead()) {
            reader.skip();
        }

        while (
            reader.canRead() && (fileSafe ? safeFileCharacters.contains(Character.toString(reader.peek())) : (ignoreSpace || reader.peek() != ' '))
        ) {
            reader.skip();
        }

        return reader.getString().substring(beginning, reader.getCursor());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}