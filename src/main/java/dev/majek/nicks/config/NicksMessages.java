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

package dev.majek.nicks.config;

import dev.majek.nicks.Nicks;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

/**
 * Handles all plugin messages.
 */
public interface NicksMessages {

  Args0 INVALID_SENDER = () -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.invalidSender", "<red>You must be in-game to use this command."));

  Args1<String> UNKNOWN_PLAYER = playerName -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.unknownPlayer", "<red>Unknown player %player%."))
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(playerName).build());

  Args0 NO_PERMISSION = () -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.noPermission", "<red>You do not have permission to execute this command."));

  Args1<Integer> TOO_SHORT = minLength -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.tooShort", "<red>That nickname is too short. Minimum length is "
          + "%length% characters.")).replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
      .replacement(String.valueOf(minLength)).build());

  Args1<Integer> TOO_LONG = maxLength -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.tooLong", "<red>That nickname is too long. Maximum length is "
          + "%length% characters.")).replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
      .replacement(String.valueOf(maxLength)).build());

  Args0 NON_ALPHANUMERIC = () -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.nonAlphanumeric", "<red>Your nickname must be alphanumeric."));

  Args1<Component> NICKNAME_SET = nickname -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.nicknameSet", "<gray>Your nickname has been set to: <white>%nick%<gray>."))
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args2<Player, Component> NICKNAME_SET_OTHER = (player, nickname) -> MiniMessage.get().parse(Nicks.core()
      .getConfig().getString("messages.nicknameSetOther", "<aqua>%player%<gray>'s "
          + "nickname has been set to: <white>%nick%<gray>."))
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(player.getName()).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args0 NICKNAME_REMOVED = () -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.nicknameRemoved", "<gray>Nickname removed."));

  Args1<Player> NICKNAME_REMOVED_OTHER = target -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.nicknameRemovedOther", "<aqua>%player%<gray>'s nickname removed."))
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(target.getName()).build());

  Args0 ONLY_COLOR_CODES = () -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.onlyColorCodes", "<red>You may only include color codes."));

  Args0 PLUGIN_RELOADED = () -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString("messages.pluginReloaded", "<green>Plugin reloaded."));

  interface Args0 {
    Component build();

    default void send(Audience audience) {
      audience.sendMessage(build());
    }
  }

  interface Args1<A0> {
    Component build(A0 arg0);

    default void send(Audience audience, A0 arg0) {
      audience.sendMessage(build(arg0));
    }
  }

  interface Args2<A0, A1> {
    Component build(A0 arg0, A1 arg1);

    default void send(Audience audience, A0 arg0, A1 arg1) {
      audience.sendMessage(build(arg0, arg1));
    }
  }
}