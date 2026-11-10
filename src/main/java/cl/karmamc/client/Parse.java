package cl.karmamc.client;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Number parsing/formatting and lore-line parsers. Pure logic: input lines must already be format-stripped. */
public final class Parse {
	private Parse() {}

	private static final String NUM = "([$+]?[0-9][0-9.,]*[kmb]?)";
	private static final int FLAGS = Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
	private static final Pattern NUMBER = Pattern.compile("([0-9][0-9.,]*?)([kmb])?", FLAGS);
	private static final Pattern THOUSANDS = Pattern.compile("[0-9]{1,3}([.,][0-9]{3})+");

	private static final Pattern INVEST = Pattern.compile("Ingreso:\\s*" + NUM, FLAGS);
	private static final Pattern INVEST_LIMIT = Pattern.compile("Invertido:\\s*[$+]?[0-9][0-9.,]*[kmb]?\\s*/\\s*" + NUM, FLAGS);
	private static final Pattern MINION = Pattern.compile("Dinero por segundo:\\s*" + NUM, FLAGS);
	private static final Pattern GEMS = Pattern.compile("Ganas\\s+" + NUM + "\\s+Gemas\\s+cada\\s+60s", FLAGS);
	private static final Pattern AURA = Pattern.compile(NUM + "\\s+Aura\\s+cada\\s+3\\s+horas", FLAGS);
	private static final Pattern SOLD = Pattern.compile("Progreso:\\s*" + NUM + "\\s*/", FLAGS);
	private static final Pattern SHOP_STEP = Pattern.compile("L[ií]mite de inversi[oó]n:\\s*" + NUM, FLAGS);
	private static final Pattern SHOP_CAP = Pattern.compile("Tope del servidor:\\s*" + NUM, FLAGS);
	private static final Pattern SHOP_PRICE = Pattern.compile("Precio:\\s*" + NUM + "\\s*Aura", FLAGS);
	private static final Pattern GEM_SHOP_PRICE = Pattern.compile("Precio:\\s*" + NUM + "\\s*Gemas", FLAGS);
	private static final Pattern GEMS_LEVEL = Pattern.compile("Tu nivel ahora:\\s*" + NUM, FLAGS);
	private static final Pattern GEMS_NEXT_COST = Pattern.compile("Nivel\\s+[0-9]+:\\s*cuesta\\s+" + NUM, FLAGS);
	private static final Pattern GEMS_STEP = Pattern.compile("Cada nivel cuesta\\s+" + NUM + "\\s+m[aá]s", FLAGS);
	// Anchored at "[!]" so a player typing the same text in chat (prefixed by rank/name) does not match.
	private static final Pattern INVEST_UPGRADE_BOUGHT = Pattern.compile("^\\[!]\\s*¡?Compraste MEJORA DE INVERSI[OÓ]N.*\\spor\\s+[0-9.,]+\\s+Aura!?$", FLAGS);
	// Anchored at `[!]` so player chat (rank/name prefix) never matches.
	private static final Pattern CLAIM = Pattern.compile("^\\[!]\\s*Cobraste\\s+\\$?[0-9][0-9.,]*[kmb]?\\s*\\$?\\.", FLAGS);
	private static final Pattern DURATION = Pattern.compile("([0-9]+(?:\\.[0-9]{1,2})?)([smhd])", Pattern.CASE_INSENSITIVE);
	private static final Pattern SCOREBOARD_AURA =Pattern.compile("(?<![\\p{L}0-9])Aura:?\\s+" + NUM, FLAGS);

	/**
	 * Parses `$51.5M`, `1.5b`, `$26,000,000`, `$1.000.000`, `+721`, `7.82k`, `$721/s`.
	 * With a k/m/b suffix `.`/`,` is decimal; without it, 3-digit groups are thousands separators.
	 * Returns null when unparseable. BigDecimal keeps e.g. 51.5M exactly 51_500_000.
	 */
	public static Double number(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.endsWith("/s")) s = s.substring(0, s.length() - 2);
		while (!s.isEmpty() && (s.charAt(0) == '$' || s.charAt(0) == '+')) s = s.substring(1);
		while (!s.isEmpty() && (s.endsWith(".") || s.endsWith(","))) s = s.substring(0, s.length() - 1);
		Matcher m = NUMBER.matcher(s);
		if (!m.matches()) return null;
		String digits = m.group(1);
		String suffix = m.group(2);
		if (suffix == null && THOUSANDS.matcher(digits).matches()) {
			digits = digits.replace(".", "").replace(",", "");
		} else {
			digits = digits.replace(',', '.');
		}
		BigDecimal v;
		try {
			v = new BigDecimal(digits);
		} catch (NumberFormatException e) {
			return null; // e.g. "1.2.3"
		}
		if (suffix != null) {
			int exp = switch (Character.toLowerCase(suffix.charAt(0))) {
				case 'k' -> 3;
				case 'm' -> 6;
				default -> 9;
			};
			v = v.scaleByPowerOfTen(exp);
		}
		double d = v.doubleValue();
		return Double.isFinite(d) ? d : null;
	}

	/** `150.4k`, `1.5m`, `2b`, `721`: one decimal, `.0` dropped. */
	public static String shortNumber(double v) {
		return shortNumber(v, 1);
	}

	/** Same as {@link #shortNumber(double)} with up to `decimals` decimals, trailing zeros dropped (`1.98k`). */
	public static String shortNumber(double v, int decimals) {
		if (!Double.isFinite(v)) return "?";
		if (v < 0) return "-" + shortNumber(-v, decimals);
		String[] suffixes = {"", "k", "m", "b"};
		int i = 0;
		BigDecimal scaled = round(v, decimals);
		while (i < suffixes.length - 1 && scaled.compareTo(BigDecimal.valueOf(1000)) >= 0) {
			i++;
			scaled = round(v / Math.pow(1000, i), decimals);
		}
		return scaled.stripTrailingZeros().toPlainString() + suffixes[i];
	}

	private static BigDecimal round(double v, int decimals) {
		return BigDecimal.valueOf(v).setScale(decimals, java.math.RoundingMode.HALF_UP);
	}

	private static Double find(Pattern p, String line) {
		if (line == null) return null;
		Matcher m = p.matcher(line);
		return m.find() ? number(m.group(1)) : null;
	}

	/** `Ingreso: $721/s` -> 721 (money/s). */
	public static Double invest(String line) { return find(INVEST, line); }

	/** `Invertido: $51.5M/51.5M` -> right side (current limit). */
	public static Double investLimit(String line) { return find(INVEST_LIMIT, line); }

	/** `Dinero por segundo: $32` -> 32 (one minion). */
	public static Double minion(String line) { return find(MINION, line); }

	/** `Ganas 33 Gemas cada 60s` -> 33. */
	public static Double gems(String line) { return find(GEMS, line); }

	/** `1 Aura cada 3 horas` -> 1. */
	public static Double aura(String line) { return find(AURA, line); }

	/** `Progreso: $570.42m/1.5b (38.0%)` -> left side (total sold). */
	public static Double sold(String line) { return find(SOLD, line); }

	/** `Límite de inversión: +1.000.000` -> 1e6 (step per purchase). */
	public static Double shopStep(String line) { return find(SHOP_STEP, line); }

	/** `Tope del servidor: 320.000.000` -> 320e6. */
	public static Double shopCap(String line) { return find(SHOP_CAP, line); }

	/** `Precio: 30 Aura` -> 30. */
	public static Double shopPrice(String line) { return find(SHOP_PRICE, line); }

	/** Gem shop `❙ Precio: 750 Gemas` -> 750. */
	public static Double gemShopPrice(String line) { return find(GEM_SHOP_PRICE, line); }

	/** `❙ Tu nivel ahora: 25` -> 25 (gems level). */
	public static Double gemsLevel(String line) { return find(GEMS_LEVEL, line); }

	/** `❙ Nivel 26: cuesta $26,000,000` -> 26e6 (cost of the next gems level). */
	public static Double gemsNextCost(String line) { return find(GEMS_NEXT_COST, line); }

	/** `(!) Cada nivel cuesta $1.000.000 mas que el anterior.` -> 1e6 (cost increase per level). */
	public static Double gemsStep(String line) { return find(GEMS_STEP, line); }

	/** Chat `[!] ¡Compraste MEJORA DE INVERSIÓN [ᴄʟɪᴄᴋ] por 30 Aura!` -> true. */
	public static boolean investUpgradeBought(String line) {
		return line != null && INVEST_UPGRADE_BOUGHT.matcher(line.trim()).matches();
	}

	/** Chat `[!] Cobraste 619,423.00 $.` (invest) or `[!] Cobraste $287.35K. Tus minions siguen trabajando.` -> true. */
	public static boolean claim(String line) {
		return line != null && CLAIM.matcher(line.trim()).find();
	}

	/** Sidebar `Aura 18` -> 18. */
	public static Double scoreboardAura(String line) { return find(SCOREBOARD_AURA, line); }

	/** `/fish afk calcular` time: `1.5h`, `90m`, `30S` -> seconds; one unit (s/m/h/d), up to 2 decimals, > 0; else null. */
	public static Double duration(String s) {
		if (s == null) return null;
		Matcher m = DURATION.matcher(s.trim());
		if (!m.matches()) return null;
		double v = Double.parseDouble(m.group(1));
		if (v <= 0) return null;
		return v * switch (Character.toLowerCase(m.group(2).charAt(0))) {
			case 's' -> 1;
			case 'm' -> 60;
			case 'h' -> 3600;
			default -> 86400;
		};
	}
}
