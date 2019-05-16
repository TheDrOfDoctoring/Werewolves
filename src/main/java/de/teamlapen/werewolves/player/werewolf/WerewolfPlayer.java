package de.teamlapen.werewolves.player.werewolf;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.actions.IActionHandler;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.player.VampirismPlayer;
import de.teamlapen.vampirism.player.actions.ActionHandler;
import de.teamlapen.vampirism.player.skills.SkillHandler;
import de.teamlapen.werewolves.WerewolvesMod;
import de.teamlapen.werewolves.api.VReference;
import de.teamlapen.werewolves.api.entities.player.werewolf.IWerewolfPlayer;
import de.teamlapen.werewolves.config.Balance;
import de.teamlapen.werewolves.core.ModPotions;
import de.teamlapen.werewolves.player.werewolf.actions.WerewolfActions;
import de.teamlapen.werewolves.util.REFERENCE;
import de.teamlapen.werewolves.util.ScoreboardUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.function.Predicate;

import javax.annotation.Nonnull;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Main class for Werewolve Players.
 */
public class WerewolfPlayer extends VampirismPlayer<IWerewolfPlayer> implements IWerewolfPlayer {
    @CapabilityInject(IWerewolfPlayer.class)
    public static final Capability<IWerewolfPlayer> CAP = getNull();
    private final static String TAG = "WerewolfPlayer";

    /**
     * Don't call before the construction event of the player entity is finished
     */
    public static WerewolfPlayer get(EntityPlayer player) {
        return (WerewolfPlayer) player.getCapability(CAP, null);
    }

    public static void registerCapability() {
        CapabilityManager.INSTANCE.register(IWerewolfPlayer.class, new Storage(), WerewolfPlayerDefaultImpl.class);
    }

    @SuppressWarnings("ConstantConditions")
    public static ICapabilityProvider createNewCapability(final EntityPlayer player) {
        return new ICapabilitySerializable<NBTTagCompound>() {

            final IWerewolfPlayer inst = new WerewolfPlayer(player);

            @Override
            public void deserializeNBT(NBTTagCompound nbt) {
                CAP.getStorage().readNBT(CAP, this.inst, null, nbt);
            }

            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
                return capability == CAP ? CAP.<T>cast(this.inst) : null;
            }

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
                return CAP.equals(capability);
            }

            @Override
            public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) CAP.getStorage().writeNBT(CAP, this.inst, null);
            }
        };
    }

    private final ActionHandler<IWerewolfPlayer> actionHandler;
    private final SkillHandler<IWerewolfPlayer> skillHandler;
    private final WerewolfPlayerSpecialAttributes specialAttributes;
    private int waterTime = 0;
    private boolean transformable = true;

    public WerewolfPlayer(EntityPlayer player) {
        super(player);
        this.applyEntityAttributes();
        this.actionHandler = new ActionHandler<>(this);
        this.skillHandler = new SkillHandler<>(this);
        this.specialAttributes = new WerewolfPlayerSpecialAttributes();
    }

    public WerewolfPlayerSpecialAttributes getSpecialAttributes() {
        return this.specialAttributes;
    }

    @Override
    public boolean canLeaveFaction() {
        return true;
    }

    @Override
    public IFaction getDisguisedAs() {
        return this.isDisguised() ? this.specialAttributes.disguisedAs : this.getFaction();
    }

    @Override
    public int getMaxLevel() {
        return REFERENCE.HIGHEST_WEREWOLF_LEVEL;
    }

    @Override
    public Predicate<Entity> getNonFriendlySelector(boolean otherFactionPlayers, boolean ignoreDisguise) {
        if (otherFactionPlayers)
            return entity -> true;
        else
            return VampirismAPI.factionRegistry().getPredicate(this.getFaction(), ignoreDisguise);
    }

    @Override
    public boolean isDisguised() {
        return !(this.specialAttributes.werewolf > 0);
    }

    @Override
    public void onLevelChanged(int newLevel, int oldLevel) {
        if (!this.isRemote()) {
            ScoreboardUtil.updateScoreboard(this.player, ScoreboardUtil.WEREWOLF_LEVEL_CRITERIA, newLevel);
            if (newLevel > 0) {
                if (oldLevel == 0) {
                    this.skillHandler.enableRootSkill();
                }

            } else {
                this.actionHandler.resetTimers();
                this.skillHandler.disableAllSkills();
            }
            this.setLevelModifier(newLevel);
        } else {
            if (oldLevel == 0) {

            } else if (newLevel == 0) {
                this.actionHandler.resetTimers();
            }
        }
    }

    public void setLevelModifier(int level) {
        if (this.getActionHandler().isActionActive(WerewolfActions.werewolf_werewolf)) {
            WerewolfActions.werewolf_werewolf.onLevelChanged(this);
        }
        //TODO harvestspeed
        //        int harvestLevel = 0;
        //        float harvestSpeed = 1F;
        //        if (level >= 13) {
        //            harvestLevel = 3;
        //        } else if (level >= 19) {
        //            harvestLevel = 2;
        //        } else if (level >= 5) {
        //            harvestLevel = 1;
        //        }
        //        this.getSpecialAttributes().harvestLevel = harvestLevel;
        //        this.getSpecialAttributes().harvestSpeed = (float) (Balance.wp.HARVESTSPEED_MAX * (level / this.getMaxLevel()));
    }

    @Override
    public ISkillHandler<IWerewolfPlayer> getSkillHandler() {
        return this.skillHandler;
    }

    @Override
    public IActionHandler<IWerewolfPlayer> getActionHandler() {
        return this.actionHandler;
    }

    @Override
    public ResourceLocation getCapKey() {
        return REFERENCE.WEREWOLF_PLAYER_KEY;
    }

    @Override
    public void onChangedDimension(int from, int to) {
    }

    @Override
    public void onDeath(DamageSource src) {
        this.actionHandler.deactivateAllActions();
    }

    @Override
    public boolean onEntityAttacked(DamageSource src, float amt) {
        return false;
    }

    @Override
    public void onJoinWorld() {
        if (this.getLevel() > 0) {
            this.actionHandler.onActionsReactivated();
        }
    }

    @Override
    public void onPlayerLoggedIn() {
    }

    @Override
    public void onPlayerLoggedOut() {
    }

    @Override
    public void onUpdate() {
        this.player.getEntityWorld().profiler.startSection("vampirewerewolf_werewolfPlayer");
        int level = this.getLevel();
        if (!this.isRemote()) {
            if (level > 0) {
                boolean sync = false;
                boolean syncToAll = false;
                NBTTagCompound syncPacket = new NBTTagCompound();
                if (this.actionHandler.updateActions()) {
                    sync = true;
                    syncToAll = true;
                    this.actionHandler.writeUpdateForClient(syncPacket);
                }
                if (this.skillHandler.isDirty()) {
                    sync = true;
                    this.skillHandler.writeUpdateForClient(syncPacket);
                }
                if (sync) {
                    this.sync(syncPacket, syncToAll);
                }
            }
        } else {
            if (level > 0) {
                this.actionHandler.updateActions();
            }
        }
        if (this.player.getEntityWorld().getWorldTime() % 40 == 0) {
            if (this.player.isInWater()) {
                this.waterTime++;
                if (this.waterTime > 3 && this.getSpecialAttributes().werewolf > 0) {
                    this.player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60));
                    if (this.player.getEntityWorld().getBlockState(this.player.getPosition().up()).getBlock().equals(Blocks.WATER)) {
                        this.player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 120));
                        this.player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60));
                    }
                }
            } else {
                this.waterTime = Math.max(0, --this.waterTime);
            }
            int heal = this.getSpecialAttributes().heals;
            if (heal >= 1) {
                int toHeal = heal / 2 < 1 ? 1 : heal / 2;
                if (this.getSpecialAttributes().healing && this.getSpecialAttributes().werewolf > 0) {
                    this.player.heal(toHeal);
                }
                this.getSpecialAttributes().removeHeal(toHeal);
            }
            if (this.specialAttributes.animalHunter) {
                boolean animal = false;
                for (Entity e : WerewolvesMod.proxy.getRayTraceEntity()) {
                    if (e instanceof EntityAnimal) {
                        animal = true;
                        break;
                    }
                }
                if (animal) {
                    this.player.addPotionEffect(new PotionEffect(ModPotions.unvisible_speed, 80));
                }
            }
        }
        this.player.world.profiler.endSection();
    }

    @Override
    public void onUpdatePlayer(Phase phase) {
    }

    @Override
    protected WerewolfPlayer copyFromPlayer(EntityPlayer old) {
        WerewolfPlayer oldWerewolve = get(old);
        NBTTagCompound nbt = new NBTTagCompound();
        oldWerewolve.saveData(nbt);
        this.loadData(nbt);
        return oldWerewolve;
    }

    /**
     * Bite the entity with the given id.
     * Checks reach distance
     *
     * @param entityId
     *            The id of the entity to start biting
     */
    public void biteEntity(int entityID) {
        Entity e = this.player.getEntityWorld().getEntityByID(entityID);
        if (this.player.isSpectator()) {
            WerewolvesMod.log.w(TAG, "Player can't bite in spectator mode");
            return;
        }
        if (e instanceof EntityLivingBase) {
            if (e.getDistance(this.player) <= this.player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1) {
                this.biteAttack((EntityLivingBase) e);
            }
        }
    }

    /**
     * only execute in werewolf form
     *
     * @param entity
     *            The entity to attack
     */
    private void biteAttack(EntityLivingBase entity) {
        // TODO
        float damage = (float) this.player.getEntityAttribute(VReference.biteDamage).getAttributeValue();
        entity.attackEntityFrom(DamageSource.causePlayerDamage(this.player), damage);
        if (!entity.isEntityUndead()) {
            this.eatFleshFrom(entity);
        }
    }

    /**
     * feeds it self from bitten entities
     *
     * @param entity
     *            The bitten entity
     */
    private void eatFleshFrom(EntityLivingBase entity) {
        if (!this.getSpecialAttributes().eatFlesh)
            return;
        // TODO
    }

    private void applyEntityAttributes() {
        if (this.player.getAttributeMap().getAttributeInstance(VReference.biteDamage) == null) {
            this.player.getAttributeMap().registerAttribute(VReference.biteDamage).setBaseValue(Balance.wp.BITE_DMG);
        }
        if (this.player.getAttributeMap().getAttributeInstance(VReference.harvestSpeed) == null) {
            this.player.getAttributeMap().registerAttribute(VReference.harvestSpeed).setBaseValue(Balance.wp.HARVESTSPEED);
        }
    }

    @Override
    protected void loadUpdate(NBTTagCompound nbt) {
        this.actionHandler.readUpdateFromServer(nbt);
        this.skillHandler.readUpdateFromServer(nbt);
    }

    @Override
    protected void writeFullUpdate(NBTTagCompound nbt) {
        this.actionHandler.writeUpdateForClient(nbt);
        this.skillHandler.writeUpdateForClient(nbt);
    }

    private void loadData(NBTTagCompound nbt) {
        this.actionHandler.loadFromNbt(nbt);
        this.skillHandler.loadFromNbt(nbt);
    }

    private void saveData(NBTTagCompound nbt) {
        this.actionHandler.saveToNbt(nbt);
        this.skillHandler.saveToNbt(nbt);
    }

    private static class Storage implements Capability.IStorage<IWerewolfPlayer> {
        @Override
        public void readNBT(Capability<IWerewolfPlayer> capability, IWerewolfPlayer instance, EnumFacing side, NBTBase nbt) {
            ((WerewolfPlayer) instance).loadData((NBTTagCompound) nbt);
        }

        @Override
        public NBTBase writeNBT(Capability<IWerewolfPlayer> capability, IWerewolfPlayer instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            ((WerewolfPlayer) instance).saveData(nbt);
            return nbt;
        }
    }

    @Override
    public IPlayableFaction<IWerewolfPlayer> getFaction() {
        return VReference.WEREWOLF_FACTION;
    }

    @Override
    public int getTheEntityID() {
        return this.player.getEntityId();
    }
}
