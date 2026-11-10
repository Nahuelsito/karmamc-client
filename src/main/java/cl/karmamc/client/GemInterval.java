package cl.karmamc.client;

/** Gem payout interval (60 s, or 40 s with an outpost) from the sidebar `(Ns)` countdown. Pure logic, runtime only. */
public final class GemInterval {
	public static final int DEFAULT = 60;

	private int interval = DEFAULT;
	private Integer last;

	public int interval() { return interval; }

	/** Feed each countdown read; a jump up is a reset, and the value right after it tells the interval. */
	public void update(int countdown) {
		if (last != null && countdown > last) interval = countdown > 40 ? 60 : 40;
		last = countdown;
	}

	public void reset() {
		interval = DEFAULT;
		last = null;
	}
}
