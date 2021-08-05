package me.jeleyka.multiutils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class SimpleItemStack extends ItemStack {

    public SimpleItemStack(Material type) {
        super(type);
    }

    public SimpleItemStack enchant(Enchantment enchantment, int level) {
        addEnchantment(enchantment, level);
        return this;
    }
}
