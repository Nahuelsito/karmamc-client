package cl.karmamc.client;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParseTest {
	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
			"$51.5M|51500000",
			"$1.28M|1280000",
			"$570.42m|570420000",
			"1.5b|1500000000",
			"1.5B|1500000000",
			"$26,000,000|26000000",
			"$1.000.000|1000000",
			"$721|721",
			"+1.000.000|1000000",
			"320.000.000|320000000",
			"7.82k|7820",
			"7.82K|7820",
			"18|18",
			"$721/s|721",
			"1,5m|1500000",
			"38.0|38",
	})
	void number(String in, double expected) {
		assertEquals(expected, Parse.number(in));
	}

	@Test
	void numberExactForCeil() {
		assertEquals(51_500_000.0, Parse.number("$51.5M"));
	}

	@Test
	void numberInvalid() {
		assertNull(Parse.number(""));
		assertNull(Parse.number("abc"));
		assertNull(Parse.number("$"));
		assertNull(Parse.number(null));
	}

	@Test
	void numberOverflowIsNull() {
		assertNull(Parse.number("9".repeat(400)));
		assertNull(Parse.number("9".repeat(400) + "b"));
	}

	@Test
	void shortNumberNotFinite() {
		assertEquals("?", Parse.shortNumber(Double.POSITIVE_INFINITY));
		assertEquals("?", Parse.shortNumber(Double.NaN, 2));
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
			"150400|150.4k",
			"1500000|1.5m",
			"2300000000|2.3b",
			"2000000000|2b",
			"2013000000|2b",
			"721|721",
			"0|0",
			"999|999",
			"1000|1k",
			"999960|1m",
			"12.5|12.5",
	})
	void shortNumber(double in, String expected) {
		assertEquals(expected, Parse.shortNumber(in));
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
			"[!] ¡Compraste MEJORA DE INVERSIÓN [ᴄʟɪᴄᴋ] por 30 Aura!|true",
			"[!] ¡Compraste MEJORA DE INVERSION por 1,000 Aura!|true",
			"[MaceGang] [$] Pepe: [!] ¡Compraste MEJORA DE INVERSIÓN [ᴄʟɪᴄᴋ] por 30 Aura!|false",
			"[!] ¡Compraste ESPADA [ᴄʟɪᴄᴋ] por 30 Aura!|false",
	})
	void investUpgradeBought(String in, boolean expected) {
		assertEquals(expected, Parse.investUpgradeBought(in));
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {
			"[!] Cobraste 619,423.00 $.|true",
			"[!] Cobraste $287.35K. Tus minions siguen trabajando.|true",
			"[MaceGang] [$] Pepe: [!] Cobraste 619,423.00 $.|false",
			"[!] ¡Compraste MEJORA DE INVERSIÓN [ᴄʟɪᴄᴋ] por 30 Aura!|false",
			"[!] Cobraste nada.|false",
	})
	void claim(String in, boolean expected) {
		assertEquals(expected, Parse.claim(in));
	}

	@ParameterizedTest
	@CsvSource(delimiter = '|', value = {"1980|1.98k", "2000|2k", "1900|1.9k", "60|60", "999999|1m", "12345678|12.35m"})
	void shortNumberTwoDecimals(double in, String expected) {
		assertEquals(expected, Parse.shortNumber(in, 2));
	}

	@Test
	void lines() {
		assertEquals(721.0, Parse.invest("Ingreso: $721/s"));
		assertEquals(721.0, Parse.invest("❙ Ingreso: $721/s"));
		assertEquals(51_500_000.0, Parse.investLimit("Invertido: $51.5M/51.5M"));
		assertEquals(51_500_000.0, Parse.investLimit("| Invertido: $20M/51.5M"));
		assertEquals(32.0, Parse.minion("  Dinero por segundo: $32"));
		assertEquals(33.0, Parse.gems("Ganas 33 Gemas cada 60s"));
		assertEquals(1.0, Parse.aura("❙ 1 Aura cada 3 horas"));
		assertEquals(570_420_000.0, Parse.sold("Progreso: $570.42m/1.5b (38.0%)"));
		assertEquals(1_000_000.0, Parse.shopStep("❙ Límite de inversión: +1.000.000"));
		assertEquals(320_000_000.0, Parse.shopCap("Tope del servidor: 320.000.000"));
		assertEquals(30.0, Parse.shopPrice("Precio: 30 Aura"));
		assertEquals(750.0, Parse.gemShopPrice("❙ Precio: 750 Gemas"));
		assertEquals(5_000.0, Parse.gemShopPrice("❙ Precio: 5.000 Gemas"));
		assertEquals(18.0, Parse.scoreboardAura("Aura 18"));
		assertEquals(18.0, Parse.scoreboardAura(" ❙ Aura: 18 "));
	}

	@Test
	void linesNoMatch() {
		assertNull(Parse.invest("Invertido: $51.5M/51.5M"));
		assertNull(Parse.minion("Dinero total: $32"));
		assertNull(Parse.shopPrice("Precio: 30 Gemas"));
		assertNull(Parse.gemShopPrice("Precio: 30 Aura"));
		assertNull(Parse.gemShopPrice("❙ Gana unos $6.000 por hora"));
		assertNull(Parse.gemShopPrice("❙ Comprado: 3.21k veces"));
		assertNull(Parse.scoreboardAura("Gemas 33"));
		assertNull(Parse.scoreboardAura("1 Aura cada 3 horas"));
	}

	@Test
	void gemsLevelLines() {
		assertEquals(25.0, Parse.gemsLevel("❙ Tu nivel ahora: 25"));
		assertEquals(26_000_000.0, Parse.gemsNextCost("❙ Nivel 26: cuesta $26,000,000"));
		assertEquals(1_000_000.0, Parse.gemsStep("(!) Cada nivel cuesta $1.000.000 mas que el anterior."));
		assertNull(Parse.gemsLevel("❙ Ganas 33 Gemas cada 60s"));
		assertNull(Parse.gemsNextCost("❙ Tu nivel ahora: 25"));
		assertNull(Parse.gemsStep("❙ Nivel 26: cuesta $26,000,000"));
	}

	@Test
	void duration() {
		assertEquals(3600.0, Parse.duration("1h"));
		assertEquals(5400.0, Parse.duration("90m"));
		assertEquals(129600.0, Parse.duration("1.5d"));
		assertEquals(30.0, Parse.duration("30S"));
		assertEquals(900.0, Parse.duration("0.25H"));
		assertEquals(165.0, Parse.duration("2.75m"), 1e-9);
		assertEquals(3600.0, Parse.duration(" 1h "));
		for (String bad : new String[] {"1h30m", "1.234h", "h", "-1h", "0h", "0.00m", "abc", "10", "", "1 h", "1,5h"})
			assertNull(Parse.duration(bad), bad);
		assertNull(Parse.duration(null));
	}

	@Test
	void scoreboardInvestAndGemsCountdown() {
		assertEquals(749.0, Parse.scoreboardInvest("Inversión $749/s"));
		assertEquals(749.0, Parse.scoreboardInvest("Inversion: $749/s"));
		assertEquals(1500.0, Parse.scoreboardInvest(" ❙ Inversión $1.5k/s "));
		assertNull(Parse.scoreboardInvest("MejoraInversión $749/s"));
		assertNull(Parse.scoreboardInvest("Aura 18"));
		assertEquals(35, Parse.scoreboardGemsCountdown("Gemas 19.1k (35s)"));
		assertEquals(0, Parse.scoreboardGemsCountdown(" ❙ Gemas: 120 (0s)"));
		assertNull(Parse.scoreboardGemsCountdown("Gemas 19.1k"));
		assertNull(Parse.scoreboardGemsCountdown("Inversión $749/s"));
	}
}
