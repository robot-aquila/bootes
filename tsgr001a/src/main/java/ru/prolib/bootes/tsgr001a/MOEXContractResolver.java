package ru.prolib.bootes.tsgr001a;

import java.time.Instant;
import java.time.LocalDate;

import ru.prolib.aquila.core.BusinessEntities.Symbol;
import ru.prolib.aquila.web.utils.moex.MoexContractFileStorage;

public class MOEXContractResolver implements ContractResolver {
	private final MoexContractFileStorage storage;
	
	public MOEXContractResolver(MoexContractFileStorage storage) {
		this.storage = storage;
	}

	@Override
	public Symbol getSymbol(String contractPrefix, Instant time) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Symbol getSymbol(String contractPrefix, LocalDate date) {
		switch ( contractPrefix ) {
		case "RTS":
			
			break;
		default:
			throw new IllegalArgumentException("Unsupported contract: " + contractPrefix);
		}
		
		//storage.createReader(symbol)
		// TODO Auto-generated method stub
		return null;
	}

}
