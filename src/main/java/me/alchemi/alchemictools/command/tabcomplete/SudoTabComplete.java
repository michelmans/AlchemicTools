package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.TabCompleteBase;
import net.md_5.bungee.api.ChatColor;

public class SudoTabComplete extends TabCompleteBase {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (!(sender.hasPermission("alchemictools.sudo")))
			return Arrays.asList("");

		
		if (args.length <= 1) {
			list.addAll(Bukkit.getOnlinePlayers().stream()
					.map(player -> ChatColor.stripColor(player.getDisplayName()))
					.collect(Collectors.toSet()));
			
		} else if (args.length == 2 && Library.getPlayer(args[0]) != null) {
			
			if (!Library.getPlayer(args[0]).isOp()) list.add("su");
			list.addAll(Bukkit.getHelpMap().getHelpTopics().stream()
					.map(helptopic -> helptopic.getName().startsWith("/") ? helptopic.getName().replaceFirst("/", "") : "")
					.collect(Collectors.toSet()));
			
		} else if (args.length > 2 && Library.getPlayer(args[0]) != null) {
			
			String cmdAlias = Arrays.asList("su", "op", "sudo").contains(args[1]) ? args[2] : args[1];
			
			String[] args2 = Arrays.asList("su", "op", "sudo").contains(args[1]) 
					? Arrays.copyOfRange(args, 3, args.length) 
							: Arrays.copyOfRange(args, 2, args.length);
			
			PluginCommand cmd = Bukkit.getPluginCommand(cmdAlias);
			if (cmd != null && cmd.getTabCompleter() != null) list.addAll(cmd.tabComplete(sender, cmdAlias, args2));
		}
		
		return returnSortSuggest(list, args);
	}
	
}
