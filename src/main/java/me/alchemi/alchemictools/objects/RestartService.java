package me.alchemi.alchemictools.objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.alchemictools.Config.Messages;
import me.alchemi.alchemictools.Config.Options;
import me.alchemi.alchemictools.Tools;
import me.alchemi.alchemictools.objects.placeholder.Stringer;

public class RestartService {

	private int seconds;
	private CommandSender starter;
	private int task;
	private String reason;
	
	private static RestartService instance;
	
	static {
		Messenger.printStatic("Hello there");
	}
	
	public RestartService(CommandSender starter) {
		if (instance != null) throw new IllegalAccessError("A restart is already scheduled.");
		
		instance = this;

		this.starter = starter;
		this.seconds = Options.RESTART_DEFAULTDELAY.asInt();
		this.reason = Options.RESTART_DEFAULTMESSAGE.asString();
		
		this.task = createTask();
		
		warnPlayers(seconds);
		
		Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.RESTART_REASONING)
				.player(this.starter.getName())
				.amount(this.seconds)
				.reason(this.reason)
				.player(this.starter.getName()), false);
		Messenger.printStatic(new Stringer(Messages.RESTART_REASONING)
				.player(this.starter.getName())
				.amount(this.seconds)
				.reason(this.reason)
				.player(this.starter.getName())
				.create());
		
	}
	
	public RestartService(CommandSender starter, int seconds) {
		if (instance != null) throw new IllegalAccessError("A restart is already scheduled.");
		
		instance = this;
		
		this.starter = starter;
		this.seconds = seconds;
		this.reason = Options.RESTART_DEFAULTMESSAGE.asString();
		
		this.task = createTask();
		
		warnPlayers(seconds);
		
		Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.RESTART_REASONING)
				.player(this.starter.getName())
				.amount(this.seconds)
				.reason(this.reason)
				.player(this.starter.getName()), false);
		Messenger.printStatic(new Stringer(Messages.RESTART_REASONING)
				.player(this.starter.getName())
				.amount(this.seconds)
				.reason(this.reason)
				.player(this.starter.getName())
				.create());
		
	}
	
	public RestartService(CommandSender starter, int seconds, String reason) {
		if (instance != null) throw new IllegalAccessError("A restart is already scheduled.");
		
		instance = this;
		
		this.starter = starter;
		this.seconds = seconds;
		this.reason = reason;
		
		this.task = createTask();
		
		warnPlayers(seconds);
		
		Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.RESTART_REASONING)
				.player(this.starter.getName())
				.amount(this.seconds)
				.reason(this.reason)
				.player(this.starter.getName()), false);
		Messenger.printStatic(new Stringer(Messages.RESTART_REASONING)
				.player(this.starter.getName())
				.amount(this.seconds)
				.reason(this.reason)
				.player(this.starter.getName())
				.create());
	}
	
	protected void warnPlayers(int timeLeft) {
		Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.RESTART_WARNING)
				.player(this.starter.getName())
				.amount(timeLeft)
				.reason(this.reason)
				.player(this.starter.getName())
				.create(), false);
		Messenger.printStatic(new Stringer(Messages.RESTART_WARNING)
				.player(this.starter.getName())
				.amount(timeLeft)
				.reason(this.reason)
				.player(this.starter.getName())
				.create());
	}
	
	protected int createTask() {
		return Bukkit.getScheduler().scheduleSyncRepeatingTask(Tools.getInstance(), new Runnable() {
			
			private int delay = seconds;
			
			@Override
			public void run() {
				
				if (delay == 0) {
					Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.RESTART_NOW)
							.player(starter.getName())
							.amount(seconds)
							.reason(reason)
							.player(starter.getName())
							.create(), false);
					Messenger.printStatic(new Stringer(Messages.RESTART_NOW)
							.player(starter.getName())
							.amount(seconds)
							.reason(reason)
							.player(starter.getName())
							.create());
					
					restart();
					
				} else if (((seconds-delay)%Options.RESTART_WARNINGS.asInt() == 0
						|| delay-Options.RESTART_WARNINGS.asInt() <= 0) && delay != seconds) {
					warnPlayers(delay);
				}
				
				delay --;
			}
		}, 0, 20);
	}
	
	public void restart() {
		
		Bukkit.getScheduler().cancelTask(task);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
		
	}
	
	public static void cancel(CommandSender stopper) {
		
		Bukkit.getScheduler().cancelTask(instance.task);
		Tools.getInstance().getMessenger().broadcast(new Stringer(Messages.RESTART_STOPPED)
				.player(stopper.getName())
				.parse(stopper)
				.create(), false);
		Messenger.printStatic(new Stringer(Messages.RESTART_STOPPED)
				.player(stopper.getName())
				.parse(stopper)
				.create());
		instance = null;
		
	}
}
