package org.iss4e.datagen.variants;

import java.util.Random;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

public class Temperature extends AbstractVariant{
	double[] avgMonthlyTemp = {-4.2, -3.2, 1.3, 7.6, 14.2, 19.2, 22.2, 21.3, 17, 10.6, 4.8, -0.9};
	double[] days = {15.5, 45, 74.5, 105, 135.5, 166, 196.5, 227.5, 258, 288.5, 319, 349.5};
	//double[] hours = {372.0,   1080,   1788.0,   2520,   3252.0,   3984,   4716.0,   5460.0,   6192,   6924.0,   7656.0,   8388.0};
	
	double stdDev = 1.0;
	Random random = new Random();
	
	@Override
	public double[] generate(int size){
		UnivariatePeriodicInterpolator interpolator = new UnivariatePeriodicInterpolator(new SplineInterpolator(), days[11]);	
		UnivariateFunction func = interpolator.interpolate(days, avgMonthlyTemp);
		double[] temps = new double[size];
		for (int i=0; i<size/24; ++i){
			double avgDailyTemp = func.value(i);
			for (int j=0; j<24; ++j){
				temps[24*i+j] = stdDev*random.nextGaussian() + avgDailyTemp;
			}
		}
		return temps;
	}

	@Override
	public Vector genVector(int size) {
		return new Vector(generate(size));
	}


}
