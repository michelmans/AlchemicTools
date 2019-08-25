package me.alchemi.alchemictools.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class PrePlayerJoin implements Listener {

	@EventHandler
	public void onJoin(AsyncPlayerPreLoginEvent e) {
		if (Tools.getInstance().isUuidConverting()) {
			e.setLoginResult(Result.KICK_OTHER);
			e.setKickMessage(new Stringer(Messages.UUID_NOJOIN)
					.player(e.getName())
					.create());
		}
	}
	
}
