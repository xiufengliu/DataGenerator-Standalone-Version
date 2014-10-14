package org.iss4e.datagen.models;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.NormalDistribution;

import java.util.Random;

public class MultipleLinearRegression {

	Vector parameters;
	Matrix x;
	NormalDistribution norDist;

	public MultipleLinearRegression(Vector parameters, Matrix x,
			double errDev) {
		this.parameters = parameters;
		this.x = x;
		this.norDist = new NormalDistribution(0, errDev, new Random());
	}

	public MultipleLinearRegression(Vector parameters, Matrix x) {
		this(parameters, x, 1.0);
	}

	public Vector getYVector() {
		Vector Y = x.times(parameters);
		Vector err = new Vector(x.getRowDimensionality());
		for (int i = 0; i < x.getRowDimensionality(); i++) {
			err.set(i, norDist.nextRandom());
		}
		//Y.plusEquals(err);
		return Y;
	}


}
