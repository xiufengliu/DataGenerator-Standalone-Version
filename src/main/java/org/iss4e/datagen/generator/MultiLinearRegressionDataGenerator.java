package org.iss4e.datagen.generator;

import org.iss4e.datagen.common.AbstractGenerator;
import org.iss4e.datagen.common.Arg;
import org.iss4e.datagen.common.Generator;
import org.iss4e.datagen.common.Utils;
import org.iss4e.datagen.models.MultipleLinearRegression;
import org.iss4e.datagen.variants.HouseSize;
import org.iss4e.datagen.variants.Temperature;
import org.iss4e.datagen.variants.Variant;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;

public class MultiLinearRegressionDataGenerator extends AbstractGenerator {

	static final String[] usage = new String[] { 
		"--alphas=(alpha0, alpha1, ...)",
		"--output=/tmp/cluster.csv"
	};

	final Arg alphasArg = new Arg(true, "alphas",	"(5.0, 1.0)");
	final Arg outputArg = new Arg(true, "output",
			"/tmp");

	public MultiLinearRegressionDataGenerator() {
		setUsage(usage);
		cmdLine.add(alphasArg);
		cmdLine.add(outputArg);
	}
	
	
	@Override
	public void generate(String[] params) {
		int numOfObserv = 24*100;
		Vector x0 = new Vector(numOfObserv);
		for (int i = 0; i < numOfObserv; ++i) {
			x0.set(i, 1.0);
		}

		Variant temp = new Temperature(); // Monthly Avg temperature, intropolate as the mean + white noise (Gaussion distribution)
		Vector x1 = temp.genVector(numOfObserv);

		Variant house = new HouseSize(10.0, 200.0); // UniformDistribution
		Vector x2 = house.genVector(numOfObserv);

		Vector x3 = new Vector(numOfObserv);
		for (int i = 0; i < numOfObserv; ++i) { // Poisson distribution
			x3.set(i, Utils.getPoisson(4.5));
		}
		
		Vector parameters = new Vector(new double[] { 0, 0.2, 0.07, 0.6});
		Matrix x = new Matrix(numOfObserv, parameters.getDimensionality());
		
		x.setCol(0, x0);
		x.setCol(1, x1);
		x.setCol(2, x2);
		x.setCol(3, x3);
		
		
		MultipleLinearRegression generator = new MultipleLinearRegression(parameters, x, 1.0);
		System.out.println(generator.getYVector().toString());
		
	}

	public static void main(String[] args) {
		Generator generator = new MultiLinearRegressionDataGenerator();
		generator.generate(args);
	}

}
