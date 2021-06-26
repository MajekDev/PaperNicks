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
import dev.majek.nicks.api.NickColorEvent;
import dev.majek.nicks.config.NicksMessages;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nickcolor</code> command execution and tab completion.
 */
public class CommandNickColor implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    // Console cannot have a nickname
    if (!(sender instanceof Player player)) {
      NicksMessages.INVALID_SENDER.send(sender);
      return true;
    }

    if (args.length == 0) {
      return false;
    }

    String nickInput = String.join(" ", args);

    // Check if we're supporting legacy
    if (Nicks.config().LEGACY_COLORS) {
      nickInput = Nicks.utils().legacyToMini(nickInput);
    }

    // If there are no colors the length should be 0
    String plainTextInput = PlainTextComponentSerializer.plainText()
        .serialize(MiniMessage.get().parse(nickInput));
    if (plainTextInput.length() > 0) {
      NicksMessages.ONLY_COLOR_CODES.send(player);
      return true;
    }

    // Get the players current nickname to apply color codes to
    String plainTextNick = PlainTextComponentSerializer.plainText().serialize(player.displayName());
    Component nickname = MiniMessage.get().parse(nickInput + plainTextNick);

    // Call event
    NickColorEvent colorEvent = new NickColorEvent(player, nickname, player.displayName());
    Nicks.api().callEvent(colorEvent);
    if (colorEvent.isCancelled()) {
      return true;
    }

    // Set nick
    Nicks.core().setNick(player, colorEvent.newNick());
    NicksMessages.NICKNAME_SET.send(player, colorEvent.newNick());

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    return Collections.emptyList();
  }
}