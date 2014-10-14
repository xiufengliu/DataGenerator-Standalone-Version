package org.iss4e.datagen.variants;

import org.iss4e.datagen.seed.Seed;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.Distribution;

public interface Variant {
	void setSeed(Seed seed);

	void setDistribution(Distribution dist);

	double[] generate(int size);

	Vector genVector(int size);
}
