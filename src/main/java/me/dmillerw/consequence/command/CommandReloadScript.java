package me.dmillerw.consequence.command;

import me.dmillerw.consequence.script.ScriptRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

/**
 * @author dmillerw
 */
public class CommandReloadScript extends CommandBase {

    @Override
    public String getName() {
        return "consequence-reload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/consequence-reload [script-tag]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length != 1) {
            throw new WrongUsageException(getUsage(sender));
        }

        String tag = args[0];

        if (!ScriptRegistry.reloadScript(tag))
            throw new WrongUsageException("No script found with the tag " + tag);
    }
}
