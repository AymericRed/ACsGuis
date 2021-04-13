package fr.nico.sqript.forge.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class ScriptItemBase extends Item {

    public final String displayName;

    public ScriptItemBase(String displayName){
        super();
        this.displayName = displayName;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return displayName;
    }

}