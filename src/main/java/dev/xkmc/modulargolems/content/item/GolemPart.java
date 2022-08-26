package dev.xkmc.modulargolems.content.item;

import dev.xkmc.modulargolems.content.config.GolemMaterial;
import dev.xkmc.modulargolems.content.config.GolemMaterialConfig;
import dev.xkmc.modulargolems.content.config.GolemPartConfig;
import dev.xkmc.modulargolems.content.core.GolemStatType;
import dev.xkmc.modulargolems.content.core.GolemType;
import dev.xkmc.modulargolems.content.core.IGolemPart;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GolemPart<T extends AbstractGolemEntity<T, P>, P extends IGolemPart<P>> extends Item {

	public static final List<GolemPart<?, ?>> LIST = new ArrayList<>();

	private static final String KEY = "golem_material";

	public static Optional<ResourceLocation> getMaterial(ItemStack stack) {
		return Optional.ofNullable(stack.getTag())
				.map(e -> e.contains(KEY) ? new ResourceLocation(e.getString(KEY)) : null);
	}

	public static ItemStack setMaterial(ItemStack stack, ResourceLocation material) {
		stack.getOrCreateTag().putString(KEY, material.toString());
		return stack;
	}

	private final Supplier<GolemType<T, P>> type;
	private final P part;
	public final int count;

	public GolemPart(Properties props, Supplier<GolemType<T, P>> type, P part, int count) {
		super(props.stacksTo(1));
		this.type = type;
		this.part = part;
		this.count = count;
		LIST.add(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		getMaterial(stack).ifPresent(e -> {
			GolemMaterial mat = parseMaterial(e);
			list.add(mat.getDesc());
			mat.stats().forEach((k, v) -> list.add(k.getAdderTooltip(v)));
			mat.modifiers().forEach((m, v) -> list.add(m.getTooltip(v)));
		});
	}

	public GolemMaterial parseMaterial(ResourceLocation mat) {
		var magnifier = GolemPartConfig.get().getMagnifier(getEntityType());
		HashMap<GolemStatType, Double> stats = new HashMap<>();
		GolemMaterialConfig.get().stats.get(mat).forEach((k, v) -> {
			var filter = GolemPartConfig.get().getFilter(this);
			double val = v * filter.getOrDefault(k.type, 1d) * magnifier.getOrDefault(k, 1d);
			if (val != 0)
				stats.compute(k, (e, o) -> (o == null ? 0 : o) + val);
		});
		return new GolemMaterial(stats, GolemMaterialConfig.get().modifiers.get(mat), mat, this);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(GolemBEWLR.EXTENSIONS);
	}

	public GolemType<T, P> getEntityType() {
		return type.get();
	}

	public P getPart() {
		return part;
	}

	@Override
	public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> list) {
		if (this.allowedIn(tab)) {
			list.add(new ItemStack(this));
			for (ResourceLocation rl : GolemMaterialConfig.get().getAllMaterials()) {
				ItemStack stack = new ItemStack(this);
				list.add(setMaterial(stack, rl));
			}
		}
	}

}
