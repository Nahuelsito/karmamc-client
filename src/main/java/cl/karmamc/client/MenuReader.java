package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Re-scans known server menus every tick (items arrive after the screen opens) and caches readings in config. */
public final class MenuReader {
	private MenuReader() {}

	public static void tick(Minecraft mc) {
		Screen screen = mc.gui.screen();
		if (!(screen instanceof AbstractContainerScreen<?> s)) return;
		String title = strip(s.getTitle());
		KarmaConfig c = KarmaClient.config;
		boolean changed = false;
		switch (title) {
			case "Inversiones" -> {
				List<String> lines = lines(s, null);
				Double v = first(lines, Parse::invest);
				if (differs(c.investPerSecond, v)) { c.investPerSecond = v; changed = true; }
				v = first(lines, Parse::investLimit);
				if (differs(c.investLimit, v)) { c.investLimit = v; changed = true; }
			}
			case "Bóveda de Minions" -> {
				double sum = 0;
				boolean found = false;
				for (String line : lines(s, null)) {
					Double v = Parse.minion(line);
					if (v != null) { sum += v; found = true; }
				}
				if (found && differs(c.minionsPerSecond, sum)) { c.minionsPerSecond = sum; changed = true; }
			}
			case "Nivel de Gemas" -> {
				Double v = first(lines(s, null), Parse::gems);
				if (differs(c.gemsPer60s, v)) { c.gemsPer60s = v; changed = true; }
			}
			case "Prestigio" -> {
				Double v = first(lines(s, null), Parse::aura);
				if (differs(c.auraPer3h, v)) { c.auraPer3h = v; changed = true; }
			}
			case "Mejora de Venta" -> {
				Double v = first(lines(s, "EN PROGRESO"), Parse::sold);
				if (differs(c.totalSold, v)) { c.totalSold = v; changed = true; }
			}
			case "Tienda de Aura" -> {
				List<String> lines = lines(s, "MEJORA DE INVERSI");
				Double v = first(lines, Parse::shopStep);
				if (differs(c.auraShopStep, v)) { c.auraShopStep = v; changed = true; }
				v = first(lines, Parse::shopCap);
				if (differs(c.auraShopCap, v)) { c.auraShopCap = v; changed = true; }
				v = first(lines, Parse::shopPrice);
				if (differs(c.auraShopPrice, v)) { c.auraShopPrice = v; changed = true; }
			}
			default -> {}
		}
		if (changed) c.save();
	}

	/** Name + lore lines of container slots (player inventory excluded); nameFilter (upper-case substring) keeps only matching items, e.g. the Aurashop emerald. */
	private static List<String> lines(AbstractContainerScreen<?> s, String nameFilter) {
		List<String> out = new ArrayList<>();
		for (Slot slot : s.getMenu().slots) {
			if (slot.container instanceof Inventory) continue;
			ItemStack stack = slot.getItem();
			if (stack.isEmpty()) continue;
			String name = strip(stack.getHoverName());
			if (nameFilter != null && !name.toUpperCase(java.util.Locale.ROOT).contains(nameFilter)) continue;
			out.add(name);
			ItemLore lore = stack.get(DataComponents.LORE);
			if (lore != null) for (Component line : lore.lines()) out.add(strip(line));
		}
		return out;
	}

	private static Double first(List<String> lines, Function<String, Double> parser) {
		for (String line : lines) {
			Double v = parser.apply(line);
			if (v != null) return v;
		}
		return null;
	}

	/** True when a new reading exists and differs from the cached one. */
	private static boolean differs(Double old, Double now) {
		return now != null && !now.equals(old);
	}

	static String strip(Component c) {
		String s = ChatFormatting.stripFormatting(c.getString());
		return s == null ? "" : s.trim();
	}
}
