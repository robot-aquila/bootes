package ru.prolib.bootes.lib.data.ts.filter;

public interface IFilter<ArgType> {
	String getID();
	boolean approve(ArgType arg);
}
