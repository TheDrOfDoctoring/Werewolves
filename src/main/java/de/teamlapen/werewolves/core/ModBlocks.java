package de.teamlapen.werewolves.core;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;
import de.teamlapen.lib.lib.item.ItemMetaBlock;
import de.teamlapen.werewolves.blocks.BlockSilverOre;
import de.teamlapen.werewolves.blocks.WerewolfFlower;
import de.teamlapen.werewolves.util.REFERENCE;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import javax.annotation.Nonnull;

/**
 * Handles all block registrations and reference.
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class ModBlocks {

    public static final BlockSilverOre silver_ore = getNull();
    // TODO icon
    public static final WerewolfFlower werewolf_flower = getNull();

    static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.register(itemBlock(silver_ore));
        registry.register(new ItemMetaBlock(werewolf_flower));
    }

    private static @Nonnull ItemBlock itemBlock(@Nonnull Block b) {
        ItemBlock item = new ItemBlock(b);
        // noinspection ConstantConditions
        item.setRegistryName(b.getRegistryName());
        return item;
    }

    static void registerBlocks(IForgeRegistry<Block> registry) {
        registry.register(new BlockSilverOre());
        registry.register(new WerewolfFlower());
    }
}
