package com.pike.messageserver.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackData {
    /**
     * Name of the material.
     */
    public String materialName;

    /**
     * Key of the material.
     */
    public String materialKey;

    /**
     * Namespace key of the material.
     */
    public String materialNameSpaceKey;
    
    /**
     * Index of the item in the inventory.
     */
    public int Index;

    /**
     * Count of the item in the inventory.
     */
    public int Count;

    /**
     * List of enchantments associated with the item.
     */
    public List<EnchantmentData> enchantments = new ArrayList<EnchantmentData>();

    @Override
    public String toString() {
        return "ItemStackData{" +
                "materialName='" + materialName + '\'' +
                ", materialKey='" + materialKey + '\'' +
                ", materialNameSpaceKey='" + materialNameSpaceKey + '\'' +
                ", Index=" + Index +
                ", Count=" + Count +
                '}';
    }

    /**
     * Converts an ItemStack object to an ItemStackData object, populating its fields, including material details,
     * enchantments, and index.
     *
     * @param  itemStack  the ItemStack object to convert
     * @param  index      the index value to assign
     * @return            the converted ItemStackData object
     */
    public static ItemStackData fromItemStack(ItemStack itemStack, int index) {
        ItemStackData itemStackData = new ItemStackData();
        Material itemStackMaterial = itemStack.getType();

        itemStackData.materialName = itemStackMaterial.name();
        itemStackData.materialKey = itemStackMaterial.getKey().getKey();
        itemStackData.materialNameSpaceKey = itemStackMaterial.getKey().getNamespace();
        itemStackData.Index = index;
        itemStackData.Count = itemStack.getAmount();

        // Fill Enchantments
        Map<Enchantment, Integer> enchantments = itemStack.getItemMeta().getEnchants();
        for (Enchantment enchantment : enchantments.keySet()) {
            EnchantmentData enchantmentData = EnchantmentData.fromItemStackData(enchantment, enchantments.get(enchantment));
            itemStackData.enchantments.add(enchantmentData);
        }
        return itemStackData;
    }

    /**
     * Converts the ItemStackData object to an ItemStack object, populating its fields, including enchantments.
     *
     * @return          the converted ItemStack object
     */
    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(materialNameSpaceKey + ":" + materialKey), Count);

        // Fill Enchantments
        if (enchantments!=null && !enchantments.isEmpty()) {
            ItemMeta meta = itemStack.getItemMeta();

            for (EnchantmentData enchantmentData : enchantments) {
                Enchantment enchantment = enchantmentData.toEnchantment();
                meta.addEnchant(enchantment, enchantmentData.level, true);
            }

            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
}
