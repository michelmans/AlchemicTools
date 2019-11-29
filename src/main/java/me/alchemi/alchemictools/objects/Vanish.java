	package me.alchemi.alchemictools.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.alchemictools.Config;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.meta.VanishMeta;
import me.alchemi.alchemictools.objects.hooks.ProtocolUtil;
import me.alchemi.alchemictools.objects.placeholder.Stringer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Vanish {

	private static Map<UUID, Integer> tasks = new HashMap<UUID, Integer>();
	
	public static enum INDICATION {
		SPECTATOR, PREFIX, NONE;
	}
	
	public static void toggle(Player player) {
		
		boolean vanish = false;
		
		if (player.hasMetadata("vanish")) {
			vanish = player.getMetadata("vanish").get(0).asBoolean();
		}
		player.setMetadata("vanish", new VanishMeta(Tools.getInstance(), !vanish));
		vanish(player, !vanish);
	}
		
	public static void vanish(Player player) { vanish(player); }
	
	public static void vanish(Player player, boolean vanish) {
		
		if (vanish) {
			
			Tools.getInstance().addVanishedPlayers(player, Permissions.VANISH_SPECIAL.check(player));
			Tools.getInstance().getStaffchat().addListener(player);
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (!(Permissions.VANISH_SEE.check(p) 
						|| p.equals(player))) {
					p.hidePlayer(Tools.getInstance(), player);
					continue;
				} else if (Permissions.VANISH_SPECIAL.check(player) 
						&& !(Permissions.VANISH_SPECIAL_SEE.check(p) 
								|| p.equals(player))) {
					p.hidePlayer(Tools.getInstance(), player);
					continue;
				}
			}
			
			if (Config.Vanish.ENABLE_POTION_EFFECT.asBoolean() || Config.Vanish.ACTION_BAR.asBoolean()) {
				tasks.put(player.getUniqueId(), Bukkit.getScheduler().scheduleSyncRepeatingTask(Tools.getInstance(), new Runnable() {
						
						private int ran = -1;
					
						@Override
						public void run() {
	
							if (Config.Vanish.ENABLE_POTION_EFFECT.asBoolean() 
									&& (ran == 14 || ran == -1)) {
								player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 220, 1), true);
								ran = -1;
							}
							
							if (Config.Vanish.ACTION_BAR.asBoolean()) player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
									TextComponent.fromLegacyText(Messenger.formatString(Messages.VANISH_PERSISTENTNOTIFICATION.value())));
							ran++;
						}
				}, 0, 5));
			}
			
			if (Config.Vanish.NOTIFY_ADMINS.asBoolean()) {
				
				if (Permissions.VANISH_SPECIAL.check(player)) {
					Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.VANISH_NOTIFYSTART)
							.player(player)
							.parse(player)
							.create(), false, p -> Permissions.VANISH_NOTIFY.check(p) 
							&& Permissions.VANISH_SPECIAL_NOTIFY.check(p) 
							&& !p.equals(player));
				} else {
					Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.VANISH_NOTIFYSTART)
							.player(player)
							.parse(player)
							.create(), false, p -> Permissions.VANISH_NOTIFY.check(p) 
							&& !p.equals(player));
				}
			}
			
			Tools.getInstance().getMessenger().sendMessage(Messages.VANISH_START.value(), player);
			if (Permissions.VANISH_SPECIAL.check(player)) Tools.getInstance().getMessenger().sendMessage(Messages.VANISH_SPECIAL.value(), player);
			
		} else {
			
			Tools.getInstance().removeVanishedPlayer(player);
			
			if (Tools.getInstance().getStaffchat().isListening(player)) Tools.getInstance().getStaffchat().removeListener(player);
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(Tools.getInstance(), player);			
			}
			
			if (tasks.containsKey(player.getUniqueId())) {
				Bukkit.getScheduler().cancelTask(tasks.get(player.getUniqueId()));
			}
			
			if (Config.Vanish.NOTIFY_ADMINS.asBoolean()) {
				Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.VANISH_NOTIFYSTOP)
						.player(player)
						.parse(player)
						.create(), false, p -> Permissions.VANISH_NOTIFY.check(p) && !p.equals(player));
			}
			Tools.getInstance().getMessenger().sendMessage(Messages.VANISH_STOP.value(), player);
			
		}
		setTabListVanish(player, vanish);
	}
	
	public static void setTabListVanish(Player player, boolean vanish) { 
		
		INDICATION indication = INDICATION.valueOf(Config.Vanish.INDICATION.asString());
		
		switch(indication) {
		case NONE:
			break;
		case PREFIX:
			addPrefixTab(player, vanish);
			break;
		case SPECTATOR:
			if (Tools.protocolPresent) ProtocolUtil.updateGameModeTab(player, vanish);
			break;
		
		}
		
	}
	
	public static void addPrefixTab(Player player, boolean vanish) {
		
		if (!Config.Vanish.TAB_PREFIX.asString().isEmpty()) {
			
			if (player.getPlayerListHeader().contains(Messenger.formatString(Config.Vanish.TAB_PREFIX.asString())) && vanish) {
				player.setPlayerListHeader(Messenger.formatString(Config.Vanish.TAB_PREFIX.asString()) + player.getPlayerListHeader());
			} else if (player.getPlayerListHeader().contains(Messenger.formatString(Config.Vanish.TAB_PREFIX.asString())) && !vanish) {
				player.setPlayerListHeader(player.getPlayerListHeader().replace(Messenger.formatString(Config.Vanish.TAB_PREFIX.asString()), ""));
			}
		}
		
	}
	
}
