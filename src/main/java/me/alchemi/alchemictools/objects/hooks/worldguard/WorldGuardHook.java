package me.alchemi.alchemictools.objects.hooks.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;

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
			Flag<?> existingGreet = registry.get("greeting-title");
			Flag<?> existingFarewell = registry.get("farewell-title");
			
	        if (existingGreet instanceof StringFlag) {
	            GREETING_TITLE = (StringFlag) existingGreet;
	        }
	        if (existingFarewell instanceof StringFlag) {
	        	FAREWELL_TITLE = (StringFlag) existingFarewell;
	        }
		}
	}
	
	public static void onEnable() {
		SessionManager sm = WorldGuard.getInstance().getPlatform().getSessionManager();
		sm.registerHandler(GreetingHandler.FACTORY, null);
		sm.registerHandler(FarewellHandler.FACTORY, null);
	}
	
}
