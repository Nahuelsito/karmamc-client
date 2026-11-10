package cl.karmamc.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

/** v1.0.2: appends money left to gems level 100 to the /gemslevel nether star's tooltip (display only). */
public final class GemsTooltip {
	private GemsTooltip() {}

	public static void register() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> {
			ItemLore lore = stack.get(DataComponents.LORE);
			if (lore == null) return;
			Double level = null, cost = null, step = null;
			for (Component c : lore.lines()) {
				String line = MenuReader.strip(c);
				if (level == null) level = Parse.gemsLevel(line);
				if (cost == null) cost = Parse.gemsNextCost(line);
				if (step == null) step = Parse.gemsStep(line);
			}
			for (String line : lines(level, cost, step)) tooltip.add(Component.literal(line));
		});
	}

	/** Tooltip lines with legacy color codes; empty when the item is not the gems-level star or a value is missing. */
	static List<String> lines(Double level, Double cost, Double step) {
		if (level == null) return List.of();
		List<String> out = new ArrayList<>(List.of("", ChatFormatting.GOLD + "KarmaMC Client"));
		long l = level.longValue();
		if (l >= Calc.GEMS_MAX_LEVEL) {
			out.add(ChatFormatting.GREEN + "Nivel máximo (" + Calc.GEMS_MAX_LEVEL + ")");
			return out;
		}
		if (cost == null || step == null) return List.of();
		long n = Calc.GEMS_MAX_LEVEL - l;
		out.add(ChatFormatting.YELLOW + "Faltan " + n + (n == 1 ? " nivel" : " niveles") + " para nivel " + Calc.GEMS_MAX_LEVEL);
		out.add(ChatFormatting.AQUA + "Cuesta $" + Parse.shortNumber(Calc.gemsLeft(l, cost, step), 2));
		return out;
	}
}
