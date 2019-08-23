package me.alchemi.alchemictools.objects.hooks.worldguard;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import me.alchemi.al.configurations.Messenger;

public class GreetingHandler extends Handler{

	public static final Factory FACTORY = new Factory();
	public static class Factory extends Handler.Factory<GreetingHandler>{
		@Override
		public GreetingHandler create(Session session) {
			return new GreetingHandler(session);
		}	
	}
	
	private Set<String> lastMessageStack = new HashSet<String>();
	
	protected GreetingHandler(Session session) {
		super(session);
	}
	
	protected static Set<String> getMessages(LocalPlayer player, ApplicableRegionSet set, Flag<String> flag) {
        return Sets.newLinkedHashSet(set.queryAllValues(player, flag));
    }
	
	protected static Set<String> sendAndCollect(LocalPlayer player, ApplicableRegionSet set, Flag<String> flag, Set<String> stack) {
		
		Set<String> strings = getMessages(player, set, flag);
		
		Player bukkitPlayer = Bukkit.getPlayer(player.getName());
		
		if (bukkitPlayer == null) return stack;
		
		for (String string : strings) {
			if (!stack.contains(string)) {
				
				String[] parts = string.replaceAll("\\\\n", "\n").split("\\n", 2);
				
				if (parts.length > 1) {
					bukkitPlayer.sendTitle(Messenger.formatString(parts[0]), Messenger.formatString(parts[1]), 30, 5, 30);
				} else {
					bukkitPlayer.sendTitle(Messenger.formatString(parts[0]), null, 30, 5, 30);
				}
				break;
			}
		}
		
		stack = Sets.newHashSet(strings);
		
		if (!stack.isEmpty()) {
			for (ProtectedRegion region : set) {
                String message = region.getFlag(flag);
                if (message != null) {
                    stack.add(message);
                }
            }
		}
		return stack;		
	}
	
	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
			Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
		
		if (!player.isPlayer()) return true;
		
		lastMessageStack = sendAndCollect(player, toSet, WorldGuardHook.GREETING_TITLE, lastMessageStack);
		return true;
	}
}
