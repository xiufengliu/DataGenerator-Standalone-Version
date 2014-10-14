package org.iss4e.datagen.variants;

import org.iss4e.datagen.seed.Seed;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.Distribution;

public abstract class AbstractVariant implements Variant {
	Seed seed;
	Distribution dist;

	public void setSeed(Seed seed) {
		this.seed = seed;
	}

	public void setDistribution(Distribution dist) {
		this.dist = dist;
	}
}
