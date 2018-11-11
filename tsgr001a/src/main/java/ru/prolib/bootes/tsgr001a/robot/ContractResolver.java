package ru.prolib.bootes.tsgr001a.robot;

import java.time.Instant;

public interface ContractResolver {
	
	ContractParams determineContract(Instant time);

}
