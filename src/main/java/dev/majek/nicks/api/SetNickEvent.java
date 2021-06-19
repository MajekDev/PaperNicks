package dev.majek.nicks.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles the event fired when a player sets their nickname.
 */
public class SetNickEvent extends Event implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player player;
  private final Component oldNick;
  private Component newNick;
  private boolean canceled;

  /**
   * Fires when a player changes their nickname using <code>/nick</code>.
   *
   * @param player  The in-game player changing the nickname.
   * @param newNick The new nickname the player is attempting to set.
   * @param oldNick The player's old name if they had one.
   */
  public SetNickEvent(@NotNull Player player, @NotNull Component newNick, @Nullable Component oldNick) {
    this.player = player;
    this.newNick = newNick;
    this.oldNick = oldNick;
    this.canceled = false;
  }

  /**
   * The in-game player attempting to change the nickname.
   *
   * @return Player.
   */
  public Player player() {
    return player;
  }

  /**
   * The old nickname if the player had one previously
   *
   * @return Old nickname.
   */
  @Nullable
  public Component oldNick() {
    return oldNick;
  }

  /**
   * Set the player's new nickname.
   *
   * @param newNick New nickname.
   */
  public void newNick(@NotNull Component newNick) {
    this.newNick = newNick;
  }

  /**
   * The new nickname the player is attempting to set.
   *
   * @return New nickname.
   */
  public Component newNick() {
    return newNick;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isCancelled() {
    return canceled;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCancelled(boolean cancel) {
    this.canceled = cancel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  /**
   * Get the HandlerList. Bukkit requires this.
   *
   * @return HandlerList.
   */
  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }
}