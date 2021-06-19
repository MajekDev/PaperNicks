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
package dev.majek.nicks.command;

import dev.majek.nicks.Nicks;
import dev.majek.nicks.api.SetNickEvent;
import dev.majek.nicks.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Handles <code>/nick</code> command execution and tab completion.
 */
public class CommandNick implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                           @NotNull String[] args) {
    // Console cannot have a nickname
    if (!(sender instanceof Player)) {
      sendMessage(sender, "messages.invalidSender", "<red>You must be in-game to use this command.");
      return true;
    }
    Player player = (Player) sender;

    if (args.length == 0)
      return false;

    String nickInput = String.join(" ", args);

    // Check if we're supporting legacy
    if (Nicks.core().getConfig().getBoolean("legacy", false))
      nickInput = Nicks.core().utils().legacyToMini(nickInput);

    Component nickname = MiniMessage.get().parse(nickInput);
    String plainTextNick = PlainComponentSerializer.plain().serialize(nickname);
    int maxLength = Nicks.core().getConfig().getInt("max-length", 20);
    int minLength = Nicks.core().getConfig().getInt("min-length", 3);

    // Make sure the nickname isn't too short
    if (plainTextNick.length() < minLength) {
      sendMessage(player, "messages.tooShort", "<red>That nickname is too short. Minimum length is " +
          "%length% characters.", new Pair<>("%length%", String.valueOf(minLength)));
      return true;
    }

    // Make sure the nickname isn't too long
    if (plainTextNick.length() > maxLength) {
      sendMessage(player, "messages.tooLong", "<red>That nickname is too long. Maximum length is " +
          "%length% characters.", new Pair<>("%length%", String.valueOf(maxLength)));
      return true;
    }

    // Call event
    SetNickEvent nickEvent = new SetNickEvent(player, nickname, player.displayName());
    Nicks.api().callEvent(nickEvent);
    if (nickEvent.isCancelled())
      return true;

    // Set nick
    player.displayName(nickEvent.newNick());
    Nicks.core().setNick(player, nickEvent.newNick());

    // Should tab list be changed
    if (Nicks.core().getConfig().getBoolean("tab-nicks", false))
      player.playerListName(nickEvent.newNick());

    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String alias, @NotNull String[] args) {
    return Collections.emptyList();
  }

  @SafeVarargs
  public final void sendMessage(CommandSender sender, String path, String def, Pair<String, String>... replacements) {
    String message = Nicks.core().getConfig().getString(path, def);
    for (Pair<String, String> replacement : replacements)
      message = message.replace(replacement.getFirst(), replacement.getSecond());
    sender.sendMessage(MiniMessage.get().parse(message));
  }
}
