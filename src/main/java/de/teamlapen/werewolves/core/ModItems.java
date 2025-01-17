package de.teamlapen.werewolves.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IEntityCrossbowArrow;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.werewolves.api.WReference;
import de.teamlapen.werewolves.config.WerewolvesConfig;
import de.teamlapen.werewolves.effects.SilverEffect;
import de.teamlapen.werewolves.items.*;
import de.teamlapen.werewolves.misc.WerewolvesCreativeTab;
import de.teamlapen.werewolves.util.Helper;
import de.teamlapen.werewolves.util.REFERENCE;
import de.teamlapen.werewolves.util.WUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.Keys.ITEMS, REFERENCE.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, REFERENCE.MODID);

    private static final Set<RegistryObject<? extends Item>> WEREWOLVES_TAB_ITEMS = new HashSet<>();
    private static final Map<ResourceKey<CreativeModeTab>, Set<RegistryObject<? extends Item>>> CREATIVE_TAB_ITEMS = new HashMap<>();

    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(REFERENCE.MODID, "default"));
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(CREATIVE_TAB_KEY.location().getPath(), () -> WerewolvesCreativeTab.builder(WEREWOLVES_TAB_ITEMS.stream().map(RegistryObject::get).collect(Collectors.toSet())).build());

    public static final RegistryObject<Item> SILVER_INGOT = register("silver_ingot", () -> new Item(props()));
    public static final RegistryObject<HoeItem> SILVER_HOE = register("silver_hoe", () -> new HoeItem(WUtils.SILVER_ITEM_TIER, -1, -1.0F, props()));
    public static final RegistryObject<ShovelItem> SILVER_SHOVEL = register("silver_shovel", () -> new ShovelItem(WUtils.SILVER_ITEM_TIER, 1.5F, -3.0F, props()));
    public static final RegistryObject<AxeItem> SILVER_AXE = register("silver_axe", () -> new AxeItem(WUtils.SILVER_ITEM_TIER, 6.0F, -3.1F, props()));
    public static final RegistryObject<PickaxeItem> SILVER_PICKAXE = register("silver_pickaxe", () -> new PickaxeItem(WUtils.SILVER_ITEM_TIER, 1, -2.8F, props()));
    public static final RegistryObject<SilverSwordItem> SILVER_SWORD = register("silver_sword", () -> new SilverSwordItem(WUtils.SILVER_ITEM_TIER, 3, -2.4F, props()));
    public static final RegistryObject<CrossbowArrowItem> CROSSBOW_ARROW_SILVER_BOLT = register("crossbow_arrow_silver_bolt", () -> new CrossbowArrowItem(new CrossbowArrowItem.ArrowType("silver_bolt", 3, 0xc0c0c0, true, true) {
        @Override
        public void onHitEntity(ItemStack arrow, LivingEntity entity, IEntityCrossbowArrow arrowEntity, Entity shootingEntity) {
            if (Helper.isWerewolf(entity)) {
                entity.addEffect(SilverEffect.createEffect(entity, WerewolvesConfig.BALANCE.UTIL.silverBoltEffectDuration.get() * 20));
            }
        }
    }));
    public static final RegistryObject<LiverItem> LIVER = register("liver", LiverItem::new);
    public static final RegistryObject<Item> CRACKED_BONE = register("cracked_bone", () -> new Item(props()));
    public static final RegistryObject<UnWerewolfInjectionItem> INJECTION_UN_WEREWOLF = register("injection_un_werewolf", UnWerewolfInjectionItem::new);
    public static final RegistryObject<WerewolfToothItem> WEREWOLF_TOOTH = register("werewolf_tooth", WerewolfToothItem::new);
    public static final RegistryObject<Item> SILVER_NUGGET = register("silver_nugget", () -> new Item(props()));
    public static final RegistryObject<Item> RAW_SILVER = register("raw_silver", () -> new Item(props()));

    public static final RegistryObject<Item> WEREWOLF_MINION_CHARM = register("werewolf_minion_charm", () -> new Item(props()) {
        @Override
        public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltips, @Nonnull TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltips, flag);
            tooltips.add(Component.translatable("item.werewolves.moon_charm.desc").withStyle(ChatFormatting.DARK_GRAY));

        }
    });
    public static final RegistryObject<WerewolfMinionUpgradeItem> WEREWOLF_MINION_UPGRADE_SIMPLE = register("werewolf_minion_upgrade_simple", () -> new WerewolfMinionUpgradeItem(props(), 1, 2));
    public static final RegistryObject<WerewolfMinionUpgradeItem> WEREWOLF_MINION_UPGRADE_ENHANCED = register("werewolf_minion_upgrade_enhanced", () -> new WerewolfMinionUpgradeItem(props(), 3, 4));
    public static final RegistryObject<WerewolfMinionUpgradeItem> WEREWOLF_MINION_UPGRADE_SPECIAL = register("werewolf_minion_upgrade_special", () -> new WerewolfMinionUpgradeItem(props(), 5, 6));

    public static final RegistryObject<SpawnEggItem> WEREWOLF_BEAST_SPAWN_EGG = register("werewolf_beast_spawn_egg", CreativeModeTabs.SPAWN_EGGS, () -> new ForgeSpawnEggItem(ModEntities.WEREWOLF_BEAST, 0xffc800, 0xfaab00, props()));
    public static final RegistryObject<SpawnEggItem> WEREWOLF_SURVIVALIST_SPAWN_EGG = register("werewolf_survivalist_spawn_egg", CreativeModeTabs.SPAWN_EGGS,() -> new ForgeSpawnEggItem(ModEntities.WEREWOLF_SURVIVALIST, 0xffc800, 0xfae700, props()));
    public static final RegistryObject<SpawnEggItem> HUMAN_WEREWOLF_SPAWN_EGG = register("human_werewolf_spawn_egg", CreativeModeTabs.SPAWN_EGGS,() -> new ForgeSpawnEggItem(ModEntities.HUMAN_WEREWOLF, 0xffc800, 0xa8a8a8, props()));
    public static final RegistryObject<SpawnEggItem> ALPHA_WEREWOLF_SPAWN_EGG = register("alpha_werewolf_spawn_egg", CreativeModeTabs.SPAWN_EGGS,() -> new ForgeSpawnEggItem(ModEntities.ALPHA_WEREWOLF, 0xffc800, 0xca0f00, props()));

    public static final RegistryObject<WerewolfRefinementItem> BONE_NECKLACE = register("bone_necklace", () -> new WerewolfRefinementItem(props(), IRefinementItem.AccessorySlotType.AMULET));
    public static final RegistryObject<WerewolfRefinementItem> CHARM_BRACELET = register("charm_bracelet", () -> new WerewolfRefinementItem(props(), IRefinementItem.AccessorySlotType.RING));
    public static final RegistryObject<WerewolfRefinementItem> DREAM_CATCHER = register("dream_catcher", () -> new WerewolfRefinementItem(props(), IRefinementItem.AccessorySlotType.OBI_BELT));

    public static class V {
        public static final RegistryObject<Item> HUMAN_HEART = item("human_heart");
        public static final RegistryObject<Item> INJECTION_EMPTY = item("injection_empty");
        public static final RegistryObject<Item> WEAK_HUMAN_HEART = item("weak_human_heart");
        public static final RegistryObject<Item> OBLIVION_POTION = item("oblivion_potion");
        public static final RegistryObject<Item> VAMPIRE_BOOK = item("vampire_book");
        public static final RegistryObject<Item> CROSSBOW_ARROW_NORMAL = item("crossbow_arrow_normal");

        private static RegistryObject<Item> item(String name) {
            return RegistryObject.create(new ResourceLocation("vampirism", name), ForgeRegistries.Keys.ITEMS, REFERENCE.MODID);
        }

        private static void init() {

        }
    }

    static <I extends Item> RegistryObject<I> register(final String name, ResourceKey<CreativeModeTab> tab, final Supplier<? extends I> sup) {
        RegistryObject<I> item = ITEMS.register(name, sup);
        if (tab == CREATIVE_TAB_KEY) {
            WEREWOLVES_TAB_ITEMS.add(item);
        } else {
            CREATIVE_TAB_ITEMS.computeIfAbsent(tab, (a) -> new HashSet<>()).add(item);
        }
        return item;
    }

    static <I extends Item> RegistryObject<I> register(String name, Supplier<? extends I> sup) {
        return register(name, CREATIVE_TAB_KEY, sup);
    }

    static {
        V.init();
    }

    static void register(IEventBus bus) {
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
    }

    public static void remapItems(MissingMappingsEvent event) {
        event.getAllMappings(ForgeRegistries.Keys.ITEMS).forEach(missingMapping -> {
            switch (missingMapping.getKey().toString()) {
                case "werewolves:bone" -> missingMapping.remap(ModItems.CRACKED_BONE.get());
                case "werewolves:silver_oil" -> missingMapping.remap(de.teamlapen.vampirism.core.ModItems.OIL_BOTTLE.get());
            }
        });
    }

    @ApiStatus.Internal
    public static void registerOtherCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        CREATIVE_TAB_ITEMS.forEach((tab, items) -> {
            if (event.getTabKey() == tab) {
                items.forEach(item -> event.accept(item.get()));
            }
        });
    }

    private static Item.Properties props() {
        return new Item.Properties();
    }
}
