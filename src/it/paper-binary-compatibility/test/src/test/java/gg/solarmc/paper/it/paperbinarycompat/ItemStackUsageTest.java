package gg.solarmc.paper.it.paperbinarycompat;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemStackUsageTest {

    private final ItemFactory itemFactory;
    private final ItemStackUsage itemStackUsage = new ItemStackUsage();

    public ItemStackUsageTest(@Mock ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }

    @BeforeEach
    public void setServer(@Mock Server server) {
        lenient().when(server.getItemFactory()).thenReturn(itemFactory);
        Bukkit.setServer(server);
    }

    private ItemStack newItem(Material material) {
        return new ItemStack(material) {
            @Override
            protected ItemFactory itemFactory() {
                return itemFactory;
            }
        };
    }

    private static byte[] randomData() {
        var tlr = ThreadLocalRandom.current();
        byte[] data = new byte[tlr.nextInt(1, 20)];
        tlr.nextBytes(data);
        return data;
    }

    @Test
    public void serialize() {
        byte[] expectedData = randomData();
        ItemStack itemStack = newItem(Material.STONE);
        when(itemFactory.serializeAsBytes(itemStack)).thenReturn(expectedData);
        assertArrayEquals(expectedData, itemStackUsage.serialize(itemStack));
        verify(itemFactory).serializeAsBytes(itemStack);
    }

    @Test
    public void deserialize() {
        ItemStack expectedItemStack = newItem(Material.GRASS);
        byte[] data = randomData();
        when(itemFactory.deserializeBytes(data)).thenReturn(expectedItemStack);
        assertEquals(expectedItemStack, itemStackUsage.deserialize(data));
        verify(itemFactory).deserializeBytes(data);
    }

    @AfterEach
    public void resetServer() {
        Bukkit.resetServer();
    }

}
