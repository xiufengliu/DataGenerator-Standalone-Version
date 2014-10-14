package org.iss4e.datagen.common;

import java.util.*;

public class Arg {
	String name;
	String value;
	boolean hasValue;
	List<String> values = new ArrayList<String>();

	public Arg() {
		this(false, null, null);
	}

	public Arg(String name) {
		this(false, name, null);
	}

	public Arg(String name, String value) {
		this(false, name, value);
	}

	public Arg(boolean takesValue, String name) {
		this(takesValue, name, null);
	}

	
	public Arg(boolean takesValue, String name, String value) {
		this.hasValue = takesValue;
		this.name = name;
		this.value = value;
	}

	void setName(String n) {
		name = n;
	}

	void setValue(String v) {
		value = v;
	}

	void addValue(String v) {
		values.add(v);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public List<String> getValues() {
		return values;
	}

	public boolean takesValue() {
		return hasValue;
	}

	public boolean matches(Arg arg) {
		return arg.getName().equalsIgnoreCase(name);
	}

	public boolean matches(String argName) {
		return argName.equalsIgnoreCase(name);
	}

	public String toString() {
		if (hasValue) {
			return name + "=" + value;
		} else {
			return name;
		}
	}
}