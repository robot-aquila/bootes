package ru.prolib.bootes.lib.cr;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class MOEXContractResolverRegistry implements ContractResolverRegistry {
	private final ZoneId zoneID; 
	private final Map<String, ContractResolver> cache;
	
	public MOEXContractResolverRegistry(ZoneId zoneID, Map<String, ContractResolver> cache) {
		this.zoneID = zoneID;
		this.cache = cache;
	}
	
	public MOEXContractResolverRegistry() {
		this(ZoneId.of("Europe/Moscow"), new HashMap<>());
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.bootes.lib.cr.ContractResolverRegistry#getResolver(java.lang.String)
	 */
	@Override
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
