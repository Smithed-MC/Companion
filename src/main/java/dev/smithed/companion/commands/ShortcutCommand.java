package dev.smithed.companion.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ShortcutCommand {

    private String functionPath;


    public static void Register(CommandDispatcher<ServerCommandSource> dispatcher, List<ShortcutData> args) {
        for (ShortcutData arg: args) {
            dispatcher.register(CommandManager.literal(arg.getAlias()).requires((source) ->
                    source.hasPermissionLevel(arg.getPermissionLevel())).executes((context) ->
                    execute(context.getSource(), CommandFunctionArgumentType.getFunctions(context, arg.getFunction()))));
        }
    }

    private static int execute(ServerCommandSource source, Collection<CommandFunction> functions) {
        int i = 0;

        CommandFunction commandFunction;
        for(Iterator<CommandFunction> var3 = functions.iterator(); var3.hasNext(); i += source.getServer().getCommandFunctionManager().execute(commandFunction, source.withSilent().withMaxLevel(2))) {
            commandFunction = var3.next();
        }

        if (functions.size() == 1) {
            source.sendFeedback(Text.translatable("commands.function.success.single", i, (functions.iterator().next()).getId()), true);
        } else {
            source.sendFeedback(Text.translatable("commands.function.success.multiple", i, functions.size()), true);
        }

        return i;
    }


    public static class ShortcutData {
        private int permissionLevel;
        private String function, alias;

        ShortcutData(int permissionLevel, String function, String alias) {
            this.permissionLevel = permissionLevel;
            this.function = function;
            this.alias = alias;
        }

        public int getPermissionLevel() {
            return permissionLevel;
        }

        public String getAlias() {
            return alias;
        }

        public String getFunction() {
            return function;
        }
    }
}
