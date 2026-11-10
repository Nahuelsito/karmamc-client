package cl.karmamc.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GemIntervalTest {
	@Test
	void detectsIntervalFromResets() {
		GemInterval t = new GemInterval();
		assertEquals(60, t.interval());
		for (int v = 59; v >= 0; v--) {
			t.update(v);
			t.update(v); // read every tick, same value repeats
		}
		t.update(59);
		assertEquals(60, t.interval());
		for (int v = 58; v >= 0; v--) t.update(v);
		t.update(39); // outpost: reset peak 39
		assertEquals(40, t.interval());
		for (int v = 38; v >= 0; v--) t.update(v);
		assertEquals(40, t.interval());
		t.update(59);
		assertEquals(60, t.interval());
		t.reset();
		assertEquals(60, t.interval());
	}
}
