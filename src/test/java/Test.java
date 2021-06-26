import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Test {

  public static void main(String[] args) {
    Component color = MiniMessage.get().parse("<red>Majekdor<white>");
    System.out.println(GsonComponentSerializer.gson().serialize(color));
    color = Component.empty().color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD, false).append(color);
    System.out.println(GsonComponentSerializer.gson().serialize(color));
  }
}
