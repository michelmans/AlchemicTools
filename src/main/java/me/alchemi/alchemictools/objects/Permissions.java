package me.alchemi.alchemictools.objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Permissions {

	SMITE(),
	VANISH(),
	VANISH_SEE(),
	VANISH_NOTIFY(),
	STAFFCHAT(),
	HELPOP(),
	SUDO(),
	INVSEE(),
	RESTARTSERVER(),
	TOOLS(),
	TOOLS_RELOAD(),
	TOOLS_SORT(),
	TOOLS_AUTOREFILL(),
	CHANNEL_ALL(),
	CHANNEL_SPY();
	
	private String permission;
	
	private Permissions() {
		permission = this.toString();
	}
	
	public boolean check(CommandSender sender) {
		return sender.hasPermission(permission);
	}
	
	public boolean check(Player player) {
		return player.hasPermission(permission) || player.isOp();
	}
	
	public static void registerNewPermission(String basePerm) {
		Permission perm = new Permission("alchemictools." + basePerm.toLowerCase().replace("_", ".").replace("all", "*"), PermissionDefault.OP);
		
		if (basePerm.toLowerCase().startsWith("channel")) {
			perm.addParent(CHANNEL_SPY.toString(), true);
		}
		
		Bukkit.getPluginManager().addPermission(perm);
	}
	
	@Override
	public String toString() {
		return "alchemictools." + super.toString().toLowerCase().replace("_", ".").replace("all", "*");
	}
	
}
