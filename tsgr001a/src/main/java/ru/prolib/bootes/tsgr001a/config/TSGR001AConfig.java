package ru.prolib.bootes.tsgr001a.config;

import java.util.ArrayList;
import java.util.List;

public class TSGR001AConfig {
	private final List<TSGR001AInstConfig> list;
	
	public TSGR001AConfig(List<TSGR001AInstConfig> list) {
		this.list = new ArrayList<>(list);
	}
	
	public List<TSGR001AInstConfig> getListOfInstances() {
		return list;
	}

}
