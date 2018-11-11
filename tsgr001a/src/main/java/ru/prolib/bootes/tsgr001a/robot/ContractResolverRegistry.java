package ru.prolib.bootes.tsgr001a.robot;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class ContractResolverRegistry {
	private final ZoneId zoneID; 
	private final Map<String, ContractResolver> cache;
	
	public ContractResolverRegistry(ZoneId zoneID, Map<String, ContractResolver> cache) {
		this.zoneID = zoneID;
		this.cache = cache;
	}
	
	public ContractResolverRegistry() {
		this(ZoneId.of("Europe/Moscow"), new HashMap<>());
	}
	
	public synchronized ContractResolver getResolver(String symbolName) {
		ContractResolver r = cache.get(symbolName);
		if ( r == null ) {
			switch ( symbolName ) {
			case "RTS":
			case "Si":
				r = new ContractResolverM3RTS(zoneID, symbolName, 3, 15);
				break;
			default:
				throw new IllegalArgumentException("Contract not supported: " + symbolName);
			}
			cache.put(symbolName, r);
		}
		return r;
	}

}
