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
import dev.majek.nicks.api.NoNickEvent;
import dev.majek.nicks.api.NoNickOtherEvent;
import dev.majek.nicks.config.NicksMessages;
import dev.majek.nicks.util.TabCompleterBase;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nonick</code> command execution and tab completion.
 */
public class CommandNoNick implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (args.length == 0) {
      // Console has no nickname
      if (!(sender instanceof Player player)) {
        NicksMessages.INVALID_SENDER.send(sender);
        return true;
      }

      // Call event
      NoNickEvent noNickEvent = new NoNickEvent(player, player.displayName());
      Nicks.api().callEvent(noNickEvent);
      if (noNickEvent.isCancelled()) {
        return true;
      }

      // Set nickname to player's name
      player.displayName(Component.text(player.getName()));
      if (Nicks.config().TAB_NICKS) {
        player.playerListName(Component.text(player.getName()));
      }
      Nicks.core().removeNick(player.getUniqueId());
      NicksMessages.NICKNAME_REMOVED.send(player);

    } else {

      // Make sure the sender has permission to remove another player's nickname
      if (!sender.hasPermission("papernicks.nonick.other")) {
        NicksMessages.NO_PERMISSION.send(sender);
        return true;
      }

      // Make sure the target player is online
      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
        NicksMessages.UNKNOWN_PLAYER.send(sender, args[0]);
        return true;
      }

      // Call event
      NoNickOtherEvent event = new NoNickOtherEvent(sender, target, target.displayName());
      Nicks.api().callEvent(event);
      if (event.isCancelled()) {
        return true;
      }

      // Remove nickname
      target.displayName(Component.text(target.getName()));
      if (Nicks.config().TAB_NICKS) {
        target.playerListName(Component.text(target.getName()));
      }
      Nicks.core().removeNick(target.getUniqueId());
      NicksMessages.NICKNAME_REMOVED_OTHER.send(sender, target);
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
    if (args.length == 1) {
      return TabCompleterBase.getOnlinePlayers(args[0]);
    } else {
      return Collections.emptyList();
    }
  }
}