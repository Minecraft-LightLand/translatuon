package dev.xkmc.modulargolems.init.registrate;

import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.repack.registrate.builders.ItemBuilder;
import dev.xkmc.l2library.repack.registrate.util.entry.ItemEntry;
import dev.xkmc.l2library.repack.registrate.util.entry.RegistryEntry;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemEntity;
import dev.xkmc.modulargolems.content.entity.dog.DogGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumaniodGolemPartType;
import dev.xkmc.modulargolems.content.entity.humanoid.HumanoidGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemPartType;
import dev.xkmc.modulargolems.content.item.CommandWandItem;
import dev.xkmc.modulargolems.content.item.RetrievalWandItem;
import dev.xkmc.modulargolems.content.item.UpgradeItem;
import dev.xkmc.modulargolems.content.item.golem.GolemHolder;
import dev.xkmc.modulargolems.content.item.golem.GolemPart;
import dev.xkmc.modulargolems.content.modifier.GolemModifier;
import dev.xkmc.modulargolems.init.ModularGolems;
import dev.xkmc.modulargolems.init.data.TagGen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

import static dev.xkmc.modulargolems.init.ModularGolems.REGISTRATE;

public class GolemItems {

	public static final Tab GOLEMS = new Tab("golems");

	static {
		REGISTRATE.creativeModeTab(() -> GOLEMS);
	}

	public static final ItemEntry<Item> GOLEM_TEMPLATE, EMPTY_UPGRADE;

	public static final ItemEntry<GolemPart<MetalGolemEntity, MetalGolemPartType>> GOLEM_BODY, GOLEM_ARM, GOLEM_LEGS;
	public static final ItemEntry<GolemHolder<MetalGolemEntity, MetalGolemPartType>> HOLDER_GOLEM;

	public static final ItemEntry<GolemPart<HumanoidGolemEntity, HumaniodGolemPartType>> HUMANOID_BODY, HUMANOID_ARMS, HUMANOID_LEGS;
	public static final ItemEntry<GolemHolder<HumanoidGolemEntity, HumaniodGolemPartType>> HOLDER_HUMANOID;

	public static final ItemEntry<GolemPart<DogGolemEntity, DogGolemPartType>> DOG_BODY, DOG_LEGS;
	public static final ItemEntry<GolemHolder<DogGolemEntity, DogGolemPartType>> HOLDER_DOG;

	public static final ItemEntry<UpgradeItem> FIRE_IMMUNE, THUNDER_IMMUNE, RECYCLE, DIAMOND, NETHERITE, QUARTZ,
			GOLD, ENCHANTED_GOLD, FLOAT, SPONGE, SWIM;

	public static final ItemEntry<RetrievalWandItem> RETRIEVAL_WAND;
	public static final ItemEntry<CommandWandItem> COMMAND_WAND;

	static {

		GOLEM_TEMPLATE = REGISTRATE.item("metal_golem_template", Item::new).defaultModel().defaultLang().register();
		RETRIEVAL_WAND = REGISTRATE.item("retrieval_wand", p -> new RetrievalWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();
		COMMAND_WAND = REGISTRATE.item("command_wand", p -> new CommandWandItem(p.stacksTo(1))).defaultModel().defaultLang().register();

		// upgrades
		{
			EMPTY_UPGRADE = REGISTRATE.item("empty_upgrade", Item::new).defaultModel().defaultLang().register();
			FIRE_IMMUNE = regUpgrade("fire_immune", () -> GolemModifiers.FIRE_IMMUNE).lang("Fire Immune Upgrade").register();
			THUNDER_IMMUNE = regUpgrade("thunder_immune", () -> GolemModifiers.THUNDER_IMMUNE).lang("Thunder Immune Upgrade").register();
			RECYCLE = regUpgrade("recycle", () -> GolemModifiers.RECYCLE).lang("Recycle Ugpgrade").register();
			DIAMOND = regUpgrade("diamond", () -> GolemModifiers.ARMOR).lang("Diamond Upgrade").register();
			NETHERITE = regUpgrade("netherite", () -> GolemModifiers.TOUGH).lang("Netherite Upgrade").register();
			QUARTZ = regUpgrade("quartz", () -> GolemModifiers.DAMAGE).lang("Quartz Upgrade").register();
			GOLD = regUpgrade("gold", () -> GolemModifiers.REGEN).lang("Golden Apple Upgrade").register();
			ENCHANTED_GOLD = regUpgrade("enchanted_gold", () -> GolemModifiers.REGEN, 2, true).lang("Enchanted Golden Apple Upgrade").register();
			FLOAT = regUpgrade("float", () -> GolemModifiers.FLOAT).lang("Float Upgrade").register();
			SPONGE = regUpgrade("sponge", () -> GolemModifiers.EXPLOSION_RES).lang("Sponge Upgrade").register();
			SWIM = regUpgrade("swim", () -> GolemModifiers.SWIM).lang("Swim Upgrade").register();
		}

		// holders
		{
			HOLDER_GOLEM = REGISTRATE.item("metal_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_GOLEM))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_HOLDERS).defaultLang().register();

			HOLDER_HUMANOID = REGISTRATE.item("humanoid_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_HOLDERS).defaultLang().register();

			HOLDER_DOG = REGISTRATE.item("dog_golem_holder", p ->
							new GolemHolder<>(p.fireResistant(), GolemTypes.TYPE_DOG))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_HOLDERS).defaultLang().register();
		}

		// metal golem
		{
			GOLEM_BODY = REGISTRATE.item("metal_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.BODY, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			GOLEM_ARM = REGISTRATE.item("metal_golem_arm", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEFT, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			GOLEM_LEGS = REGISTRATE.item("metal_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_GOLEM, MetalGolemPartType.LEG, 9))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
		}

		// humanoid golem
		{
			HUMANOID_BODY = REGISTRATE.item("humanoid_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			HUMANOID_ARMS = REGISTRATE.item("humanoid_golem_arms", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.ARMS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
			HUMANOID_LEGS = REGISTRATE.item("humanoid_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_HUMANOID, HumaniodGolemPartType.LEGS, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
		}

		// dog golem
		{
			DOG_BODY = REGISTRATE.item("dog_golem_body", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_DOG, DogGolemPartType.BODY, 6))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();

			DOG_LEGS = REGISTRATE.item("dog_golem_legs", p ->
							new GolemPart<>(p.fireResistant(), GolemTypes.TYPE_DOG, DogGolemPartType.LEGS, 3))
					.model((ctx, pvd) -> pvd.getBuilder(ctx.getName()).parent(new ModelFile.UncheckedModelFile("builtin/entity")))
					.tag(TagGen.GOLEM_PARTS).defaultLang().register();
		}

	}

	public static ItemBuilder<UpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod) {
		return regUpgrade(id, mod, 1, false);
	}

	public static ItemBuilder<UpgradeItem, L2Registrate> regUpgrade(String id, Supplier<RegistryEntry<? extends GolemModifier>> mod, int level, boolean foil) {
		return REGISTRATE.item(id, p -> new UpgradeItem(p, mod.get()::get, level, foil))
				.model((ctx, pvd) -> pvd.generated(ctx, pvd.modLoc("item/upgrades/" + id)))
				.tag(TagGen.GOLEM_UPGRADES);
	}

	public static void register() {
	}

	public static class Tab extends CreativeModeTab {

		public Tab(String label) {
			super(ModularGolems.MODID + "." + label);
		}

		@Override
		public ItemStack makeIcon() {
			return HOLDER_GOLEM.asStack();
		}
	}

}
