package com.lab24;

import java.util.Random;

public class NumberGenerator {

	int feed = 34;
	int sequenceStart = 1443;
	Random r = new Random(sequenceStart);
	
	public long generateNumber() {
		return r.nextLong() % feed;
	}
}
