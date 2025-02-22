package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.CarminiteModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.FieryModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFDamageModifier;
import dev.xkmc.modulargolems.compat.materials.twilightforest.modifier.TFHealingModifier;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.modifier.common.AttributeGolemModifier;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;

import static dev.xkmc.modulargolems.init.registrate.GolemItems.regUpgrade;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.THORN;
import static dev.xkmc.modulargolems.init.registrate.GolemModifiers.reg;

public class TFCompatRegistry {

	public static final RegistryEntry<FieryModifier> FIERY;
	public static final RegistryEntry<TFDamageModifier> TF_DAMAGE;
	public static final RegistryEntry<TFHealingModifier> TF_HEALING;
	public static final RegistryEntry<CarminiteModifier> CARMINITE;
	public static final RegistryEntry<AttributeGolemModifier> NAGA;

	public static final RegistryEntry<UpgradeItem> UP_CARMINITE, UP_STEELEAF, UP_FIERY, UP_IRONWOOD, UP_KNIGHTMETAL, UP_NAGA;

	static {
		FIERY = reg("fiery", FieryModifier::new, "Deal %s%% fire damage to mobs not immune to fire");
		TF_DAMAGE = reg("tf_damage", TFDamageModifier::new, "TF Damage Bonus", "Deal %s%% damage in twilight forest");
		TF_HEALING = reg("tf_healing", TFHealingModifier::new, "TF Healing Bonus", "Healing becomes %s%% in twilight forest");
		CARMINITE = reg("carminite", CarminiteModifier::new, "After being hurt, turn invisible and invinsible for %s seconds");
		NAGA = reg("naga", () -> new AttributeGolemModifier(2,
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ARMOR, 10),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_SPEED, 0.3),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATTACK, 4),
				new AttributeGolemModifier.AttrEntry(GolemTypes.STAT_ATKKB, 1)
		)).register();

		UP_CARMINITE = regUpgrade("carminite", () -> CARMINITE).lang("Carminite Upgrade").register();
		UP_STEELEAF = regUpgrade("steeleaf", () -> TF_DAMAGE).lang("Steeleaf Upgrade").register();
		UP_FIERY = regUpgrade("fiery", () -> FIERY).lang("Fiery Upgrade").register();
		UP_IRONWOOD = regUpgrade("ironwood", () -> TF_HEALING).lang("Ironwood Upgrade").register();
		UP_KNIGHTMETAL = regUpgrade("knightmetal", () -> THORN).lang("Knightmetal Upgrade").register();
		UP_NAGA = regUpgrade("naga", () -> NAGA).lang("Naga Upgrade").register();

	}

	public static void register() {

	}

}
