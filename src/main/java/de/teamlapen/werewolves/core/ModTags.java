package de.teamlapen.werewolves.core;

import de.teamlapen.werewolves.util.REFERENCE;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("SameParameterValue")
public class ModTags {
    public static class Blocks extends de.teamlapen.vampirism.core.ModTags.Blocks {
        public static final TagKey<Block> SILVER_ORE = forge("ores/silver");

        private static TagKey<Block> mc(ResourceLocation id) {
            return BlockTags.create(id);
        }

        private static TagKey<Block> werewolves(String id) {
            return BlockTags.create(new ResourceLocation(REFERENCE.MODID, id));
        }

        private static TagKey<Block> forge(String id) {
            return BlockTags.create(new ResourceLocation("forge", id));
        }
    }

    public static class Items extends de.teamlapen.vampirism.core.ModTags.Items {
        public static final TagKey<Item> SILVER_ORE = forge("ores/silver");
        public static final TagKey<Item> SILVER_INGOT = forge("ingots/silver");
        public static final TagKey<Item> SILVER_NUGGET = forge("nuggets/silver");
        public static final TagKey<Item> RAWMEATS = forge("rawmeats");
        public static final TagKey<Item> COOKEDMEATS = forge("cookedmeats");
        public static final TagKey<Item> SILVER_TOOL = werewolves("tools/silver");

        private static TagKey<Item> mc(ResourceLocation id) {
            return ItemTags.create(id);
        }

        private static TagKey<Item> werewolves(String id) {
            return ItemTags.create(new ResourceLocation(REFERENCE.MODID, id));
        }

        private static TagKey<Item> forge(String id) {
            return ItemTags.create(new ResourceLocation("forge", id));
        }
    }

    public static class Biomes {
        public static final TagKey<Biome> WEREWOLF_BIOME = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(REFERENCE.MODID, "werewolf_biome"));
    }
}
