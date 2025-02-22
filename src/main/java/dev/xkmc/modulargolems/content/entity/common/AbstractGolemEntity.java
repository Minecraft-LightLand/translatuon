package dev.xkmc.modulargolems.content.entity.common;

import dev.xkmc.l2library.serial.SerialClass;
import dev.xkmc.l2library.serial.codec.PacketCodec;
import dev.xkmc.l2library.serial.codec.TagCodec;
import dev.xkmc.l2library.util.annotation.ServerOnly;
import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.l2library.util.nbt.NBTObj;
import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.swim.GolemSwimMoveControl;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.WandItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.advancement.GolemTriggers;
import dev.xkmc.modulargolems.init.data.ModConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.*;

@SerialClass
public class AbstractGolemEntity<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends AbstractGolem
		implements IEntityAdditionalSpawnData, NeutralMob, OwnableEntity {

	protected AbstractGolemEntity(EntityType<T> type, Level level) {
		super(type, level);
		this.waterNavigation = new AmphibiousPathNavigation(this, level);
		this.groundNavigation = new GroundPathNavigation(this, level);
	}

	// ------ materials

	@SerialClass.SerialField(toClient = true)
	private ArrayList<GolemMaterial> materials = new ArrayList<>();
	@SerialClass.SerialField(toClient = true)
	private ArrayList<Item> upgrades = new ArrayList<>();
	@SerialClass.SerialField(toClient = true)
	@Nullable
	private UUID owner;
	@SerialClass.SerialField(toClient = true)
	private HashMap<GolemModifier, Integer> modifiers = new HashMap<>();

	protected final PathNavigation waterNavigation;
	protected final PathNavigation groundNavigation;

	public void onCreate(ArrayList<GolemMaterial> materials, ArrayList<UpgradeItem> upgrades, @Nullable UUID owner) {
		updateAttributes(materials, upgrades, owner);
		this.setHealth(this.getMaxHealth());
	}

	public void updateAttributes(ArrayList<GolemMaterial> materials, ArrayList<UpgradeItem> upgrades, @Nullable UUID owner) {
		this.materials = materials;
		this.upgrades = Wrappers.cast(upgrades);
		this.owner = owner;
		this.modifiers = GolemMaterial.collectModifiers(materials, upgrades);
		this.maxUpStep = 1;
		if (canSwim()) {
			this.moveControl = new GolemSwimMoveControl(this);
			this.navigation = waterNavigation;
			this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		}
		GolemMaterial.addAttributes(materials, upgrades, getThis());
	}

	public EntityType<T> getType() {
		return Wrappers.cast(super.getType());
	}

	public ArrayList<GolemMaterial> getMaterials() {
		return materials;
	}

	public ArrayList<Item> getUpgrades() {
		return upgrades;
	}

	public HashMap<GolemModifier, Integer> getModifiers() {
		return modifiers;
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if (player.getItemInHand(hand).getItem() instanceof WandItem) return InteractionResult.PASS;
		if (!ModConfig.COMMON.barehandRetrieve.get() || !this.isAlliedTo(player)) return InteractionResult.FAIL;
		if (player.getMainHandItem().isEmpty()) {
			if (!level.isClientSide()) {
				player.setItemSlot(EquipmentSlot.MAINHAND, toItem());
			}
			return InteractionResult.SUCCESS;
		}
		return super.mobInteract(player, hand);
	}

	@ServerOnly
	public ItemStack toItem() {
		var ans = GolemHolder.setEntity(getThis());
		level.broadcastEntityEvent(this, EntityEvent.POOF);
		this.discard();
		return ans;
	}

	@Override
	public boolean fireImmune() {
		return getModifiers().getOrDefault(GolemModifiers.FIRE_IMMUNE.get(), 0) > 0;
	}

	@Override
	protected void actuallyHurt(DamageSource source, float damage) {
		if (source.isBypassInvul()) damage *= 1000;
		super.actuallyHurt(source, damage);
		if (getHealth() <= 0 && modifiers.getOrDefault(GolemModifiers.RECYCLE.get(), 0) > 0) {
			spawnAtLocation(GolemHolder.setEntity(getThis()));
			level.broadcastEntityEvent(this, EntityEvent.POOF);
			this.discard();
		}
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int i, boolean b) {
		Map<Item, Integer> drop = new HashMap<>();
		for (GolemMaterial mat : getMaterials()) {
			Item item = GolemMaterialConfig.get().ingredients.get(mat.id()).getItems()[0].getItem();
			drop.compute(item, (e, old) -> (old == null ? 0 : old) + 1);
		}
		drop.forEach((k, v) -> spawnAtLocation(new ItemStack(k, v)));
	}

	// ------ swim

	public boolean canSwim() {
		return this.modifiers.getOrDefault(GolemModifiers.SWIM.get(), 0) > 0;
	}

	public void travel(Vec3 pTravelVector) {
		if (this.isEffectiveAi() && this.isInWater() && canSwim()) {
			this.moveRelative(0.01F, pTravelVector);
			this.move(MoverType.SELF, this.getDeltaMovement());
			this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
		} else {
			super.travel(pTravelVector);
		}
	}

	public void updateSwimming() {
		if (!this.level.isClientSide) {
			this.setSwimming(this.isEffectiveAi() && this.isInWater() && this.canSwim());
		}

	}

	public boolean isPushedByFluid() {
		return !this.isSwimming();
	}

	// ------ ownable entity

	@Nullable
	public UUID getOwnerUUID() {
		return owner;
	}

	@Nullable
	public Player getOwner() {
		try {
			UUID uuid = this.getOwnerUUID();
			return uuid == null ? null : this.level.getPlayerByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	// ------ addition golem behavior

	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		this.addPersistentAngerSaveData(tag);
		tag.put("auto-serial", Objects.requireNonNull(TagCodec.toTag(new CompoundTag(), this)));
		tag.putInt("follow_mode", getMode());
		new NBTObj(tag).getSub("guard_pos").fromBlockPos(getGuardPos());
	}

	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.readPersistentAngerSaveData(this.level, tag);
		if (tag.contains("auto-serial")) {
			Wrappers.run(() -> {
				TagCodec.fromTag(tag.getCompound("auto-serial"), this.getClass(), this, (f) -> true);
			});
		}
		setMode(tag.getInt("follow_mode"), new NBTObj(tag).getSub("guard_pos").toBlockPos());
	}

	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public void writeSpawnData(FriendlyByteBuf buffer) {
		PacketCodec.to(buffer, this);
	}

	public void readSpawnData(FriendlyByteBuf data) {
		PacketCodec.from(data, Wrappers.cast(this.getClass()), getThis());
	}

	public T getThis() {
		return Wrappers.cast(this);
	}

	// ------ common golem behavior

	@Override
	public boolean canAttack(LivingEntity pTarget) {
		if (pTarget instanceof AbstractGolemEntity<?, ?> golem) {
			return false;
		}
		return !this.isAlliedTo(pTarget) && super.canAttack(pTarget);
	}

	protected float getAttackDamage() {
		float ans = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
		for (var entry : getModifiers().entrySet()) {
			ans = entry.getKey().modifyDamage(ans, this, entry.getValue());
		}
		return ans;
	}

	@Override
	public void aiStep() {
		this.updateSwingTime();
		super.aiStep();
		double heal = this.getAttributeValue(GolemTypes.GOLEM_REGEN.get());
		if (heal > 0 && this.tickCount % 20 == 0) {
			for (var entry : getModifiers().entrySet()) {
				heal = entry.getKey().onHealTick(heal, this, entry.getValue());
			}
			if (heal > 0) {
				this.heal((float) heal);
			}
		}
		if (!this.level.isClientSide) {
			for (var entry : getModifiers().entrySet()) {
				entry.getKey().onAiStep(this, entry.getValue());
			}
			this.updatePersistentAnger((ServerLevel) this.level, true);
		}
	}

	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean wasKilled(ServerLevel level, LivingEntity target) {
		Player player = getOwner();
		if (player != null) GolemTriggers.KILL.trigger((ServerPlayer) player, target);
		return super.wasKilled(level, target);
	}

	// mode

	private static final EntityDataAccessor<Integer> DATA_MODE = SynchedEntityData.defineId(AbstractGolemEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<BlockPos> GUARD_POS = SynchedEntityData.defineId(AbstractGolemEntity.class, EntityDataSerializers.BLOCK_POS);

	public int getMode() {
		return this.entityData.get(DATA_MODE);
	}

	public BlockPos getGuardPos() {
		return this.entityData.get(GUARD_POS);
	}

	public void setMode(int mode, BlockPos pos) {
		this.entityData.set(DATA_MODE, mode);
		this.entityData.set(GUARD_POS, pos);
	}

	@Override
	public boolean canChangeDimensions() {
		return getMode() == 0 && super.canChangeDimensions();
	}

	// ------ persistent anger

	private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
	private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(AbstractGolemEntity.class, EntityDataSerializers.INT);

	@Nullable
	private UUID persistentAngerTarget;

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
		this.entityData.define(DATA_MODE, 0);
		this.entityData.define(GUARD_POS, BlockPos.ZERO);
	}

	public void startPersistentAngerTimer() {
		this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
	}

	public int getRemainingPersistentAngerTime() {
		return this.entityData.get(DATA_REMAINING_ANGER_TIME);
	}

	public void setRemainingPersistentAngerTime(int pTime) {
		this.entityData.set(DATA_REMAINING_ANGER_TIME, pTime);
	}

	public void setPersistentAngerTarget(@Nullable UUID target) {
		this.persistentAngerTarget = target;
	}

	@Nullable
	public UUID getPersistentAngerTarget() {
		return this.persistentAngerTarget;
	}

	// ------ tamable

	public Team getTeam() {
		LivingEntity owner = this.getOwner();
		if (owner != null) {
			return owner.getTeam();
		}
		return super.getTeam();
	}

	public boolean isAlliedTo(Entity other) {
		LivingEntity owner = this.getOwner();
		if (other == owner) {
			return true;
		}
		if (owner != null) {
			return owner.isAlliedTo(other);
		}
		return super.isAlliedTo(other);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (target instanceof LivingEntity le) {
			le.setLastHurtByPlayer(getOwner());
		}
		return super.doHurtTarget(target);
	}

	protected void registerTargetGoals() {
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, this::predicatePriorityTarget));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, this::predicateSecondaryTarget));
		this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
	}

	protected boolean predicatePriorityTarget(LivingEntity e) {
		if (e instanceof Mob mob) {
			for (var target : List.of(
					Optional.ofNullable(mob.getLastHurtMob()),
					Optional.ofNullable(mob.getTarget()),
					Optional.ofNullable(mob.getLastHurtByMob())
			)) {
				if (target.isPresent()) {
					Player owner = getOwner();
					if (target.get() == owner) return true;
					if (target.get().isAlliedTo(this)) return true;
				}
			}
		}
		return false;
	}

	protected boolean predicateSecondaryTarget(LivingEntity e) {
		return e instanceof Enemy && !(e instanceof Creeper);
	}

	public boolean isInSittingPose() {
		return false;
	}

	public Vec3 getTargetPos() {
		if (getMode() == 1) {
			BlockPos pos = getGuardPos();
			return new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
		}
		LivingEntity owner = getOwner();
		if (owner == null) return getPosition(1);
		return owner.getPosition(1);
	}

}


