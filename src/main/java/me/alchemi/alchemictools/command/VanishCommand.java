package me.alchemi.alchemictools.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Permissions;
import me.alchemi.alchemictools.objects.Vanish;

public class VanishCommand extends CommandBase {

	public VanishCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player && sender.hasPermission(command.getPermission())) {
			
			if (Permissions.VANISH_SPECIAL.check(sender)) {
				if (args.length >= 1 
						&& args[0].equals("special")) Vanish.toggle((Player) sender, false);
				else Vanish.toggle((Player) sender, true);
			} else {
				Vanish.toggle((Player) sender, false);
			}
			
		} else if (sender instanceof Player) {
			
			sendNoPermission(sender, command);
		
		}
		
		return true;
	}
	
}
