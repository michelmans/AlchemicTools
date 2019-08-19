package me.alchemi.alchemictools.listener.commandstuff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdvancedCommand implements CommandExecutor {
	
	private final CommandExecutor originalExecutor;
	private final CommandExecutor newExecutor;
	private final String oldPrefix;
	
	public AdvancedCommand(CommandExecutor originalExecutor, CommandExecutor newExecutor, String oldPrefix) {
		this.originalExecutor = originalExecutor;
		this.newExecutor = newExecutor;
		this.oldPrefix = oldPrefix;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (label.startsWith(oldPrefix)) return originalExecutor.onCommand(sender, command, label, args);
		else return newExecutor.onCommand(sender, command, label, args);
		
	}

}
