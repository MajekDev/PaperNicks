/*
 * This file is part of PaperNicks, licensed under the MIT License.
 *
 * Copyright (c) 2021 Majekdor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.majek.nicks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.majek.nicks.api.NicksApi;
import dev.majek.nicks.command.*;
import dev.majek.nicks.event.ChatFormatter;
import dev.majek.nicks.config.ConfigUpdater;
import dev.majek.nicks.config.JsonConfig;
import dev.majek.nicks.config.NicksConfig;
import dev.majek.nicks.event.PlayerJoin;
import dev.majek.nicks.util.NicksUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Main plugin class.</p>
 * <p>Use {@link #core()} to access core plugin utilities such as nickname storage.</p>
 * <p>Use {@link #api()} to access api utilities such as event management.</p>
 */
public final class Nicks extends JavaPlugin {

  private static Nicks                core;
  private static NicksApi             api;
  private static NicksUtils           utils;
  private static NicksConfig          config;
  private final JsonConfig            storage;
  private final Map<UUID, Component>  nickMap;

  /**
   * Initialize plugin.
   */
  public Nicks() {
    core = this;
    api = new NicksApi();
    utils = new NicksUtils();
    config = new NicksConfig();
    storage = new JsonConfig(getDataFolder(), "nicknames.json");
    try {
      storage.createConfig();
    } catch (FileNotFoundException e) {
      error("Error creating nicknames.json file:");
      e.printStackTrace();
    }
    nickMap = new HashMap<>();
  }

  /**
   * Plugin startup logic.
   */
  @Override
  public void onEnable() {
    // Initialize configuration file
    reload();

    // Load nicknames from storage
    try {
      JsonObject jsonObject = storage.toJsonObject();
      for (String key : jsonObject.keySet()) {
        nickMap.put(UUID.fromString(key), GsonComponentSerializer.gson()
            .deserializeFromTree(jsonObject.get(key)));
      }
    } catch (IOException e) {
      error("Error loading nickname data from nicknames.json file:");
      e.printStackTrace();
    }
    log("Successfully loaded nicknames from Json storage.");

    // Track metrics
    new Metrics(this, 11860);

    // Register plugin commands
    registerCommands();

    // Register events
    registerEvents(new PlayerJoin(), new ChatFormatter());
  }

  /**
   * Register plugin commands.
   */
  @SuppressWarnings("ConstantConditions")
  private void registerCommands() {
    getCommand("nick").setExecutor(new CommandNick());
    getCommand("nick").setTabCompleter(new CommandNick());
    getCommand("nonick").setExecutor(new CommandNoNick());
    getCommand("nonick").setTabCompleter(new CommandNoNick());
    getCommand("nickother").setExecutor(new CommandNickOther());
    getCommand("nickother").setTabCompleter(new CommandNickOther());
    getCommand("nickcolor").setExecutor(new CommandNickColor());
    getCommand("nickcolor").setTabCompleter(new CommandNickColor());
    getCommand("nicksreload").setExecutor(new CommandNicksReload());
    getCommand("nicksreload").setTabCompleter(new CommandNicksReload());
  }

  /**
   * Register plugin events.
   */
  private void registerEvents(Listener... listeners) {
    for (Listener listener : listeners) {
      getServer().getPluginManager().registerEvents(listener, this);
    }
  }

  /**
   * Get an instance of the main class. Use this for things like managing nicknames.
   *
   * @return Core.
   */
  public static Nicks core() {
    return core;
  }

  /**
   * Get the Nicks Api for accessing things such as event management and nickname lookup.
   *
   * @return Api.
   */
  public static NicksApi api() {
    return api;
  }

  /**
   * Access various utility methods used in the plugin.
   *
   * @return NicksUtils.
   */
  public static NicksUtils utils() {
    return utils;
  }

  /**
   * Easier access for plugin config options with defaults for redundancy.
   *
   * @return NicksConfig.
   */
  public static NicksConfig config() {
    return config;
  }

  /**
   * Log an object to console. This should be a non-critical message.
   *
   * @param x Object to log.
   */
  public static void log(@NotNull Object x) {
    core().getLogger().info(x.toString());
  }

  /**
   * Log an object to console. This will only be logged if debugging is enabled.
   *
   * @param x Object to log.
   */
  public static void debug(@NotNull Object x) {
    if (config().DEBUG) {
      core().getLogger().warning(x.toString());
    }
  }

  /**
   * Log an object to console. This should only be used for plugin errors.
   *
   * @param x Object to log.
   */
  public static void error(@NotNull Object x) {
    core().getLogger().severe(x.toString());
  }

  /**
   * Reload the plugin's configuration file.
   */
  public void reload() {
    debug("Reloading plugin...");
    saveDefaultConfig();
    File configFile = new File(core().getDataFolder(), "config.yml");
    try {
      ConfigUpdater.update(core(), "config.yml", configFile, Collections.emptyList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    reloadConfig();
    config().reload();
  }

  /**
   * Get the map that stores unique ids keyed to nicknames.
   *
   * @return NickMap.
   */
  public Map<UUID, Component> getNickMap() {
    return nickMap;
  }

  /**
   * Check whether or not there is a nickname stored for a unique id.
   *
   * @param uuid The unique id.
   * @return True if there is a nickname stored.
   */
  public boolean hasNick(@NotNull UUID uuid) {
    return nickMap.containsKey(uuid);
  }

  /**
   * Get a nickname from a unique id.
   *
   * @param uuid Unique id.
   * @return Nickname if it exists.
   */
  @Nullable
  public Component getNick(@NotNull UUID uuid) {
    return nickMap.get(uuid);
  }

  /**
   * Set a user's nickname using an online {@link Player}.
   * This will immediately be saved to Json.
   *
   * @param player Online player.
   * @param nick Player's new nickname.
   */
  public void setNick(@NotNull Player player, @NotNull Component nick) {
    nick = Component.empty().color(NamedTextColor.WHITE)
        .decoration(TextDecoration.BOLD, false).append(nick);
    nickMap.put(player.getUniqueId(), nick);
    player.displayName(nick);
    if (config().TAB_NICKS) {
      player.playerListName(nick);
    }
    saveNick(player.getUniqueId());
  }

  /**
   * Remove a nickname from the map and from Json storage.
   * It will be asynchronously removed from the file.
   *
   * @param uuid The unique id to remove.
   */
  public void removeNick(@NotNull UUID uuid) {
    nickMap.remove(uuid);
    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
      try {
        storage.removeFromJsonObject(uuid.toString());
        debug("Removed nickname from user " + uuid + " from json.");
      } catch (IOException e) {
        error("Error removing nickname from file \nUUID: " + uuid);
        e.printStackTrace();
      }
    });
  }

  /**
   * Save nickname to Json from a unique id. Primarily used internally.
   * All <code>setNick(...)</code> methods will call this after saving
   * the nickname to the map. If this is called using a unique id not
   * in the map an error will be thrown.
   *
   * @param uuid Unique id of user who's nickname is being saved.
   * @throws NullPointerException Will be thrown if the unique id is not in the map.
   */
  @SuppressWarnings("ConstantConditions")
  @Internal
  public void saveNick(@NotNull UUID uuid) throws NullPointerException {
    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
      try {
        storage.putInJsonObject(uuid.toString(),
            JsonParser.parseString(GsonComponentSerializer.gson().serialize(getNick(uuid))));
        debug("Saved nickname from user " + uuid + " to json.");
      } catch (IOException e) {
        error("Error saving nickname to file \nUUID: " + uuid);
        e.printStackTrace();
      }
    });
  }
}