package org.iss4e.datagen.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

import org.iss4e.datagen.common.Generator;
import org.iss4e.datagen.common.AbstractGenerator;
import org.iss4e.datagen.common.Arg;
import org.iss4e.datagen.common.StackTracer;
import org.iss4e.datagen.common.Utils;
import org.iss4e.datagen.models.SimpleLinearRegression;

import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.UniformDistribution;

public class SimpleLinearRegressionDataGenerator extends AbstractGenerator{

	static final String[] usage = new String[] { 
		"--size=n",
		"--params=(slope, intercept, erroStddev)",
		"--uniformdist=(min, max)",
		"--output=/tmp/data.csv"};

	final Arg sizeArg = new Arg(true, "size","100");
	final Arg paramsArg = new Arg(true, "params","(2, 0, 1.0)");
	final Arg uniformDistArg = new Arg(true, "uniformdist","(1, 10)");
	final Arg outputArg = new Arg(true, "output", "/tmp/data.csv");

	public SimpleLinearRegressionDataGenerator() {
		setUsage(usage);
		cmdLine.add(sizeArg);
		cmdLine.add(paramsArg);
		cmdLine.add(uniformDistArg);
		cmdLine.add(outputArg);
	}
	
	@Override
	public void generate(String[] params) {
		try{
			cmdLine.process(params);
			int size = Integer.parseInt(cmdLine.getArg(sizeArg).getValue());
			String parametersStr = cmdLine.getArg(paramsArg).getValue();
			String uniformDistStr = cmdLine.getArg(uniformDistArg).getValue();
			String outputPath = cmdLine.getArg(outputArg).getValue();
			
			String[] parameters = Utils.removeSpecialChar(parametersStr, new char[]{'(', ')', ' '}).split(",");
			String[] uniformDistParams = Utils.removeSpecialChar(uniformDistStr, new char[]{'(', ')', ' '}).split(",");
			double slope = Double.parseDouble(parameters[0]);
			double intercept = Double.parseDouble(parameters[1]);
			double errStddev = Double.parseDouble(parameters[2]);
			double min = Double.parseDouble(uniformDistParams[0]);
			double max = Double.parseDouble(uniformDistParams[1]);
			
			SimpleLinearRegression generator = new SimpleLinearRegression(slope, intercept, errStddev);
			UniformDistribution uniformDist = new UniformDistribution(min, max, new Random());
			double[]X = new double[size];
			for (int i=0; i<X.length; ++i){
				X[i] = uniformDist.nextRandom();
			}
			generator.setX(X);		
			Vector Y = generator.getYVector();
			
			BufferedWriter out = new BufferedWriter(new FileWriter(outputPath, false));
			out.write("X,Y\n");
			for (int i=0; i<size; ++i){
				out.write(X[i]+"," + Y.get(i) + "\n");
			}
			out.close();
		} catch(Exception e){
			super.printUsage();
			StackTracer.printStackTrace(e);
		}
	}

	public static void  main(String[]args){
		Generator generator = new SimpleLinearRegressionDataGenerator();
		generator.generate(args);
	}

}
