package cl.karmamc.client;

import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** `/fish afk calcular <tiempo>`: passive income over a duration, money and spawners proportional, gems and aura in whole intervals (60 s / 3 h). Pure logic. */
public final class AfkCalc {
	private AfkCalc() {}

	public static final String USAGE = ChatFormatting.RED + "Tiempo inválido. Usa un número (hasta 2 decimales) + s, m, h o d. Ej: /fish afk calcular 1.5h";

	public static List<String> lines(String label, double seconds, Double investPerSecond, Double minionsPerSecond,
			Double gemsPer60s, boolean showAura, Double auraPer3h, Map<String, Integer> spawners, Double totalSold) {
		List<String> out = new ArrayList<>();
		out.add(ChatFormatting.GOLD + "Catfish69 QoL for KarmaMC — AFK " + label);
		out.add(ChatFormatting.GREEN + (investPerSecond == null && minionsPerSecond == null
				? "Dinero: ? (abre /invest y la bóveda de minions)"
				: "Dinero (invest + minions): $" + Parse.shortNumber((orZero(investPerSecond) + orZero(minionsPerSecond)) * seconds)));
		out.add(ChatFormatting.LIGHT_PURPLE + (gemsPer60s == null
				? "Gemas: ? (abre /gemslevel)"
				: "Gemas: " + Parse.shortNumber(Math.floor(seconds / 60) * gemsPer60s, 2)));
		if (spawners.values().stream().anyMatch(n -> n != null && n > 0))
			out.add(ChatFormatting.GREEN + "Spawners: $" + Parse.shortNumber(Calc.spawnersPerHour(spawners, orZero(totalSold)) * seconds / 3600));
		if (showAura) out.add(ChatFormatting.RED + (auraPer3h == null
				? "Aura: ? (abre /prestige)"
				: "Aura: " + Parse.shortNumber(Math.floor(seconds / 10800) * auraPer3h, 2)));
		return out;
	}

	private static double orZero(Double d) { return d == null ? 0 : d; }
}
