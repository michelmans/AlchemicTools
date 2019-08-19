package me.alchemi.alchemictools.command;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.alchemi.al.objects.base.CommandBase;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.listener.staffchat.StaffChat;

public class StaffChatCommand extends CommandBase {

	private StaffChat staffChat;
	
	public StaffChatCommand() {
		super(Tools.getInstance(), Messages.COMMANDS_NOPERMISSION.value());
		staffChat = Tools.getInstance().getStaffchat();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length == 0 && sender.hasPermission("alchemictools.staffchat")) {
			
			if (!staffChat.isListening(sender)) staffChat.addListener(sender);
			else staffChat.removeListener(sender);
			
		} else if (args.length > 0 && 
				(sender.hasPermission("alchemictools.staffchat") 
						|| ((label.equals("ho") || label.equals("helpop")) 
								&& sender.hasPermission("alchemictools.helpop")))) {
			
			String toSend = "";
			for (String arg : args) {
				toSend += arg + " ";
			}
			staffChat.send(sender, toSend);
			
		} else {
			
			sendNoPermission(sender, command);
			
		}
		
		return true;
	}

}
