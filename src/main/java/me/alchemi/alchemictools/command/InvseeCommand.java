package me.alchemi.alchemictools.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.Invsee;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class InvseeCommand extends CommandBase {

	public InvseeCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value(), Messages.COMMANDS_WRONGFORMAT.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!(sender instanceof Player)) return true;
		
		if (sender.hasPermission(command.getPermission())) {
			Player player = Library.getPlayer(args[0]);
			
			if (player == null) {
				messenger.sendMessage(new Stringer(Messages.INVSEE_PLAYEROFFLINE)
						.player(args[0])
						.create(), sender);
				return true;
			}
			
			if (args.length == 1) {
				Invsee.inventory(player, (Player) sender);
			} else if (args.length == 2 && args[1].equals("armour")) {
				Invsee.armour(player, (Player) sender);
			} else if (args.length == 2 && args[1].equals("potions")) {
				Invsee.potions(player, (Player) sender);
			} else if (args.length == 2 && args[1].equals("inventory")) {
				Invsee.inventory(player, (Player) sender);
			}
		} else {
			sendNoPermission(sender, command);
		}
		
		return true;
	}
	
}
