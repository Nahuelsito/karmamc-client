package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GemShopTooltipTest {
	private static List<String> plain(List<String> lines) {
		return lines.stream().map(ChatFormatting::stripFormatting).toList();
	}

	@Test
	void blaze() {
		// 33 gems/60s -> 1980/h; 1980 / 750 = 2.64
		assertEquals(List.of("", "Catfish69 QoL for KarmaMC", "Compras 2.64 por hora con tu /gemslevel"),
				plain(GemShopTooltip.lines(750.0, 33.0)));
	}

	@Test
	void roundsToTwoDecimals() {
		// 1 gem/60s -> 60/h; 60 / 400 = 0.15; 60 / 7 = 8.571 -> 8.57
		assertEquals("Compras 0.15 por hora con tu /gemslevel", plain(GemShopTooltip.lines(400.0, 1.0)).get(2));
		assertEquals("Compras 8.57 por hora con tu /gemslevel", plain(GemShopTooltip.lines(7.0, 1.0)).get(2));
	}

	@Test
	void noGemsLevel() {
		assertEquals(List.of("", "Catfish69 QoL for KarmaMC", "Abre /gemslevel para calcular"),
				plain(GemShopTooltip.lines(750.0, null)));
	}

	@Test
	void noPrice() {
		assertEquals(List.of(), GemShopTooltip.lines(null, 33.0));
		assertEquals(List.of(), GemShopTooltip.lines(null, null));
	}
}
