package dev.smithed.companion.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.smithed.companion.SmithedMain;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * A series of utilities for handling shortcuts
 * @author dragoncommands
 */
public class ShortcutUtils {

    private static final Logger logger = SmithedMain.createLogger("shortcuts");

    /**
     * Registers a shortcut given a ShortcutData object and server reference
     * @param data the source data object from which a command will be created
     * @param dispatcher the instance of the server command dispatcher which will be used to register the commands
     */
    public static void registerShortcut(ShortcutData data, CommandDispatcher<ServerCommandSource> dispatcher) {
        logger.info("Registering shortcut: " + data.getAlias());
        dispatcher.register(literal(data.getAlias()).requires(source -> source.hasPermissionLevel(data.getPermissionLevel())).executes(ctx -> {
            ServerCommandSource source = ctx.getSource();
            CommandFunctionManager manager = source.getServer().getCommandFunctionManager();
            Optional<CommandFunction> function = manager.getFunction(data.getFunction());
            function.ifPresent(commandFunction -> manager.execute(commandFunction, source));
            return 1;
        }));
    }

    /**
     * When called the server will parse all contained shortcuts on start-up.
     * @param server server to dispatch command additions to
     */
    public static void enableShortcuts(MinecraftServer server) {
        logger.info("Shortcuts enabled!");
        long timeStart = System.currentTimeMillis();

        var registry= server.getRegistryManager().get(RegistryUtils.SHORTCUT_REGISTRY);
        registry.forEach(data -> registerShortcut(data, server.getCommandFunctionManager().getDispatcher()));

        long timeStop = System.currentTimeMillis();
        logger.info("Registered: {} shortcuts in: {} milliseconds", registry.size(), timeStop-timeStart);
    }

    /**
     * When called this function will send an updated shortcut tree to the connected clients
     * @param server: server to dispatch command modifications and additions to
     */
    //TODO: write up code for complete tree rebuilding so that old commands are cleared from cache. -Dragon
    public static void updateShortcuts(MinecraftServer server) {
        logger.info("Reloading shortcuts!");
        long timeStart = System.currentTimeMillis();

        var registry= server.getRegistryManager().get(RegistryUtils.SHORTCUT_REGISTRY);
        registry.forEach(data -> registerShortcut(data, server.getCommandFunctionManager().getDispatcher()));
        server.getPlayerManager().getPlayerList().forEach(player -> server.getCommandManager().sendCommandTree(player));

        long timeStop = System.currentTimeMillis();
        logger.info("Registered: {} shortcuts in: {} milliseconds", registry.size(), timeStop-timeStart);
    }

    /**
     * The shortcut data class, contains the data needed to assemble shortcuts.
     * @author dragoncommands
     */
    public static class ShortcutData {
        private final int permission_level;
        private final Identifier function;
        private final String alias;

        /**
         * @param permission_level specifies the server permission level of a user required to run a command
         * @param function specifies the function to be run from the passed command
         * @param alias specifies the name of the new shortcut command
         */
        public ShortcutData(int permission_level, Identifier function, String alias) {
            this.permission_level = permission_level;
            this.function = function;
            this.alias = alias;
        }

        /**
        * Standard getters to be used by the Codec
         */
        public int getPermissionLevel() {
            return permission_level;
        }
        public String getAlias() {
            return this.alias;
        }
        public Identifier getFunction() {
            return function;
        }

        /**
         * The Codec for serialization and deserialization of shortcuts.
         */
        public static final Codec<ShortcutData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("permission_level").forGetter(ShortcutData::getPermissionLevel),
                        Identifier.CODEC.fieldOf("function").forGetter(ShortcutData::getFunction),
                        Codec.STRING.fieldOf("alias").forGetter(ShortcutData::getAlias)
                ).apply(instance, ShortcutData::new)
        );
    }

}
