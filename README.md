# PaperNicks by Majekdor
PaperNicks is a nickname plugin designed exclusively for [PaperMC](https://github.com/PaperMC/Paper) 1.16.5 and above. It will run perfectly fine on Minecraft 1.17. It uses [MiniMessage](https://github.com/KyoriPowered/adventure-text-minimessage) for colors in nicknames and stores nicknames as [adventure](https://github.com/KyoriPowered/adventure) components.

## Commands

There are 5 plugin commands:
- `/nick <nickname>` - Set your own nickname.
- `/nickother <player> <nickname>` - Set another player's nickname.
- `/nonick [player]` - Remove your nickname or another player's nickname.
- `/nickcolor <color>` - Change the color of your nickname.
- `/nicksreload` - Reload the plugin.

## Permissions

`papernicks.nick`, `papernicks.nonick`, and `papernicks.nickcolor` are given to all players by default but can be negated by a permissions manager like [LuckPerms](https://luckperms.net/).
- `papernicks.nick` - Permission to change your own nickname.
- `papernicks.nick.other` - Permissions to change other player's nicknames.
- `papernicks.nonick` - Permission to remove your own nickname.
- `papernicks.nonick.other` - Permission to remove other player's nicknames.
- `papernicks.nickcolor` - Permission to change your nickname color.
- `papernicks.reload` - Permission to reload the plugin.

## For the nerds... I mean devs :P

PaperNicks does have an api and all commands trigger an event when executed. These events can be listened to the same way as other Bukkit events. You can see the events [here](https://github.com/Majekdor/PaperNicks/tree/master/src/main/java/dev/majek/nicks/api). No JavaDocs yet.

Event example:
```java
@EventHandler
public void onNickname(SetNickEvent event) {
  Player player = event.player();
  player.sendMessage("Setting nickname...");
  event.newNick(Component.text("New nickname"));
}
```

There are multiple ways to retrieve nicknames, but the easiest way is:
```java
Nicks.api().getNick(player); // You can pass thru a player, offlineplayer, or uuid
```

## Support

If you need help with the plugin and can't find the answer here or on Spigot, then the best way to get help is to join my [Discord](https://discord.gg/CGgvDUz). Make sure you read the frequently-asked channel before posting in the bug-reports channel (if it's a bug) or in the simple-homes channel (for general help).

If you have discovered a bug you can either join my [Discord](https://discord.gg/CGgvDUz) and report it there or open an issue here on GitHub. Please do not message me on Spigot in regard to a bug, there are easier ways to communicate.


## Contributing

SimpleHomes is open-source and licensed under the [MIT License](https://github.com/Majekdor/SimpleHomes/blob/main/LICENSE), so if you want to use any code contained in the plugin or clone the repository and make some changes, go ahead!

If you've found a bug within the plugin and would like to just make the changes to fix it yourself, you're free to do so and make a pull request here on GitHub. If you make significant contributions to the project, and by significant I mean one little PR to fix a tiny bug doesn't count as significant, you can earn the Contributor role in my [Discord](https://discord.gg/CGgvDUz).


## Donate

I'm a full time college student who makes and supports these plugins in my free time (when I have any). As a long time supporter of open source, most of my plugins are free. If you enjoy my plugins and would like to support me, you can buy me coffee over on  [PayPal](https://paypal.com/paypalme/majekdor). Donations of any amount are appreciated and a donation of $10 or more will get you the Supporter role in my [Discord](https://discord.gg/CGgvDUz)!
