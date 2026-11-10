package cl.karmamc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

/** The per-hour HUD (PLAN.md Módulo 1). */
public final class KarmaHud {
	private KarmaHud() {}

	private static final int PAD = 2;

	public static List<String> lines() {
		KarmaConfig c = KarmaClient.config;
		List<String> out = new ArrayList<>();
		Double invest = SidebarReader.effectiveInvest();
		Double gems = SidebarReader.effectiveGemsPerPayout();
		out.add(invest == null && c.minionsPerSecond == null
				? "Dinero: ? (/invest, minions)"
				: "Dinero: $" + Parse.shortNumber((orZero(invest) + orZero(c.minionsPerSecond)) * 3600) + "/h");
		out.add(gems == null ? "Gemas: ? (/gemslevel)"
				: "Gemas: " + Parse.shortNumber(gems * 3600 / SidebarReader.GEM_INTERVAL.interval(), 2) + "/h");
		if (c.showAura) out.add(c.auraPer3h == null ? "Aura: ? (/prestige)" : "Aura: " + Parse.shortNumber(c.auraPer3h / 3) + "/h");
		if (c.spawners.values().stream().anyMatch(n -> n != null && n > 0))
			out.add("Spawners: $" + Parse.shortNumber(Calc.spawnersPerHour(c.spawners, orZero(c.totalSold))) + "/h");
		out.addAll(AfkSession.hudLines());
		return out;
	}

	private static double orZero(Double d) { return d == null ? 0 : d; }

	public static int width(Font font, List<String> lines) {
		int w = 0;
		for (String l : lines) w = Math.max(w, font.width(l));
		return w + PAD * 2;
	}

	public static int height(Font font, List<String> lines) {
		return lines.size() * font.lineHeight + PAD * 2;
	}

	/** Draws at the configured position, clamped to the screen; returns {x, y, w, h} of the box. */
	public static int[] draw(GuiGraphicsExtractor g, Font font) {
		List<String> lines = lines();
		int w = width(font, lines), h = height(font, lines);
		int x = Math.max(0, Math.min(KarmaClient.config.hudX, g.guiWidth() - w));
		int y = Math.max(0, Math.min(KarmaClient.config.hudY, g.guiHeight() - h));
		g.fill(x, y, x + w, y + h, 0x80000000);
		for (int i = 0; i < lines.size(); i++) g.text(font, lines.get(i), x + PAD, y + PAD + i * font.lineHeight, 0xFFFFFFFF, true);
		return new int[] {x, y, w, h};
	}

	/** HudElement callback: hidden with F1, F3 and while the edit screen draws its own copy. */
	public static void extract(GuiGraphicsExtractor g, Minecraft mc) {
		if (mc.gui.hud.isHidden() || mc.getDebugOverlay().showDebugScreen() || mc.gui.screen() instanceof HudEditScreen) return;
		draw(g, mc.font);
	}
}
