package me.alchemi.alchemictools.objects.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.base.ConfigBase.IMessage;
import me.alchemi.al.objects.placeholder.IStringer;
import me.alchemi.alchemictools.Config.Messages;

public class Stringer implements IStringer{

	private String string;
	
	public Stringer(String initialString) {
		string = initialString;
	}
	
	public Stringer(Messages message) {
		string = message.value();
	}
	
	@Override
	public Stringer command(String command) {
		string = string.replace("%command%", command);
		return this;
	}

	@Override
	public Stringer player(Player player) {
		string = string.replace("%player%", player.getName());
		return this;
	}
	
	@Override	
	public Stringer player(String player) {
		string = string.replace("%player%", player);
		return this;
	}
	
	@Override
	public Stringer amount(int amount) {
		string = string.replace("%amount%", String.valueOf(amount));
		return this;
	}
	
	public Stringer reason(String reason) {
		string = string.replace("%reason%", reason);
		return this;
	}
	
	public Stringer message(String message) {
		string = string.replace("%message%", message);
		return this;
	}

	@Override
	public Stringer parse(Player player) {
		
		string = Library.getParser().parse(player, string);
		return this;
		
	}
	
	@Override
	public Stringer parse(OfflinePlayer player) {
		
		string = Library.getParser().parse(player, string);
		return this;
		
	}
	
	@Override
	public Stringer parse(CommandSender sender) {
		
		string = Library.getParser().parse(sender, string);
		return this;
		
	}

	@Override
	public String create() {
		return Messenger.formatString(string);
	}

	@Override
	public IStringer message(IMessage message) {
		string = message.value();
		return this;
	}
	
}
