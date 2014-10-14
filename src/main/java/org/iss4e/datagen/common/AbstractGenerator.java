package org.iss4e.datagen.common;

public abstract class AbstractGenerator implements Generator {

	protected CommandLine cmdLine = new CommandLine();
	private String[] usage = new String[] { "Forget the usage!" };

	public abstract void generate(String[] params);

	protected void setUsage(String[] a) {
		usage = a;
	}

	protected void printUsage() {
		for (int i = 0; i < usage.length; i++) {
			System.err.println(usage[i]);
		}
	}
}
