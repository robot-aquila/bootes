package ru.prolib.bootes.tsgr001a;

import java.time.Instant;
import java.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Symbol;

public interface ContractResolver {
	
	Symbol getSymbol(String contractPrefix, Instant time);
	Symbol getSymbol(String contractPrefix, LocalDate date);

}
