import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Test {

  public static void main(String[] args) {
    Component color = MiniMessage.get().parse("<gradient:#1eae98:#d8b5ff>Majekdor");
    String legacy = LegacyComponentSerializer.builder().hexColors().build().serialize(color);
    System.out.println(color);
    System.out.println(legacy);
  }
}
