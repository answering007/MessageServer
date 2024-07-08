package com.pike.messageserver.data;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentData {
    /**
     * The key of the Enchantment in the EnchantmentData object.
     */
    public String key;

    /**
     * The namespace of the Enchantment in the EnchantmentData object.
     */
    public String nameSpace;

    /**
     * The level of the Enchantment in the EnchantmentData object.
     */
    public Integer level;

    @Override
    public String toString() {
        return "EnchantmentData{" + key + ", " + nameSpace + ", " + level + "}";
    }

    /**
     * Creates an EnchantmentData object from an Enchantment and a level.
     *
     * @param  enchantment  the Enchantment to create the EnchantmentData from
     * @param  level        the level of the Enchantment
     * @return              the created EnchantmentData object
     */
    public static EnchantmentData fromItemStackData(Enchantment enchantment, int level) {
        EnchantmentData enchantmentData = new EnchantmentData();
        NamespacedKey nsKey = enchantment.getKey();
        
        enchantmentData.key = nsKey.getKey();
        enchantmentData.nameSpace = nsKey.getNamespace();
        enchantmentData.level = level;
        
        return enchantmentData;
    }

    /**
     * Converts the EnchantmentData object to an Enchantment object.
     *
     * @return the Enchantment object corresponding to the key in the EnchantmentData object
     */
    public Enchantment toEnchantment() {
        return Enchantment.getByKey(NamespacedKey.minecraft(key));
    }
}
