package me.alchemi.alchemictools;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.base.PluginBase;
import me.alchemi.alchemictools.Config.Hooks;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.bungee.BungeeReceiver;
import me.alchemi.alchemictools.command.InvseeCommand;
import me.alchemi.alchemictools.command.RestartCommand;
import me.alchemi.alchemictools.command.SmiteCommand;
import me.alchemi.alchemictools.command.StaffChatCommand;
import me.alchemi.alchemictools.command.SudoCommand;
import me.alchemi.alchemictools.command.ToolsCommand;
import me.alchemi.alchemictools.command.VanishCommand;
import me.alchemi.alchemictools.command.tabcomplete.InvseeTabComplete;
import me.alchemi.alchemictools.command.tabcomplete.RestartTabComplete;
import me.alchemi.alchemictools.command.tabcomplete.SudoTabComplete;
import me.alchemi.alchemictools.command.tabcomplete.ToolsTabComplete;
import me.alchemi.alchemictools.listener.AutoRefill;
import me.alchemi.alchemictools.listener.PrePlayerJoin;
import me.alchemi.alchemictools.listener.staffchat.ChatControl;
import me.alchemi.alchemictools.listener.staffchat.StaffChat;
import me.alchemi.alchemictools.listener.vanish.ListPing;
import me.alchemi.alchemictools.listener.vanish.OpenChest;
import me.alchemi.alchemictools.listener.vanish.PlayerJoin;
import me.alchemi.alchemictools.listener.vanish.PlayerTarget;
import me.alchemi.alchemictools.listener.vanish.RightClickEntity;
import me.alchemi.alchemictools.listener.vanish.TabComplete;
import me.alchemi.alchemictools.objects.hooks.ProtocolUtil;
import me.alchemi.alchemictools.objects.hooks.worldguard.WorldGuardHook;
import me.alchemi.alchemictools.objects.placeholder.MVdWExpansion;
import me.alchemi.alchemictools.objects.placeholder.PapiExpansion;
import me.alchemi.alchemictools.objects.uuidconverting.GlobalConverter;
import me.alchemi.alchemictools.objects.uuidconverting.PlotSquaredConverter;
import me.alchemi.alchemictools.objects.uuidconverting.UUIDResolver;

public class Tools extends PluginBase implements Listener {

	private static Tools instance;
	
	private StaffChat staffchat;
	
	private Set<Player> vanishedPlayers = new HashSet<Player>();
	private Set<String> vanishedNames = new HashSet<String>();
	
	public static boolean protocolPresent = false;
	public static boolean chatControlPresent = false;
	
	private boolean uuidConverting = false;
	
	private UUIDResolver idResolver;
	
	private Config conf;
	
	@Override
	public void onLoad() {
		if (getServer().getPluginManager().getPlugin("WorldGuard") != null) WorldGuardHook.onLoad();
	}
	
	@Override
	public void onEnable() {
		
		for (String s : getDescription().getDepend()) {
			if (Bukkit.getPluginManager().getPlugin(s) == null 
					|| !Bukkit.getPluginManager().isPluginEnabled(s)) {
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', "&4&lDependency %depend% not found, disabling plugin...".replace("%depend%", s)));
				getServer().getPluginManager().disablePlugin(this);
			}
		}
		
		instance = this;
		
		setMessenger(new Messenger(this));
		
		try {
			conf = new Config();
			messenger.print("&6Configs enabled!");
		} catch (IOException | InvalidConfigurationException e) {
			System.err.println("[PLUGIN]: Could not enable config files.\nDisabling plugin...");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}
		
		staffchat = new StaffChat();
		
		enableCommands();
		
		// Placeholders
		if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiExpansion().register();
            messenger.print("Placeholders registered.");
        }
		if (getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI") && Hooks.MVDWPLACEHOLDERAPI.asBoolean()) {
			new MVdWExpansion();
			messenger.print("Placeholders registered at MVdWPlaceholderAPI");
		}
		if (getServer().getPluginManager().isPluginEnabled("WorldGuard") && Hooks.WORLDGUARD.asBoolean()) {
			WorldGuardHook.onEnable();
		}
		
		if (Options.UUIDCONVERSION.asBoolean()){
			idResolver = new UUIDResolver();
			idResolver.onEnable(getServer());
		}
		
		registerEvents();
	
		messenger.print("&4ALERT ALERT\n"
				+ "&9THE DOCTOR &4IS DETECTED!\n"
				+ "&9THE DOCTOR &4IS SURROUNDED\n"
				+ "&4INFORM HIGH COMMAND WE HAVE &9THE DOCTOR!\n"
				+ "&6SEEK! &7LOCATE! &4&lDESTROY!\n"
				+ "&6SEEK! &7LOCATE! &4&lDESTROY!");
		
	}
	
	@Override
	public void onDisable() {
		messenger.print("No more...");
	}
	
	@EventHandler
	public void onPluginEnable(PluginEnableEvent e) {
		if (e.getPlugin().getName().equals("AlchemicLibrary")) {
			Plugin plugin = this;
			PluginManager pm = Bukkit.getPluginManager();
			
			Bukkit.getScheduler().runTaskLater(e.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					
					pm.enablePlugin(plugin);
					
				}
			}, 5);
			
			pm.disablePlugin(plugin);
		} else if (e.getPlugin().getName().equals("AlchemicTools")) {
			Library.getMeta().enable(this.getName());
		}
	}
	
	public void enableCommands() {
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("invsee").setExecutor(new InvseeCommand());
		getCommand("smite").setExecutor(new SmiteCommand());
		getCommand("staffchat").setExecutor(new StaffChatCommand());
		getCommand("reboot").setExecutor(new RestartCommand());
		getCommand("sudo").setExecutor(new SudoCommand());
		getCommand("tools").setExecutor(new ToolsCommand());
		
		getCommand("invsee").setTabCompleter(new InvseeTabComplete());
		getCommand("reboot").setTabCompleter(new RestartTabComplete());
		getCommand("sudo").setTabCompleter(new SudoTabComplete());
		getCommand("tools").setTabCompleter(new ToolsTabComplete());
	}
	
	public void registerEvents() {
		
		List<Listener> listeners = new ArrayList<Listener>();
		
		listeners.addAll(Arrays.asList(this, 
				new RightClickEntity(), 
				new ListPing(), 
				new TabComplete(), 
				new PlayerJoin(), 
				new PlayerTarget(), 
				new AutoRefill(), 
				staffchat,
				idResolver,
				new GlobalConverter(),
				new PrePlayerJoin()));
		
		if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
			ProtocolUtil.listenChatPackets();
			ProtocolUtil.listenPreInteractPackets();
			
			listeners.add(new OpenChest());
			protocolPresent = true;
			messenger.print("ProtocolLib is present!");
		}
		
		if (getServer().getPluginManager().getPlugin("ChatControl") != null 
				&& Hooks.CHATCONTROL.asBoolean()
				&& getServer().getPluginManager().isPluginEnabled("ChatControl")) {
			chatControlPresent = true;
			listeners.add(new ChatControl());
		}
		if (getServer().getPluginManager().isPluginEnabled("PlotSquared")) {
			listeners.add(new PlotSquaredConverter());
		}
				
		for (Listener l : listeners) Bukkit.getPluginManager().registerEvents(l, this);
		
		if (Hooks.BUNGEE.asBoolean()) {
			Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			Bukkit.getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeReceiver());
		}
		
		
		
	}
	
	public static Tools getInstance() {
		return instance;
	}
	
	public Messenger getMessenger() {
		return messenger;
	}
	
	public StaffChat getStaffchat() {
		return staffchat;
	}

	/**
	 * @return the vanishedPlayers
	 */
	public Set<Player> getVanishedPlayers() {
		return vanishedPlayers;
	}
	public void addVanishedPlayers(Player vanishedPlayer) {
		this.vanishedPlayers.add(vanishedPlayer);
		this.vanishedNames.add(vanishedPlayer.getName());
		this.vanishedNames.add(vanishedPlayer.getPlayerListName());
		this.vanishedNames.add(vanishedPlayer.getDisplayName());
	}
	
	public void removeVanishedPlayer(Player vanishedPlayer) {
		this.vanishedPlayers.remove(vanishedPlayer);
		this.vanishedNames.remove(vanishedPlayer.getName());
		this.vanishedNames.remove(vanishedPlayer.getPlayerListName());
		this.vanishedNames.remove(vanishedPlayer.getDisplayName());
	}
	
	public Set<String> getVanishedNames() {
		return vanishedNames;
	}
	
	public Config getConf() {
		return conf;
	}
	
	public UUIDResolver getIdResolver() {
		return idResolver;
	}
	
	public boolean isUuidConverting() {
		return uuidConverting;
	}
	
	public void setUuidConverting(boolean uuidConverting) {
		this.uuidConverting = uuidConverting;
	}
}
