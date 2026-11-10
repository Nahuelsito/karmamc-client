package cl.karmamc.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

/** v1.1.1: appends purchases per hour (from /gemslevel income) to /gemshop items' tooltips (display only). */
public final class GemShopTooltip {
	private GemShopTooltip() {}

	public static void register() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> {
			Screen screen = Minecraft.getInstance().gui.screen();
			if (screen == null || !MenuReader.strip(screen.getTitle()).equals("Tienda de Gemas")) return;
			ItemLore lore = stack.get(DataComponents.LORE);
			if (lore == null) return;
			Double price = null;
			for (Component c : lore.lines()) {
				if (price == null) price = Parse.gemShopPrice(MenuReader.strip(c));
			}
			for (String line : lines(price, KarmaClient.config.gemsPer60s)) tooltip.add(Component.literal(line));
		});
	}

	/** Tooltip lines with legacy color codes; empty when the item has no gem price. */
	static List<String> lines(Double price, Double gemsPer60s) {
		if (price == null || price <= 0) return List.of();
		List<String> out = new ArrayList<>(List.of("", ChatFormatting.GOLD + "Catfish69 QoL for KarmaMC"));
		if (gemsPer60s == null) {
			out.add(ChatFormatting.RED + "Abre /gemslevel para calcular");
			return out;
		}
		out.add(ChatFormatting.AQUA + "Compras " + Parse.shortNumber(gemsPer60s * 60 / price, 2) + " por hora con tu /gemslevel");
		return out;
	}
}
