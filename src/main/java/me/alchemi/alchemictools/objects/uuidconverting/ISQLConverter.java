package me.alchemi.alchemictools.objects.uuidconverting;

import me.alchemi.alchemictools.objects.events.OnlineUUIDApplyEvent;

public interface ISQLConverter {

	void onDatabase(OnlineUUIDApplyEvent e);
	
}
