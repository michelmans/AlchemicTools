package me.alchemi.alchemictools.listener.vanish;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import me.alchemi.alchemictools.Tools;

public class ListPing implements Listener {
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		
		Iterator<Player> iter = e.iterator();
		
		while (iter.hasNext()) {
			Player next = iter.next();
			if (Tools.getInstance().getVanishedPlayers().contains(next)
					|| Tools.getInstance().getVanishedOPPlayers().contains(next)) {
				iter.remove();
			}
		}
		
	}

}
