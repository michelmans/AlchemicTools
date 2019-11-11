package me.alchemi.alchemictools.command.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.base.TabCompleteBase;
import me.alchemi.alchemictools.objects.Permissions;

public class ToolsTabComplete extends TabCompleteBase {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (!(sender instanceof Player && sender.hasPermission(command.getPermission())))
			return Arrays.asList("");

		if (args.length <= 1) {
			if (Permissions.TOOLS_RELOAD.check(sender)) list.add("reload");
			if (Permissions.TOOLS_MIGRATEUUID.check(sender)) list.add("migrate-uuid");
			if (Permissions.TOOLS_RESTORE.check(sender)) list.add("restore-backups");
			if (Permissions.TOOLS_GETUUID.check(sender)) list.add("get-uuid");
			
			if (ifAny(sender, Permissions.TOOLS_BUGS_ACTIVATE.toString(),
					Permissions.TOOLS_BUGS_DEACTIVATE.toString(),
					Permissions.TOOLS_BUGS_LIST.toString(),
					Permissions.TOOLS_BUGS_TRELLO_TOKEN.toString(),
					Permissions.TOOLS_BUGS_TRELLO_TOKEN_GENERATE.toString())) list.add("bugs");
			
		} else if (args.length <= 2) {
			
			if (args[0].equals("get-uuid")) {
				list.addAll(Arrays.stream(Bukkit.getOfflinePlayers())
						.map(OfflinePlayer::getName)
						.collect(Collectors.toSet()));
				
			} else if (args[0].equals("bugs")) {
				if (Permissions.TOOLS_BUGS_ACTIVATE.check(sender)) list.add("activate");
				if (Permissions.TOOLS_BUGS_DEACTIVATE.check(sender)) list.add("deactive");
				if (Permissions.TOOLS_BUGS_LIST.check(sender)) list.add("list");
				
				if (ifAny(sender, Permissions.TOOLS_BUGS_TRELLO_TOKEN.toString(),
					Permissions.TOOLS_BUGS_TRELLO_TOKEN_GENERATE.toString())) list.add("trello");
			}
			
		} else if (args.length <= 3) {
			
			if (args[0].equals("trello")) {
				if (Permissions.TOOLS_BUGS_TRELLO_TOKEN.check(sender)) list.add("token");
				if (Permissions.TOOLS_BUGS_TRELLO_TOKEN_GENERATE.check(sender)) list.add("generateToken");
			}
			
		}
		
		return returnSortSuggest(list, args);
	}
	
}
