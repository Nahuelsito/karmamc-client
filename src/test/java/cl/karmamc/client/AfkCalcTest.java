package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AfkCalcTest {
	private static List<String> plain(List<String> lines) {
		return lines.stream().map(ChatFormatting::stripFormatting).toList();
	}

	@Test
	void userExampleOneHour() {
		List<String> lines = AfkCalc.lines("1h", 3600, 14.0, 14.0, 1.0, false, null, Map.of("blaze", 1), 0.0);
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 1h", "Dinero (invest + minions): $100.8k", "Gemas: 60", "Spawners: $6k"),
				plain(lines));
		assertTrue(lines.get(0).startsWith(ChatFormatting.GOLD.toString()));
		assertTrue(lines.get(2).startsWith(ChatFormatting.LIGHT_PURPLE.toString()));
	}

	@Test
	void unknownValuesAndAura() {
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 30m", "Dinero: ? (abre /invest y la bóveda de minions)",
						"Gemas: ? (abre /gemslevel)", "Aura: ? (abre /prestige)"),
				plain(AfkCalc.lines("30m", 1800, null, null, null, true, null, Map.of("blaze", 0), null)));
	}

	@Test
	void auraProportional() {
		// 1 Aura / 3h over 1.5h = 0.5; spawner line hidden with no spawners
		assertEquals(List.of("Catfish69 QoL for KarmaMC — AFK 1.5h", "Dinero (invest + minions): $3.6k", "Gemas: 0.5", "Aura: 0.5"),
				plain(AfkCalc.lines("1.5h", 5400, null, 0.6667, 1.0 / 180, true, 1.0, Map.of(), null)));
	}
}
