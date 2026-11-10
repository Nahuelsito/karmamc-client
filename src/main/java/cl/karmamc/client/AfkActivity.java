package cl.karmamc.client;

import java.util.ArrayDeque;

/**
 * Auto-stop detector for `/fish afk start` (PLAN.md v1.1.1). Pure logic, fed once per client tick (20/s).
 * Rule A: 5+ input events within any 10 s window. Rule B: 10 s of continuous activity (gaps of at most 1 s).
 * Jump and sneak are never passed in, so they never count.
 */
public final class AfkActivity {
	// Held-key bits passed to tick().
	public static final int ATTACK = 1, USE = 2, FORWARD = 4, BACK = 8, LEFT = 16, RIGHT = 32, SPRINT = 64;
	// Sprint excluded: toggle-sprint keeps it held while standing still. Its press still counts for Rule A.
	static final int MOVE = FORWARD | BACK | LEFT | RIGHT;

	static final int GRACE_TICKS = 40, WINDOW_TICKS = 200, MAX_EVENTS = 5, CONTINUOUS_TICKS = 200, MAX_GAP_TICKS = 20;
	static final int CAMERA_QUIET_TICKS = 10;
	static final float CAMERA_DEGREES = 1f;

	private int tick = -1;
	private int prevKeys;
	private boolean prevScreen = true; // the chat that ran the command is open: its closing never counts
	private final ArrayDeque<Integer> events = new ArrayDeque<>();
	private int lastCamera = Integer.MIN_VALUE / 2, runStart = -1, lastActive = Integer.MIN_VALUE / 2;

	/**
	 * @param keys bitmask of held keys (ATTACK, USE, FORWARD, ...)
	 * @param cameraDegrees |yaw change| + |pitch change| this tick
	 * @param screenOpen any screen (chat, inventory, container) is open
	 * @return true when the session must stop
	 */
	public boolean tick(int keys, float cameraDegrees, boolean screenOpen) {
		tick++;
		int pressed = keys & ~prevKeys;
		boolean opened = screenOpen && !prevScreen;
		prevKeys = keys;
		prevScreen = screenOpen;
		boolean camera = cameraDegrees > CAMERA_DEGREES;
		// A camera event starts a gesture: movement after >= 0.5 s still, so one long sweep is one event (Rule B covers long sweeps).
		boolean cameraEvent = camera && tick - lastCamera > CAMERA_QUIET_TICKS;
		if (camera) lastCamera = tick;
		if (tick < GRACE_TICKS) return false;

		int count = Integer.bitCount(pressed) + (opened ? 1 : 0) + (cameraEvent ? 1 : 0);
		for (int i = 0; i < count; i++) events.add(tick);
		while (!events.isEmpty() && events.peekFirst() <= tick - WINDOW_TICKS) events.removeFirst();
		if (events.size() >= MAX_EVENTS) return true;

		if ((keys & MOVE) != 0 || camera) {
			if (runStart < 0 || tick - lastActive - 1 > MAX_GAP_TICKS) runStart = tick;
			lastActive = tick;
			return tick - runStart >= CONTINUOUS_TICKS;
		}
		return false;
	}
}
