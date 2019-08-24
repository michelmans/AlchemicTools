package me.alchemi.alchemictools.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.listener.commandstuff.DummyCommand;
import me.alchemi.alchemictools.objects.uuidconverting.IConverter;

public class ToolsCommand extends CommandBase{

	public ToolsCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender.hasPermission(command.getPermission())) {
			if (args.length == 1) {
				if (args[0].equals("reload") && sender.hasPermission("alchemictools.tools.reload")) {
					Tools.getInstance().getConf().reload();
					messenger.sendMessage(Messages.COMMANDS_RELOAD.value(), sender);
					
				} else if (args[0].equals("reload")) {
					sendNoPermission(sender, new DummyCommand("tools reload"));
				} else if (args[0].equals("migrate-uuid")) {
					Tools.getInstance().getIdResolver().apply();
				} else if (args[0].equals("restore-backup")) {
					IConverter.restoreBackups();
				}
			}
		} else {
			sendNoPermission(sender, command);
		}
		return true;
		
	}
	
}
