package dev.xkmc.modulargolems.compat.materials.twilightforest;

import dev.xkmc.l2library.repack.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2library.repack.registrate.providers.RegistrateRecipeProvider;
import dev.xkmc.l2library.serial.network.ConfigDataProvider;
import dev.xkmc.modulargolems.compat.materials.common.ConditionalRecipeWrapper;
import dev.xkmc.modulargolems.compat.materials.common.ModDispatch;
import dev.xkmc.modulargolems.init.registrate.GolemItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import twilightforest.init.TFBlocks;
import twilightforest.init.TFItems;

public class TFDispatch extends ModDispatch {

	public static final String MODID = "twilightforest";

	public TFDispatch() {
		TFCompatRegistry.register();
	}

	public void genLang(RegistrateLangProvider pvd) {
		pvd.add("golem_material." + MODID + ".ironwood", "Ironwood");
		pvd.add("golem_material." + MODID + ".steeleaf", "Steeleaf");
		pvd.add("golem_material." + MODID + ".knightmetal", "Knightmetal");
		pvd.add("golem_material." + MODID + ".fiery", "Fiery");
	}

	@Override
	public void genRecipe(RegistrateRecipeProvider pvd) {
		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(TFCompatRegistry.UP_CARMINITE.get())::unlockedBy, TFItems.CARMINITE.get())
				.pattern("CAC").pattern("ABA").pattern("CAC")
				.define('A', TFItems.CARMINITE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', TFBlocks.ENCASED_TOWERWOOD.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(TFCompatRegistry.UP_FIERY.get())::unlockedBy, TFItems.FIERY_INGOT.get())
				.pattern("CAC").pattern("ABA").pattern("CAC")
				.define('A', TFItems.FIERY_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', Items.BLAZE_POWDER)
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(TFCompatRegistry.UP_KNIGHTMETAL.get())::unlockedBy, TFItems.KNIGHTMETAL_INGOT.get())
				.pattern("CAC").pattern("ABA").pattern("CAC")
				.define('A', TFItems.KNIGHTMETAL_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.define('C', TFBlocks.HEDGE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(TFCompatRegistry.UP_STEELEAF.get())::unlockedBy, TFItems.STEELEAF_INGOT.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', TFItems.STEELEAF_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(TFCompatRegistry.UP_IRONWOOD.get())::unlockedBy, TFItems.IRONWOOD_INGOT.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', TFItems.IRONWOOD_INGOT.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));

		safeUpgrade(pvd, ShapedRecipeBuilder.shaped(TFCompatRegistry.UP_NAGA.get())::unlockedBy, TFItems.NAGA_SCALE.get())
				.pattern(" A ").pattern("ABA").pattern(" A ")
				.define('A', TFItems.NAGA_SCALE.get())
				.define('B', GolemItems.EMPTY_UPGRADE.get())
				.save(ConditionalRecipeWrapper.mod(pvd, MODID));
	}

	@Override
	protected ConfigDataProvider getDataGen(DataGenerator gen) {
		return new TFConfigGen(gen);
	}

}
