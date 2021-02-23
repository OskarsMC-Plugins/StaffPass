package com.oskarsmc.staffpass;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class StaffPass extends JavaPlugin implements Listener {
    public boolean joinMessageEnabled;
    public Component joinMessage;

    private BukkitAudiences adventure;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this,this);

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        joinMessageEnabled = this.getConfig().getBoolean("bypass-message.enabled");
        this.adventure = BukkitAudiences.create(this);
        try {
            joinMessage = MiniMessage.get().parse(Objects.requireNonNull(this.getConfig().getString("bypass-message.message")));
            Bukkit.getLogger().info("Loaded!");
        } catch (NullPointerException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Error occurred trying to enable plugin: Bypass message is null");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @EventHandler
    public void playerLoginEvent(PlayerLoginEvent event) {
        if (event.getPlayer().hasPermission("staffpass.bypass")) {
            adventure().player(event.getPlayer()).sendMessage(joinMessage);
            event.allow();
        } else {
            if (getFreeSlots() > 0) {
                event.allow();
            }
        }
    }

    public int getFreeSlots() {
        int playerCount = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("staffpass.bypass")) {
            } else {
                playerCount++;
            }
        }
        return getServer().getMaxPlayers() - playerCount;
    }

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
    }
}
