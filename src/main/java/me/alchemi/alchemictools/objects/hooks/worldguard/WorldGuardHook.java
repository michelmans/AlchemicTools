package me.alchemi.alchemictools.objects.hooks.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;

import me.alchemi.al.configurations.Messenger;

public class WorldGuardHook {

	public static StringFlag GREETING_TITLE;
	public static StringFlag FAREWELL_TITLE;
	
	public static void onLoad() {
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try { 
			StringFlag welcomeFlag = new StringFlag("greeting-title");
			StringFlag farewellFlag = new StringFlag("farewell-title");
			registry.register(welcomeFlag);
			registry.register(farewellFlag);
			GREETING_TITLE = welcomeFlag;
			FAREWELL_TITLE = farewellFlag;
		} catch (FlagConflictException e) {
			Messenger.printStatic("Flags already registered, not using them.", "AlchemicTools");
		} catch (IllegalStateException e) {
			Messenger.printStatic("&4" + e.getMessage());
		}
	}
	
	public static void onEnable() {
		SessionManager sm = WorldGuard.getInstance().getPlatform().getSessionManager();
		if (GREETING_TITLE != null) sm.registerHandler(GreetingHandler.FACTORY, null);
		if (FAREWELL_TITLE != null) sm.registerHandler(FarewellHandler.FACTORY, null);
	}
	
}
