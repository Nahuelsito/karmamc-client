package cl.karmamc.client;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Module 4: appends "Invest Left" lines to the Aurashop investment emerald's tooltip (display only). */
public final class InvestTooltip {
	private InvestTooltip() {}

	public static void register() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, tooltip) -> {
			Screen screen = Minecraft.getInstance().gui.screen();
			if (screen == null || !MenuReader.strip(screen.getTitle()).equals("Tienda de Aura")) return;
			if (!MenuReader.strip(stack.getHoverName()).toUpperCase(Locale.ROOT).contains("MEJORA DE INVERSI")) return;
			KarmaConfig c = KarmaClient.config;
			for (String line : lines(c.investLimit, c.auraShopCap, c.auraShopStep, c.auraShopPrice, SidebarReader.aura, c.auraPrice))
				tooltip.add(Component.literal(line));
		});
	}

	/** Tooltip lines with legacy color codes; empty while the shop lore has not been read yet. */
	static List<String> lines(Double limit, Double cap, Double step, Double price, Double aura, double auraPrice) {
		if (cap == null || step == null || price == null) return List.of();
		List<String> out = new ArrayList<>(List.of("", ChatFormatting.GOLD + "KarmaMC Client"));
		if (limit == null) {
			out.add(ChatFormatting.RED + "Abre /invest para calcular");
			return out;
		}
		Calc.InvestLeft r = Calc.investLeft(limit, cap, step, Math.round(price), aura == null ? 0 : aura.longValue(), auraPrice);
		if (r.purchases() == 0) {
			out.add(ChatFormatting.GREEN + "Ya estás en el tope de inversión");
			return out;
		}
		String have = aura == null ? "? (scoreboard no leído)" : num(aura.longValue());
		out.add(ChatFormatting.YELLOW + "Faltan " + num(r.purchases()) + " mejoras (" + num(r.auraTotal()) + " Aura)");
		out.add(ChatFormatting.GRAY + "Tienes " + have + " → faltan " + num(r.auraMissing()) + " Aura");
		out.add(ChatFormatting.AQUA + "Por /trade: " + num(r.auraBuy()) + " Aura ≈ $" + Parse.shortNumber(r.cost()));
		return out;
	}

	private static String num(long v) {
		return String.format(Locale.ROOT, "%,d", v);
	}
}
