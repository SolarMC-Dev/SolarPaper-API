package org.bukkit;

import net.kyori.adventure.key.Key;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Solar - whole class, revitalize and add more tests
public class NamespacedKeyTest {

    @Test
    public void testValid() {
        assertEquals("minecraft:foo", new NamespacedKey("minecraft", "foo").toString());
        assertEquals("minecraft:foo/bar", new NamespacedKey("minecraft", "foo/bar").toString());
        assertEquals("minecraft:foo/bar_baz", new NamespacedKey("minecraft", "foo/bar_baz").toString());
        assertEquals("minecraft:foo/bar_baz-qux", new NamespacedKey("minecraft", "foo/bar_baz-qux").toString());
        assertEquals("minecraft:foo/bar_baz-qux.quux", new NamespacedKey("minecraft", "foo/bar_baz-qux.quux").toString());
    }

    @Test
    public void testEmptyNamespace() {
        assertThrows(IllegalArgumentException.class, () -> new NamespacedKey("", "foo"));
    }

    @Test
    public void testEmptyKey() {
        assertThrows(IllegalArgumentException.class, () -> new NamespacedKey("minecraft", ""));
    }

    @Test
    public void testInvalidNamespace() {
        assertThrows(IllegalArgumentException.class, () -> new NamespacedKey("minecraft/test", "foo"));
    }

    @Test
    public void testInvalidKey() {
        assertThrows(IllegalArgumentException.class, () -> new NamespacedKey("minecraft", "foo!"));
    }

    @Test
    public void testBelowLength() {
        new NamespacedKey("loremipsumdolorsitametconsecteturadipiscingelitduisvolutpatvelitsitametmaximusscelerisquemorbiullamcorperexacconsequategestas",
                "loremipsumdolorsitametconsecteturadipiscingelitduisvolutpatvelitsitametmaximusscelerisquemorbiullamcorperexacconsequategestas").toString();
    }

    @Test
    public void testAboveLength() {
        assertThrows(IllegalArgumentException.class, () -> new NamespacedKey("loremipsumdolorsitametconsecteturadipiscingelitduisvolutpatvelitsitametmaximusscelerisquemorbiullamcorperexacconsequategestas",
                "loremipsumdolorsitametconsecteturadipiscingelitduisvolutpatvelitsitametmaximusscelerisquemorbiullamcorperexacconsequategestas/"
                + "loremipsumdolorsitametconsecteturadipiscingelitduisvolutpatvelitsitametmaximusscelerisquemorbiullamcorperexacconsequategestas"));
    }

    @Test
    public void adventureKey() {
        Key adventureKey = Key.key(Key.MINECRAFT_NAMESPACE, "value");
        NamespacedKey bukkitKey = new NamespacedKey(NamespacedKey.MINECRAFT, "value");
        assertEquals(adventureKey.namespace(), bukkitKey.namespace());
        assertEquals(adventureKey.value(), bukkitKey.value());
    }

    @Test
    public void adventureKeyAsString() {
        Key adventureKey = Key.key(Key.MINECRAFT_NAMESPACE, "value");
        NamespacedKey bukkitKey = new NamespacedKey(NamespacedKey.MINECRAFT, "value");
        assertEquals(adventureKey.asString(), bukkitKey.asString());
    }

}
