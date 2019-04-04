package com.rifledluffy.chairs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.rifledluffy.chairs.config.ConfigManager;
import com.rifledluffy.chairs.messages.MessageConstruct;
import com.rifledluffy.chairs.messages.MessageEvent;
import com.rifledluffy.chairs.messages.MessageType;
import com.rifledluffy.chairs.messages.Messages;
import com.rifledluffy.chairs.utility.Util;

public class MessageManager implements Listener {
	
	private RFChairs plugin = RFChairs.getPlugin(RFChairs.class);
	private ConfigManager configManager = plugin.getConfigManager();
	public FileConfiguration messages = configManager.getMessages();
	private boolean allowMessages;
	private int tempMuteDuration;
	
	public List<UUID> muted = new ArrayList<UUID>();
	private List<UUID> tempMute = new ArrayList<UUID>();
	
	public void reload(RFChairs plugin) {
		configManager = plugin.getConfigManager();
		messages = configManager.getMessages();
		allowMessages = configManager.getConfig().getBoolean("allow-custom-messages", true);
		tempMuteDuration = messages.getInt("temp-mute-duration", 0);
	}
	
	private void tempMute(Player player) {
		int duration = tempMuteDuration;
		tempMute.add(player.getUniqueId());
		BukkitRunnable runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (tempMuted(player)) tempMute.remove(player.getUniqueId());
			}
		};
		runnable.runTaskLater(plugin, duration);
	}
	
	private boolean tempMuted(Player player) {
		return tempMute.contains(player.getUniqueId());
	}
	
	@EventHandler
	public void onMessage(MessageEvent event) {
		Player player = event.getPlayer();
		if (tempMute.contains(player.getUniqueId())) event.setCancelled(true);
		if (event.isCancelled()) return;
		if (tempMuteDuration > 0) tempMute(player);
		if (allowMessages && !muted.contains(player.getUniqueId())) {
			String string = processString(event);
			if (!string.isEmpty()) player.sendMessage(string);
		}
	}
	
	private String processString(MessageEvent event) {
		MessageType type = event.getType();
		MessageConstruct construct = event.getConstruct();
		String string;

		String stringOutput = Messages.getMessage(type);
		if (stringOutput == null) {
			string = Messages.getDefault(type);
		} else {
			string = stringOutput;
		}

		string = messages.getString(string);

		String finalString = "";
		
		if (construct == MessageConstruct.SINGLE) finalString = Util.replaceMessage(event.getPlayer(), string);
		else if (construct == MessageConstruct.DEFENSIVE) finalString = Util.replaceMessage(event.getEntity(), event.getPlayer(), string);
		else if (construct == MessageConstruct.OFFENSIVE && event.getEntity() instanceof Player) finalString = Util.replaceMessage(event.getPlayer(),(Player) event.getEntity(), string);
		return finalString;
	}
	
	void saveMuted() {
		List<String> ids = new ArrayList<String>();
		if (muted == null || muted.size() == 0) configManager.getData().set("Muted", new ArrayList<String>());
		for (UUID id : muted) ids.add(id.toString());
		plugin.getServer().getLogger().info("[RFChairs] Saving " + ids.size() + " Players that had events muted.");
		configManager.getData().set("Muted", ids);
	}
	
	void loadMuted() {
		List<String> muted = configManager.getData().getStringList("Muted");
		if (muted == null || muted.size() == 0) return;
		plugin.getServer().getLogger().info("[RFChairs] " + muted.size() + " Players had events muted off. Adding Them...");
		for (String toggler : muted) {
			UUID id = UUID.fromString(toggler);
			this.muted.add(id);
		}
		configManager.getData().set("Muted", new ArrayList<String>());
	}

}
