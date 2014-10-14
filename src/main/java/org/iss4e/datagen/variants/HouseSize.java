package org.iss4e.datagen.variants;

import java.util.Random;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.Distribution;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.UniformDistribution;

public class HouseSize extends AbstractVariant {

	Distribution dist;

	public HouseSize(double minArea, double maxArea) {
		dist = new UniformDistribution(minArea, maxArea, new Random());
	}

	@Override
	public double[] generate(int size) {
		return genVector(size).getArrayCopy();
	}

	@Override
	public Vector genVector(int size) {
		Vector data = new Vector(size);
		for (int i = 0; i < size; ++i) {
			data.set(i, dist.nextRandom());
		}
		return data;
	}

}
