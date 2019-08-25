package me.alchemi.alchemictools.command;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.Library;
import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class SudoCommand extends CommandBase {

	public SudoCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender.hasPermission(command.getPermission())) {
			
			boolean makeOp = false;
			
			if (args.length > 1) {
				
				Player player = Library.getPlayer(args[0]);
				
				if (Arrays.asList("su", "op", "sudo").contains(args[1]) && !player.isOp()) {
					makeOp = true;
				}
				
				if (player != null) {
					
					if (makeOp) player.setOp(true);
					player.performCommand(compileArgs(Arrays.copyOfRange(args, makeOp ? 2 : 1, args.length)));
					if (makeOp) player.setOp(false);
					messenger.sendMessage(new Stringer(Messages.SUDO_RUN)
							.command("/" + compileArgs(Arrays.copyOfRange(args, makeOp ? 2 : 1, args.length)))
							.player(player)
							.parse(player)
							.create(), sender);
					return true;
				}
				messenger.sendMessage(new Stringer(Messages.SUDO_PLAYEROFFLINE)
						.player(args[0])
						.create(), sender);
				
			}
			
		}
		sendNoPermission(sender, command);
		return true;
		
	}
	
	

}
