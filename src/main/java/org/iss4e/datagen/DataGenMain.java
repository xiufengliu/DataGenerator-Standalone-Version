package org.iss4e.datagen;

import org.iss4e.datagen.generator.ClusterDataGenerator;
import org.iss4e.datagen.common.ProgramDriver;
import org.iss4e.datagen.generator.MultiLinearRegressionDataGenerator;
import org.iss4e.datagen.generator.PARSmartMeterDataGenerator;
import org.iss4e.datagen.generator.SimpleLinearRegressionDataGenerator;
import org.iss4e.datagen.generator.ThreelineSmartMeterDataGenerator;

public class DataGenMain {

	public static void main(String argv[]) {
		int exitCode = -1;
		ProgramDriver pgd = new ProgramDriver();
		try {
			pgd.addClass("cluster", ClusterDataGenerator.class,
					"Generate clustered data");
			pgd.addClass("multilinearreg",
					MultiLinearRegressionDataGenerator.class,
					"Generate multiple linear regression data");
			pgd.addClass("simplelinearreg",
					SimpleLinearRegressionDataGenerator.class,
					"Generate simple linear regression data");
			pgd.addClass("threeline", ThreelineSmartMeterDataGenerator.class,
					"Generate threeline model data");
			pgd.addClass("par", PARSmartMeterDataGenerator.class,
					"Generate par model data");
			exitCode = pgd.run(argv);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.exit(exitCode);
	}
}
