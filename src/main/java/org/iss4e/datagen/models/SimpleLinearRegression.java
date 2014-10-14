package org.iss4e.datagen.models;

import java.util.Random;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.NormalDistribution;

public class SimpleLinearRegression  {

	double slope;
	double intercept;
	double[] X;
	NormalDistribution norDist;

	public SimpleLinearRegression(double slope, double intercept, double errDev) {
		this.slope = slope;
		this.intercept = intercept;
		this.norDist = new NormalDistribution(0, errDev, new Random());
	}

	public void setX(double[] X) {
		this.X = X;
	}

	public void setX(double x) {
		setX(new double[] { x });
	}

	public double[] getYArray() {
		return getYVector().getArrayCopy();
	}

	public Vector getYVector() {
		Vector Y= (new Vector(X)).times(slope);
		Vector err = new Vector(X.length);
		for (int i = 0; i < X.length; i++) {
			err.set(i, norDist.nextRandom());
		}
		Y.plusEquals(err);
		return Y;
	}
}
