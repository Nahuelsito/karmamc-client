package cl.karmamc.client;

import org.junit.jupiter.api.Test;

import static cl.karmamc.client.AfkActivity.*;
import static org.junit.jupiter.api.Assertions.*;

class AfkActivityTest {
	/** Detector already past the 2 s grace period. */
	private static AfkActivity afterGrace() {
		AfkActivity a = new AfkActivity();
		run(a, 40, 0, 0);
		return a;
	}

	/** Feeds n ticks; returns true if any of them stopped the session. */
	private static boolean run(AfkActivity a, int n, int keys, float camera) {
		boolean stop = false;
		for (int i = 0; i < n; i++) stop |= a.tick(keys, camera, false);
		return stop;
	}

	/** n clicks (1 tick down, 1 s apart). */
	private static boolean clicks(AfkActivity a, int n) {
		boolean stop = false;
		for (int i = 0; i < n; i++) stop |= run(a, 1, ATTACK, 0) | run(a, 19, 0, 0);
		return stop;
	}

	@Test
	void fiveClicksInTenSecondsStops() {
		assertTrue(clicks(afterGrace(), 5));
	}

	@Test
	void fourClicksInTenSecondsDoesNotStop() {
		AfkActivity a = afterGrace();
		assertFalse(clicks(a, 4));
		assertFalse(run(a, 200, 0, 0));
		assertFalse(clicks(a, 4)); // old clicks left the window
	}

	@Test
	void holdingAttackOrUseDoesNotStop() {
		assertFalse(run(afterGrace(), 15 * 20, ATTACK | USE, 0));
	}

	@Test
	void jumpAndSneakAreNeverPassedSoIdleNeverStops() {
		// KarmaClient never puts jump/sneak in the mask: holding them looks like an idle tick.
		assertFalse(run(afterGrace(), 30 * 20, 0, 0));
	}

	@Test
	void walkingTenSecondsStops() {
		assertTrue(run(afterGrace(), 10 * 20 + 1, FORWARD, 0));
	}

	@Test
	void toggleSprintHeldWhileStandingDoesNotStop() {
		assertFalse(run(afterGrace(), 30 * 20, SPRINT, 0));
	}

	@Test
	void walkingWithTwoSecondPauseDoesNotStop() {
		AfkActivity a = afterGrace();
		assertFalse(run(a, 8 * 20, FORWARD, 0));
		assertFalse(run(a, 2 * 20, 0, 0));
		assertFalse(run(a, 8 * 20, FORWARD, 0));
	}

	@Test
	void walkingWithShortPausesIsContinuous() {
		AfkActivity a = afterGrace();
		assertFalse(run(a, 6 * 20, FORWARD, 0));
		assertFalse(run(a, 10, 0, 0)); // 0.5 s gap does not reset
		assertTrue(run(a, 5 * 20, FORWARD, 0));
	}

	@Test
	void longMouseSweepCountsAsOneEvent() {
		AfkActivity a = afterGrace();
		assertFalse(run(a, 5 * 20, 0, 3f)); // 5 s sweep: one event, Rule B not reached
		assertFalse(run(a, 30, 0, 0));
		assertFalse(clicks(a, 3)); // sweep + 3 clicks = 4 events
	}

	@Test
	void mouseSweepTenSecondsStops() {
		assertTrue(run(afterGrace(), 10 * 20 + 1, 0, 3f));
	}

	@Test
	void tinyCameraJitterIgnored() {
		assertFalse(run(afterGrace(), 30 * 20, 0, 0.5f));
	}

	@Test
	void openingScreensCount() {
		AfkActivity a = afterGrace();
		boolean stop = false;
		for (int i = 0; i < 5; i++) {
			stop |= a.tick(0, 0, true);
			stop |= a.tick(0, 0, false);
		}
		assertTrue(stop);
	}

	@Test
	void gracePeriodIgnoresActivity() {
		AfkActivity a = new AfkActivity();
		boolean stop = false;
		for (int i = 0; i < 39; i++) stop |= a.tick(i % 2 == 0 ? ATTACK : 0, 5f, i % 2 == 0);
		assertFalse(stop);
		assertFalse(run(a, 30 * 20, 0, 0));
	}

	@Test
	void keyHeldThroughGraceIsNotANewPress() {
		AfkActivity a = new AfkActivity();
		run(a, 40, ATTACK, 0);
		assertFalse(run(a, 30 * 20, ATTACK, 0));
	}

	@Test
	void clockFormat() {
		assertEquals("01:23:05", AfkSession.clock(4985));
		assertEquals("00:00:00", AfkSession.clock(0));
	}
}
