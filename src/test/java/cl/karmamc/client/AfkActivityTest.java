package cl.karmamc.client;

import org.junit.jupiter.api.Test;

import static cl.karmamc.client.AfkActivity.*;
import static org.junit.jupiter.api.Assertions.*;

class AfkActivityTest {
	/** Detector in a manual session, already past the 2 s grace period. */
	private static AfkActivity afterGrace() {
		AfkActivity a = new AfkActivity();
		a.startSession(true);
		run(a, 40, 0);
		return a;
	}

	/** Feeds n ticks; returns the first non-NONE signal, or NONE. */
	private static Signal run(AfkActivity a, int n, int keys) {
		Signal out = Signal.NONE;
		for (int i = 0; i < n; i++) {
			Signal s = a.tick(keys, false);
			if (out == Signal.NONE) out = s;
		}
		return out;
	}

	/** n taps of a key (1 tick down, 1 s apart). */
	private static Signal taps(AfkActivity a, int n, int key) {
		Signal out = Signal.NONE;
		for (int i = 0; i < n; i++) {
			Signal s = run(a, 1, key);
			if (out == Signal.NONE) out = s;
			s = run(a, 19, 0);
			if (out == Signal.NONE) out = s;
		}
		return out;
	}

	@Test
	void fiveClicksInTenSecondsStops() {
		assertEquals(Signal.STOP, taps(afterGrace(), 5, ATTACK));
	}

	@Test
	void fiveJumpTapsInTenSecondsStops() {
		assertEquals(Signal.STOP, taps(afterGrace(), 5, JUMP));
	}

	@Test
	void fourTapsInTenSecondsDoesNotStop() {
		AfkActivity a = afterGrace();
		assertEquals(Signal.NONE, taps(a, 4, JUMP));
		assertEquals(Signal.NONE, run(a, 200, 0));
		assertEquals(Signal.NONE, taps(a, 4, SNEAK)); // old taps left the window
	}

	@Test
	void holdingKeysDoesNotStop() {
		assertEquals(Signal.NONE, run(afterGrace(), 15 * 20, ATTACK | USE));
		assertEquals(Signal.NONE, run(afterGrace(), 30 * 20, FORWARD));
		assertEquals(Signal.NONE, run(afterGrace(), 30 * 20, SPRINT | SNEAK)); // toggle-sprint/sneak report as held
	}

	@Test
	void openingScreensCount() {
		AfkActivity b = afterGrace();
		Signal any = Signal.NONE;
		for (int i = 0; i < 5; i++) {
			Signal s = b.tick(0, true);
			if (any == Signal.NONE) any = s;
			b.tick(0, false);
		}
		assertEquals(Signal.STOP, any);
	}

	@Test
	void gracePeriodIgnoresActivity() {
		AfkActivity a = new AfkActivity();
		a.startSession(true);
		Signal any = Signal.NONE;
		for (int i = 0; i < 40; i++) {
			Signal s = a.tick(i % 2 == 0 ? ATTACK : 0, i % 2 == 0);
			if (any == Signal.NONE) any = s;
		}
		assertEquals(Signal.NONE, any);
		assertEquals(Signal.NONE, run(a, 30 * 20, 0));
	}

	@Test
	void keyHeldThroughGraceIsNotANewPress() {
		AfkActivity a = new AfkActivity();
		a.startSession(true);
		run(a, 40, ATTACK);
		assertEquals(Signal.NONE, run(a, 30 * 20, ATTACK));
	}

	@Test
	void idleFiveMinutesAutoStarts() {
		AfkActivity a = new AfkActivity();
		assertEquals(Signal.NONE, run(a, IDLE_TICKS - 1, 0));
		assertEquals(Signal.AUTO_START, a.tick(0, false));
		assertEquals(Signal.NONE, run(a, 30 * 20, 0)); // now in a session, no second start
	}

	@Test
	void inputAtFourFiftyNineResets() {
		AfkActivity a = new AfkActivity();
		run(a, IDLE_TICKS - 20, 0);
		assertEquals(Signal.NONE, run(a, 1, JUMP));
		assertEquals(0, a.idleTicks());
		assertEquals(Signal.NONE, run(a, IDLE_TICKS - 1, 0));
		assertEquals(Signal.AUTO_START, a.tick(0, false));
	}

	@Test
	void holdingKeysDoesNotResetIdle() {
		AfkActivity a = new AfkActivity();
		run(a, 1, FORWARD | ATTACK); // initial press resets
		assertEquals(Signal.NONE, run(a, IDLE_TICKS - 1, FORWARD | ATTACK));
		assertEquals(Signal.AUTO_START, a.tick(FORWARD | ATTACK, false));
	}

	@Test
	void claimInputResetsIdle() {
		AfkActivity a = new AfkActivity();
		run(a, IDLE_TICKS - 1, 0);
		a.input();
		assertEquals(Signal.NONE, run(a, IDLE_TICKS - 1, 0));
	}

	@Test
	void autoStartedSessionHasNoGraceAndStops() {
		AfkActivity a = new AfkActivity();
		run(a, IDLE_TICKS, 0);
		assertEquals(Signal.STOP, taps(a, 5, ATTACK));
	}

	@Test
	void sessionEndRestartsIdleFromZero() {
		AfkActivity a = afterGrace();
		run(a, IDLE_TICKS * 2, 0); // in a session: never auto-starts
		a.endSession();
		assertEquals(Signal.NONE, run(a, IDLE_TICKS - 1, 0));
		assertEquals(Signal.AUTO_START, a.tick(0, false));
	}

	@Test
	void clockFormat() {
		assertEquals("01:23:05", AfkSession.clock(4985));
		assertEquals("00:00:00", AfkSession.clock(0));
	}
}
