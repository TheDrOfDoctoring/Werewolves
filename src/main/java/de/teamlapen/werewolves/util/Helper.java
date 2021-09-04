package de.teamlapen.werewolves.util;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.werewolves.config.WerewolvesConfig;
import de.teamlapen.werewolves.core.ModBiomes;
import de.teamlapen.werewolves.core.ModTags;
import de.teamlapen.werewolves.core.WerewolfSkills;
import de.teamlapen.werewolves.entities.IWerewolf;
import de.teamlapen.werewolves.player.IWerewolfPlayer;
import de.teamlapen.werewolves.player.werewolf.WerewolfPlayer;
import de.teamlapen.werewolves.player.werewolf.actions.WerewolfFormAction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class Helper extends de.teamlapen.vampirism.util.Helper {

    public static boolean isWerewolf(Entity entity) {
        return WReference.WEREWOLF_FACTION.equals(VampirismAPI.factionRegistry().getFaction(entity));
    }

    public static boolean isWerewolf(PlayerEntity entity) {
        return VampirismAPI.getFactionPlayerHandler((entity)).map(h -> WReference.WEREWOLF_FACTION.equals(h.getCurrentFaction())).orElse(false);
    }

    public static boolean hasFaction(Entity entity) {
        if (VampirismAPI.factionRegistry().getFaction(entity) != null) {
            return true;
        } else return isWerewolf(entity);
    }

    public static BlockPos multiplyBlockPos(BlockPos pos, double amount) {
        return new BlockPos(pos.getX() * amount, pos.getY() * amount, pos.getZ() * amount);
    }

    public static boolean isInWerewolfBiome(IWorld world, BlockPos pos) {
        ResourceLocation loc = world.getBiome(pos).getRegistryName();
        if (loc != null) {
            ResourceLocation a = ModBiomes.WEREWOLF_HEAVEN_KEY.location();
            return loc.equals(a);
        }
        return false;
    }

    public static boolean canBecomeWerewolf(PlayerEntity player) {
        return FactionPlayerHandler.getOpt(player).map((v) -> v.canJoin(WReference.WEREWOLF_FACTION)).orElse(false);
    }

    public static boolean isNight(World world) {
        long time = world.getDayTime() % 24000;
        return !world.dimensionType().hasFixedTime() && time > 12786 && time < 23216;
    }

    public static boolean isFullMoon(World world) {
        long time = world.getDayTime() % 192000;
        return !world.dimensionType().hasFixedTime() && time > 12786 && time < 23216;
    }

    public static Map<Item, Integer> getMissingItems(IInventory inventory, Item[] items, int[] amount){
        Map<Item, Integer> missing = new HashMap<>();
        for (int i = 0; i < items.length; i++) {
            missing.put(items[i], amount[i]);
        }

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            missing.computeIfPresent(stack.getItem(), (item, amount1) -> amount1 - stack.getCount());
        }
        missing.entrySet().removeIf(s -> s.getValue() <= 0);
        return missing;
    }

    public static boolean isFormActionActive(IWerewolfPlayer player) {
        return WerewolfFormAction.isWerewolfFormActionActive(player.getActionHandler());
    }

    public static void deactivateWerewolfActions(IWerewolfPlayer player) {
        WerewolfFormAction.getAllAction().stream().filter(action -> player.getActionHandler().isActionActive(action)).forEach(action -> player.getActionHandler().toggleAction(action));
    }

    public static WerewolfDamageSource causeWerewolfDamage(String cause, Entity entity) {
        return new WerewolfDamageSource(cause, entity);
    }

    public static WerewolfDamageSource causeWerewolfDamage(PlayerEntity entity) {
        return causeWerewolfDamage("player", entity);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean canWerewolfEatItem(PlayerEntity player, ItemStack stack) {
        return !stack.isEdible() || ModTags.Items.COOKEDMEATS.contains(stack.getItem()) || WerewolvesConfig.SERVER.isCustomMeatItems(stack.getItem()) || ModTags.Items.RAWMEATS.contains(stack.getItem()) || WerewolvesConfig.SERVER.isCustomRawMeatItems(stack.getItem()) || stack.getItem().getFoodProperties().isMeat() || WerewolfPlayer.getOpt(player).map(w -> w.getSkillHandler().isSkillEnabled(WerewolfSkills.not_meat)).orElse(false);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isRawMeat(ItemStack stack){
        return stack.isEdible() && stack.getItem().getFoodProperties().isMeat() && ModTags.Items.RAWMEATS.contains(stack.getItem());
    }

    public static IWerewolf asIWerewolf(LivingEntity entity) {
        if (entity instanceof IWerewolf) {
            return ((IWerewolf) entity);
        } if (entity instanceof PlayerEntity) {
            return WerewolfPlayer.get(((PlayerEntity) entity));
        } else {
            return null;
        }
    }

}
