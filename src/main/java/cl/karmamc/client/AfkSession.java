package cl.karmamc.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;

/** `/fish afk start|stop`: tracks an AFK session, shows it on the HUD and prints a summary at the end. Display only. */
public final class AfkSession {
	private AfkSession() {}

	private static final int RECALC_TICKS = 40;

	private static final long IDLE_NANOS = AfkActivity.IDLE_TICKS * 50_000_000L;

	private static AfkActivity detector = new AfkActivity();
	private static boolean running;
	private static long startNanos;
	/** Time of the last input event (or of entering the world); an auto-started session begins here. */
	private static long idleSinceNanos = System.nanoTime();
	private static boolean claimed;
	private static int ticks;
	private static List<String> cachedEstimates = List.of();
	private static ClientLevel level;
	/** Summary of a session that ended while leaving a world; printed once the player is in another one. */
	private static List<String> pending;
	private static ClientLevel pendingLevel;

	public static boolean active() { return running; }

	public static String start(Minecraft mc) {
		if (active()) return ChatFormatting.YELLOW + "Ya hay una sesión AFK activa. Usa /fish afk stop para terminarla.";
		detector.startSession(true);
		begin(mc, System.nanoTime());
		return ChatFormatting.GREEN + "Sesión AFK iniciada. Se detiene sola si te mueves, haces clicks o abres menús.";
	}

	/** Stops from the command; returns null when a session was running (the summary is printed), else a reply. */
	public static String stop(Minecraft mc) {
		if (!active()) return ChatFormatting.YELLOW + "No hay ninguna sesión AFK activa. Usa /fish afk start.";
		finish(mc);
		return null;
	}

	/** Server `[!] Cobraste ...` claim: ends the session (or resets the idle counter) on the next tick, after the message shows. */
	public static void onClaim() { claimed = true; }

	/** Disconnect: end the session now, print the summary in the next world. */
	public static void onDisconnect(Minecraft mc) {
		if (!active()) return;
		pending = summary();
		pendingLevel = level;
		end();
	}

	public static void tick(Minecraft mc) {
		if (pending != null && mc.player != null && mc.level != null && mc.level != pendingLevel) {
			print(mc, pending);
			pending = null;
			pendingLevel = null;
		}
		if (active() && mc.level != level) onDisconnect(mc); // server switch (new world instance) or disconnect
		long now = System.nanoTime();
		if (mc.player == null || mc.level == null) { // idle counting only runs in a world
			detector = new AfkActivity();
			idleSinceNanos = now;
			claimed = false;
			return;
		}
		if (claimed) {
			claimed = false;
			if (active()) {
				finish(mc);
				return;
			}
			detector.input();
			idleSinceNanos = now;
		}
		var o = mc.options;
		int keys = bit(o.keyAttack.isDown(), AfkActivity.ATTACK) | bit(o.keyUse.isDown(), AfkActivity.USE)
				| bit(o.keyUp.isDown(), AfkActivity.FORWARD) | bit(o.keyDown.isDown(), AfkActivity.BACK)
				| bit(o.keyLeft.isDown(), AfkActivity.LEFT) | bit(o.keyRight.isDown(), AfkActivity.RIGHT)
				| bit(o.keySprint.isDown(), AfkActivity.SPRINT) | bit(o.keyJump.isDown(), AfkActivity.JUMP)
				| bit(o.keyShift.isDown(), AfkActivity.SNEAK);
		AfkActivity.Signal signal = detector.tick(keys, mc.gui.screen() != null);
		if (detector.idleTicks() == 0) idleSinceNanos = now;
		switch (signal) {
			case STOP -> {
				finish(mc);
				return;
			}
			case AUTO_START -> {
				begin(mc, Math.min(idleSinceNanos, now - IDLE_NANOS)); // timer shows at least 05:00
				mc.player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW
						+ "AFK detectado (5 min). Sesión AFK iniciada; /fish afk stop para terminar."));
			}
			case NONE -> {}
		}
		if (active() && ++ticks % RECALC_TICKS == 0) cachedEstimates = estimates(elapsedSeconds());
	}

	/** HUD section: timer (live, whole seconds) + estimates cached every 2 s. Empty when no session. */
	public static List<String> hudLines() {
		if (!active()) return List.of();
		List<String> out = new ArrayList<>();
		out.add(ChatFormatting.GOLD + "AFK " + clock(elapsedSeconds()));
		out.addAll(cachedEstimates);
		return out;
	}

	private static void begin(Minecraft mc, long start) {
		running = true;
		startNanos = start;
		ticks = 0;
		level = mc.level;
		cachedEstimates = estimates(elapsedSeconds());
	}

	private static void finish(Minecraft mc) {
		List<String> lines = summary();
		end();
		print(mc, lines);
	}

	private static void end() {
		running = false;
		detector.endSession();
		idleSinceNanos = System.nanoTime();
	}

	private static List<String> summary() {
		double s = elapsedSeconds();
		return lines("terminado " + clock(s), s);
	}

	private static void print(Minecraft mc, List<String> lines) {
		if (mc.player == null) return;
		for (String line : lines) mc.player.sendSystemMessage(Component.literal(line));
		mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1f));
	}

	private static List<String> estimates(double seconds) {
		List<String> all = lines("", seconds);
		return all.subList(1, all.size()); // drop the chat header
	}

	private static List<String> lines(String label, double seconds) {
		KarmaConfig c = KarmaClient.config;
		return AfkCalc.lines(label, seconds, c.investPerSecond, c.minionsPerSecond, c.gemsPer60s, c.showAura, c.auraPer3h,
				c.spawners, c.totalSold);
	}

	private static double elapsedSeconds() { return Math.floor((System.nanoTime() - startNanos) / 1e9); }

	static String clock(double seconds) {
		long s = (long) seconds;
		return String.format("%02d:%02d:%02d", s / 3600, s / 60 % 60, s % 60);
	}

	private static int bit(boolean down, int bit) { return down ? bit : 0; }
}
