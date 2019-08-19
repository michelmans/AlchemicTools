package me.alchemi.alchemictools.listener.commandstuff;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AdvancedTabComplete implements TabCompleter {

	private final TabCompleter originalCompleter;
	private final TabCompleter newCompleter;
	private final String oldPrefix;
	
	public AdvancedTabComplete(TabCompleter originalCompleter, TabCompleter newCompleter, String oldPrefix) {
		this.originalCompleter = originalCompleter;
		this.newCompleter = newCompleter;
		this.oldPrefix = oldPrefix;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		if (alias.startsWith(oldPrefix)) {
			return originalCompleter.onTabComplete(sender, command, alias, args);
		} else {
			return newCompleter.onTabComplete(sender, command, alias, args);
		}
		
	}

}
