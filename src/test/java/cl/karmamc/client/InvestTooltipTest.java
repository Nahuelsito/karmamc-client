package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvestTooltipTest {
	private static List<String> plain(List<String> lines) {
		return lines.stream().map(ChatFormatting::stripFormatting).toList();
	}

	@Test
	void planCheck() {
		assertEquals(List.of("", "Catfish69 QoL for KarmaMC",
						"Faltan 269 mejoras (8,070 Aura)",
						"Tienes 18 → faltan 8,052 Aura",
						"Por /trade: 10,065 Aura ≈ $2b"),
				plain(InvestTooltip.lines(51.5e6, 320e6, 1e6, 30.0, 18.0, 200_000)));
	}

	@Test
	void atCap() {
		assertEquals(List.of("", "Catfish69 QoL for KarmaMC", "Ya estás en el tope de inversión"),
				plain(InvestTooltip.lines(320e6, 320e6, 1e6, 30.0, 18.0, 200_000)));
	}

	@Test
	void missingLimit() {
		assertEquals(List.of("", "Catfish69 QoL for KarmaMC", "Abre /invest para calcular"),
				plain(InvestTooltip.lines(null, 320e6, 1e6, 30.0, 18.0, 200_000)));
	}

	@Test
	void missingShopOrAura() {
		assertEquals(List.of(), InvestTooltip.lines(51.5e6, null, 1e6, 30.0, 18.0, 200_000));
		assertEquals("Tienes ? (scoreboard no leído) → faltan 8,070 Aura",
				plain(InvestTooltip.lines(51.5e6, 320e6, 1e6, 30.0, null, 200_000)).get(3));
	}
}
