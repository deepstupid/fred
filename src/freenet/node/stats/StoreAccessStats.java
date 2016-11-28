package freenet.node.stats;

public interface StoreAccessStats {
	
	long hits();
	
	long misses();
	
	long falsePos();
	
	long writes();
	
	default long readRequests() {
		return hits() + misses();
	}

	default long successfulReads() {
		if (readRequests() > 0)
			return hits();
		else
			return 0;
	}

	default double successRate() throws StatsNotAvailableException {
		if (readRequests() > 0)
			return (100.0 * hits() / readRequests());
		else
			throw new StatsNotAvailableException();
	}

	default double accessRate(long nodeUptimeSeconds) {
		return (1.0 * readRequests() / nodeUptimeSeconds);
	}

	default double writeRate(long nodeUptimeSeconds) {
		return (1.0 * writes() / nodeUptimeSeconds);
	}

}
