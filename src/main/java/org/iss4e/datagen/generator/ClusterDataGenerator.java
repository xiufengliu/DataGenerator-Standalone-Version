package org.iss4e.datagen.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;

import org.iss4e.datagen.common.AbstractGenerator;
import org.iss4e.datagen.common.Arg;
import org.iss4e.datagen.common.StackTracer;
import org.iss4e.datagen.common.Utils;

import de.lmu.ifi.dbs.elki.data.synthetic.bymodel.GeneratorSingleCluster;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Vector;
import de.lmu.ifi.dbs.elki.math.statistics.distribution.NormalDistribution;

public class ClusterDataGenerator extends AbstractGenerator {

	static final String[] usage = new String[] { 
		"--clusters={clustersize, (dim1.centroid, dim1.stddev)(dim2.centroid, dim2.stddev), ...}; {...}; ...",
		"--output=/tmp/cluster.csv"};

	final Arg clustersArg = new Arg(true, "clusters",
			"{100, (5.0, 1.0), (15.0, 1.0)}; {200, (12.0, 1.0)}");
	final Arg outputArg = new Arg(true, "output",
			"/tmp");

	public ClusterDataGenerator() {
		setUsage(usage);
		cmdLine.add(clustersArg);
		cmdLine.add(outputArg);
	}

	@Override
	public void generate(String[] params) {
		try {
			cmdLine.process(params);
			String clusterStr = cmdLine.getArg(clustersArg).getValue();
			String outputDir =  cmdLine.getArg(outputArg).getValue();
			StringBuilder buf = new StringBuilder();
			String[] clusterFormats = clusterStr.split(";");
			for (int i = 0; i < clusterFormats.length; ++i) {
				BufferedWriter out = new BufferedWriter(new FileWriter(String.format("%s/cluster%d.csv", outputDir, i+1), true));
				String s = Utils.removeSpecialChar(clusterFormats[i], new char[] {
						'{', '}', '(', ')', ' '});
				String[] clusterParams = s.split(",");
				int size = Integer.parseInt(clusterParams[0]);
				GeneratorSingleCluster clusterGen = new GeneratorSingleCluster(
						"cluster", 0, 1, new Random());
				for (int j=1; j<clusterParams.length; ++j){
					double centroid = Double.parseDouble(clusterParams[j]);
					double stddev = Double.parseDouble(clusterParams[++j]);
					clusterGen.addGenerator(new NormalDistribution(centroid, stddev, new Random()));	
				}
				
				List<Vector> results = clusterGen.generate(size);
				for (Vector vec : results) {
					buf.setLength(0);
					int ndim = vec.getDimensionality();
					for (int d = 0; d < ndim; ++d) {
						buf.append(vec.get(d));
						if (d<ndim-1){
							buf.append(",");
						}
					}
					buf.append("\n");
					out.write(buf.toString());
				}
				out.close();
			}
			
		} catch (IllegalArgumentException ex) {
			printUsage();
			StackTracer.printStackTrace(ex);
		} catch (Exception e) {
			StackTracer.printStackTrace(e);
		}
	}

	public static void main(String[] args) {
		ClusterDataGenerator generator = new ClusterDataGenerator();
		generator.generate(args);
	}

}
