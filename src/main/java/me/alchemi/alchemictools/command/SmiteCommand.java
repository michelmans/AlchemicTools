package me.alchemi.alchemictools.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class SmiteCommand extends CommandBase {

	public SmiteCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender.hasPermission(command.getPermission())) {
		
			Location smite;
			if (args.length == 0) {
				if (sender instanceof Player) {
					smite = ((Player) sender).getLocation();
					messenger.sendMessage(new Stringer(Messages.SMITE_SMITTEN)
							.create(), sender);
					messenger.sendMessage(new Stringer(Messages.SMITE_SENT)
							.player((Player)sender)
							.command(command.getName())
							.create(), sender);
				} else {
					messenger.sendMessage(new Stringer(Messages.SMITE_PROVIDE)
							.create(), sender);
					return true;
				}
			} else {
				Player player = Library.getPlayer(args[0]);
				if (player == null) {
					messenger.sendMessage(new Stringer(Messages.SMITE_PLAYEROFFLINE)
							.player(args[0])
							.create(), sender);
					return true;
				}
				
				smite = player.getLocation();
				
				messenger.sendMessage(new Stringer(Messages.SMITE_SMITTEN)
						.command(command.getName())
						.player(args[0])
						.create(), player);
				messenger.sendMessage(new Stringer(Messages.SMITE_SENT)
						.command(command.getName())
						.player(sender.getName())
						.create(), sender);
				
			}
			
			smite.getWorld().strikeLightning(smite);
			
			
		} else if (sender instanceof Player) {
			sendNoPermission(sender, command);
		}
		
		return true;
	}

}
