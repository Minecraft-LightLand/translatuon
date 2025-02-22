package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.serial.network.BaseConfig;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.init.registrate.GolemModifiers;
import dev.xkmc.modulargolems.init.registrate.GolemTypes;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import twilightforest.data.tags.ItemTagGenerator;

import java.util.Map;

public class TFConfigGen extends ConfigDataProvider {

	public TFConfigGen(DataGenerator generator) {
		super(generator, "data/" + TFDispatch.MODID + "/golem_config/", "Golem Config for Twilight Forest");
	}

	public void add(Map<String, BaseConfig> map) {
		map.put("materials/" + TFDispatch.MODID, new GolemMaterialConfig()
				.addMaterial(new ResourceLocation(TFDispatch.MODID, "ironwood"), Ingredient.of(ItemTagGenerator.IRONWOOD_INGOTS))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 10)
				.addStat(GolemTypes.STAT_REGEN.get(), 2)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1).end()

				.addMaterial(new ResourceLocation(TFDispatch.MODID, "steeleaf"), Ingredient.of(ItemTagGenerator.STEELEAF_INGOTS))
				.addStat(GolemTypes.STAT_HEALTH.get(), 20)
				.addStat(GolemTypes.STAT_ATTACK.get(), 30)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1).end()

				.addMaterial(new ResourceLocation(TFDispatch.MODID, "knightmetal"), Ingredient.of(ItemTagGenerator.KNIGHTMETAL_INGOTS))
				.addStat(GolemTypes.STAT_HEALTH.get(), 300)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addStat(GolemTypes.STAT_WEIGHT.get(), -0.4)
				.addModifier(GolemModifiers.THORN.get(), 2)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1)
				.end()

				.addMaterial(new ResourceLocation(TFDispatch.MODID, "fiery"), Ingredient.of(ItemTagGenerator.FIERY_INGOTS))
				.addStat(GolemTypes.STAT_HEALTH.get(), 200)
				.addStat(GolemTypes.STAT_ATTACK.get(), 20)
				.addModifier(GolemModifiers.FIRE_IMMUNE.get(), 1)
				.addModifier(GolemModifiers.THORN.get(), 1)
				.addModifier(TFCompatRegistry.FIERY.get(), 1)
				.addModifier(TFCompatRegistry.TF_DAMAGE.get(), 1)
				.addModifier(TFCompatRegistry.TF_HEALING.get(), 1)
				.end()
		);
	}

}
