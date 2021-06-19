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
import dev.majek.nicks.api.NicksApi;
import dev.majek.nicks.command.CommandNick;
import dev.majek.nicks.config.ConfigUpdater;
import dev.majek.nicks.config.JsonConfig;
import dev.majek.nicks.util.NicksUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Main plugin class.</p>
 * <p>Use {@link #core()} to access core plugin utilities such as nickname storage.</p>
 * <p>Use {@link #api()} to access api utilities such as event management.</p>
 */
public final class Nicks extends JavaPlugin {

  private static Nicks core;
  private static NicksApi api;
  private final NicksUtils utils;
  private final JsonConfig storage;
  private final Map<UUID, Component> nickMap;
  private boolean debug;

  public Nicks() {
    core = this;
    api = new NicksApi();
    utils = new NicksUtils();
    storage = new JsonConfig(getDataFolder(), "nicknames.json");
    try {
      storage.createConfig();
    } catch (FileNotFoundException e) {
      error("Error creating nicknames.json file:");
      e.printStackTrace();
    }
    nickMap = new HashMap<>();
  }

  @Override
  public void onEnable() {
    // Initialize configuration file
    reload();

    // Load nicknames from storage
    try {
      JsonObject jsonObject = storage.toJsonObject();
      for (String key : jsonObject.keySet())
        nickMap.put(UUID.fromString(key), GsonComponentSerializer.gson().deserializeFromTree(jsonObject.get(key)));
    } catch (IOException e) {
      error("Error loading nickname data from nicknames.json file:");
      e.printStackTrace();
    }
  }

  /**
   * Register plugin commands.
   */
  @SuppressWarnings("ConstantConditions")
  private void registerCommands() {
    getCommand("nick").setExecutor(new CommandNick());
    getCommand("nick").setTabCompleter(new CommandNick());
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
    if (core().debug)
      core().getLogger().warning(x.toString());
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
    saveDefaultConfig();
    File configFile = new File(core().getDataFolder(), "config.yml");
    try {
      ConfigUpdater.update(core(), "config.yml", configFile, Collections.emptyList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    reloadConfig();
    debug = getConfig().getBoolean("debug", false);
  }

  /**
   * Access various utility methods used in the plugin.
   *
   * @return NicksUtils.
   */
  public NicksUtils utils() {
    return utils;
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
   * Get a nickname from an online {@link Player}.
   *
   * @param player Player.
   * @return Nickname if it exists.
   */
  @Nullable
  public Component getNick(@NotNull Player player) {
    return getNick(player.getUniqueId());
  }

  /**
   * Get a nickname from an {@link OfflinePlayer}.
   *
   * @param player OfflinePlayer.
   * @return Nickname if it exists.
   */
  @Nullable
  public Component getNick(@NotNull OfflinePlayer player) {
    return getNick(player.getUniqueId());
  }

  /**
   * Set a user's nickname from their unique id. This will immediately be saved to Json.
   *
   * @param uuid User's unique id.
   * @param nick User's new nickname.
   */
  public void setNick(@NotNull UUID uuid, @NotNull Component nick) {
    nickMap.put(uuid, nick);
    saveNick(uuid);
  }

  /**
   * Set a user's nickname using an online {@link Player}. This will immediately be saved to Json.
   *
   * @param player Online player.
   * @param nick Player's new nickname.
   */
  public void setNick(@NotNull Player player, @NotNull Component nick) {
    setNick(player.getUniqueId(), nick);
  }

  /**
   * Set a user's nickname using an {@link OfflinePlayer}. This will immediately be saved to Json.
   *
   * @param player OfflinePlayer.
   * @param nick Player's new nickname.
   */
  public void setNick(@NotNull OfflinePlayer player, @NotNull Component nick) {
    setNick(player.getUniqueId(), nick);
  }

  /**
   * Save nickname to Json from a unique id. Primarily used internally. All <code>setNick(...)</code>
   * methods will call this after saving the nickname to the map. If this is called using a unique id
   * not in the map an error will be thrown.
   *
   * @param uuid Unique id of user who's nickname is being saved.
   * @throws NullPointerException Will be thrown if the unique id is not in the map.
   */
  @SuppressWarnings("ConstantConditions")
  @Internal
  public void saveNick(@NotNull UUID uuid) throws NullPointerException {
    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
      try {
        storage.putInJsonObject(uuid.toString(), GsonComponentSerializer.gson().serialize(getNick(uuid)));
      } catch (IOException e) {
        error("Error saving nickname to file: \nUUID: " + uuid);
        e.printStackTrace();
      }
    });
  }
}