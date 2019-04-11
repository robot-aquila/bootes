package ru.prolib.bootes.lib.cr;

import java.time.Instant;

public interface ContractResolver {
	
	ContractParams determineContract(Instant time);

}
