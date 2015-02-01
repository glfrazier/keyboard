package com.apogee.characteranalyzer;

public class FingerPattern {

	public static enum Finger {
		FORE(1), MIDDLE(1), RING(2), PINKY(3.5f);

		private float weight;

		private Finger(float w) {
			weight = w;
		}

		public float getWeight() {
			return weight;
		}
	}

	public static enum Hand {
		LEFT, RIGHT
	}

	public static enum Pressure {
		SOFT(1), MED(2), HARD(3);
		
		private float weight;

		private Pressure(float w) {
			weight = w;
		}

		public float getWeight() {
			return weight;
		}
	}

	private static int fingerDistance = 5;
	private static int handDistance = 4;
	private static int modifierDistance = 3;
	private static int shiftDistance = 3;
	private static int pressureDistance = 1;

	private final Hand hand;
	private final Finger finger;
	private final Pressure pressure;
	private final Finger modifier;
	private final boolean shift;

	private transient Integer hashcode = null;
	private transient String stringVal = null;

	public FingerPattern(Finger f, Hand h, Pressure p, Finger m, boolean sh) {
		finger = f;
		hand = h;
		pressure = p;
		modifier = m;
		shift = sh;
	}

	public FingerPattern(Finger f, Hand h, Pressure p) {
		this(f, h, p, null, false);
	}

	private void initialize() {
		if (finger != null)
			hashcode += finger.hashCode();
		if (hand != null)
			hashcode += 7 * hand.hashCode();
		if (pressure != null)
			hashcode += 13 * pressure.hashCode();
		if (modifier != null)
			hashcode += 19 * modifier.hashCode();
		StringBuffer buffer = new StringBuffer(hand.toString());
		buffer.append("-");
		if (modifier != null) {
			buffer.append("[");
			buffer.append(modifier.toString());
			buffer.append("]");
		}
		buffer.append(finger.toString());
		buffer.append("<");
		buffer.append(pressure.toString());
		buffer.append(">");
	}

	public boolean equals(Object o) {
		if (!(o instanceof FingerPattern)) {
			return false;
		}
		FingerPattern fp = (FingerPattern) o;
		return fp.finger == finger && fp.hand == hand
				&& fp.pressure == pressure && fp.modifier == modifier;
	}

	public int hashCode() {
		if (hashcode == null)
			initialize();
		return hashcode;
	}

	public String toString() {
		if (stringVal == null) {
			initialize();
		}
		return stringVal;
	}

	public float getWeight() {
		float weight = finger.getWeight() * pressure.getWeight();
		if (modifier != null)
			weight += 1.5*modifier.getWeight();
		if (shift)
			weight *= 1.5;
		return weight;
	}

	public int getDistance(FingerPattern pattern) {
		int distance = 0;
		if (finger != pattern.finger) {
			distance += fingerDistance;
		}
		if (hand != pattern.hand) {
			distance += handDistance;
		}
		if (pressure != pattern.pressure) {
			distance += pressureDistance;
		}
		if (modifier != pattern.modifier) {
			distance += modifierDistance;
		}
		if (shift != pattern.shift) {
			distance += shiftDistance;
		}
		return distance;
	}
}
