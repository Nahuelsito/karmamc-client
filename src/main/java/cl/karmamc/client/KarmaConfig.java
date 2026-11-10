package cl.karmamc.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Persisted in config/karmaclient.json. Mutate fields, then call {@link #save()}. */
public class KarmaConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("karmaclient.json");

	public int hudX = 4;
	public int hudY = 4;
	public boolean showAura = false;
	/** Spawner type -> owned count. */
	public Map<String, Integer> spawners = new HashMap<>();

	// Cached menu readings; null = not read yet (Gson omits nulls, so absent = unknown).
	public Double investPerSecond;
	public Double minionsPerSecond;
	public Double gemsPer60s;
	public Double auraPer3h;
	public Double totalSold;
	/** Current invest limit: right side of `Invertido: $X/Y` in Inversiones. */
	public Double investLimit;
	/** Tienda de Aura, MEJORA DE INVERSIÓN: limit added per purchase, server cap, Aura per purchase. */
	public Double auraShopStep;
	public Double auraShopCap;
	public Double auraShopPrice;

	// Token prices (money), edited via /tokenprices.
	public double gemPricePer1k = 300_000;
	public double auraPrice = 200_000;

	public static KarmaConfig load() {
		if (Files.exists(PATH)) {
			try (Reader r = Files.newBufferedReader(PATH)) {
				KarmaConfig c = GSON.fromJson(r, KarmaConfig.class);
				if (c != null) {
					if (c.spawners == null) c.spawners = new HashMap<>();
					return c;
				}
			} catch (Exception e) {
				// Keep the broken file untouched; run with defaults until the next save overwrites it.
				KarmaClient.LOGGER.error("Failed to read {}, using defaults", PATH, e);
			}
		}
		return new KarmaConfig();
	}

	public void save() {
		try {
			Files.createDirectories(PATH.getParent());
			Path tmp = PATH.resolveSibling("karmaclient.json.tmp");
			try (Writer w = Files.newBufferedWriter(tmp)) {
				GSON.toJson(this, w);
			}
			Files.move(tmp, PATH, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			KarmaClient.LOGGER.error("Failed to save {}", PATH, e);
		}
	}
}
