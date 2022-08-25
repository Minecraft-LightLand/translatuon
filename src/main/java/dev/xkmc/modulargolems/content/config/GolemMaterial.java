package dev.xkmc.modulargolems.content.config;

import dev.xkmc.l2library.util.code.Wrappers;
import dev.xkmc.modulargolems.content.core.GolemModifier;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.item.GolemPart;
import dev.xkmc.modulargolems.init.ModularGolems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public record GolemMaterial(
		HashMap<GolemStatType, Double> stats,
		HashMap<GolemModifier, Integer> modifiers,
		ResourceLocation id, Item part) {

	public static final ResourceLocation EMPTY = new ResourceLocation(ModularGolems.MODID, "empty");

	public static Map<GolemStatType, Double> collectAttributes(List<GolemMaterial> list) {
		HashMap<GolemStatType, Double> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			stats.stats.forEach((k, v) ->
					values.compute(k, (a, old) ->
							a.kind == GolemStatType.Kind.PERCENT ?
									(old == null ? 1 : old) * (1 + v) :
									(old == null ? 0 : old) + v));
		}
		return values;
	}

	public static HashMap<GolemModifier, Integer> collectModifiers(Collection<GolemMaterial> list) {
		HashMap<GolemModifier, Integer> values = new HashMap<>();
		for (GolemMaterial stats : list) {
			stats.modifiers.forEach((k, v) -> values.compute(k, (a, old) ->
					Math.min(a.maxLevel, (old == null ? 0 : old) + v)));
		}
		return values;
	}

	public static void addAttributes(List<GolemMaterial> list, LivingEntity entity) {
		collectAttributes(list).forEach((k, v) -> k.applyToEntity(entity, v));
	}

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		for (Map.Entry<ResourceLocation, Ingredient> ent : GolemMaterialConfig.get().ingredients.entrySet()) {
			if (ent.getValue().test(stack)) {
				return Optional.of(ent.getKey());
			}
		}
		return Optional.empty();
	}

	public MutableComponent getDesc() {
		return Component.translatable("golem_material." + id.getNamespace() + "." + id.getPath()).withStyle(ChatFormatting.GOLD);
	}

	public GolemPart<?, ?> getPart() {
		return Wrappers.cast(part());
	}
}
