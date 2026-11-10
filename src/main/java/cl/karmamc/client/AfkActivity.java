package cl.karmamc.client;

import java.util.ArrayDeque;

/**
 * AFK input detector (PLAN.md v1.1.2). Pure logic, fed once per client tick (20/s) while in a world.
 * Input event = new press edge of a tracked key (holding counts once) or opening a screen (no screen -> screen).
 * In a session: 5+ events within any 10 s window -> STOP (ignored for 2 s after a manual start).
 * Outside a session: 5 min without events -> AUTO_START.
 */
public final class AfkActivity {
	// Held-key bits passed to tick().
	public static final int ATTACK = 1, USE = 2, FORWARD = 4, BACK = 8, LEFT = 16, RIGHT = 32, SPRINT = 64, JUMP = 128,
			SNEAK = 256;

	public enum Signal { NONE, STOP, AUTO_START }

	static final int GRACE_TICKS = 40, WINDOW_TICKS = 200, MAX_EVENTS = 5, IDLE_TICKS = 5 * 60 * 20;

	private int tick;
	private int prevKeys;
	private boolean prevScreen = true; // joining a world or the chat that ran a command: closing never counts
	private int idleTicks;
	private boolean session;
	private int graceUntil;
	private final ArrayDeque<Integer> events = new ArrayDeque<>();

	/**
	 * @param keys bitmask of held keys (ATTACK, USE, FORWARD, ...)
	 * @param screenOpen any screen (chat, inventory, container) is open
	 */
	public Signal tick(int keys, boolean screenOpen) {
		tick++;
		int count = Integer.bitCount(keys & ~prevKeys) + (screenOpen && !prevScreen ? 1 : 0);
		prevKeys = keys;
		prevScreen = screenOpen;
		if (count > 0) idleTicks = 0;
		else idleTicks++;

		if (!session) {
			if (idleTicks < IDLE_TICKS) return Signal.NONE;
			startSession(false);
			return Signal.AUTO_START;
		}
		if (tick <= graceUntil) return Signal.NONE;
		for (int i = 0; i < count; i++) events.add(tick);
		while (!events.isEmpty() && events.peekFirst() <= tick - WINDOW_TICKS) events.removeFirst();
		return events.size() >= MAX_EVENTS ? Signal.STOP : Signal.NONE;
	}

	/** Session begins: manual (`/fish afk start`, 2 s grace) or auto (no grace). */
	public void startSession(boolean grace) {
		session = true;
		graceUntil = grace ? tick + GRACE_TICKS : tick;
		events.clear();
	}

	/** Session ended (any reason): the idle counter restarts from 0. */
	public void endSession() {
		session = false;
		idleTicks = 0;
	}

	/** A non-key input (claim message): resets the idle counter. */
	public void input() { idleTicks = 0; }

	/** Ticks since the last input event. */
	public int idleTicks() { return idleTicks; }
}
