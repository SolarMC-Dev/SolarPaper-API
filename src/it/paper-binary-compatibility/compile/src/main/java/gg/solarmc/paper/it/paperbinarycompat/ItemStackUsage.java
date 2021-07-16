package gg.solarmc.paper.it.paperbinarycompat;

import org.bukkit.inventory.ItemStack;

public class ItemStackUsage {

    public byte[] serialize(ItemStack itemStack) {
        return itemStack.serializeAsBytes();
    }

    public ItemStack deserialize(byte[] itemStackData) {
        return ItemStack.deserializeBytes(itemStackData);
    }
}
