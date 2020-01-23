package ru.prolib.bootes.lib.config;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.aquila.core.BusinessEntities.Account;
import ru.prolib.aquila.core.config.ConfigException;
import ru.prolib.aquila.core.config.KVStoreHash;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;
import ru.prolib.aquila.qforts.impl.QForts;

public class QFTerminalConfigTest {
	
	@Rule
	public ExpectedException eex = ExpectedException.none();

	private IMocksControl control;
	private Map<String, String> options1, options2;
	private QFTerminalConfig service1, service2;

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		service1 = new QFTerminalConfig(new OptionProviderKvs(new KVStoreHash(options1 = new LinkedHashMap<>())));
		service2 = new QFTerminalConfig(new OptionProviderKvs(new KVStoreHash(options2 = new LinkedHashMap<>())));
	}
	
	@Test
	public void testGetTestAccount() throws Exception {
		options1.put("qforts-test-account", "KUMHO");
		options2.put("qforts-test-account", "BABAKA");
		
		assertEquals(new Account("KUMHO"), service1.getTestAccount());
		assertEquals(new Account("BABAKA"), service2.getTestAccount());
	}
	
	@Test
	public void testGetTestAccount_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("qforts-test-account option expected to be not null");

		service2.getTestAccount();
	}

	@Test
	public void testGetTestBalance() throws Exception {
		options1.put("qforts-test-balance", "50000");
		options2.put("qforts-test-balance", "85000");
		
		assertEquals(ofRUB2("50000.00"), service1.getTestBalance());
		assertEquals(ofRUB2("85000.00"), service2.getTestBalance());
	}
	
	@Test
	public void testGetTestBalance_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("qforts-test-balance option expected to be not null");
		
		service2.getTestBalance();
	}

	@Test
	public void testGetDataDir() throws Exception {
		options1.put("qforts-data-dir", "/foo/bar");
		options2.put("qforts-data-dir", "kamarillo");
		
		assertEquals(new File("/foo/bar"), service1.getDataDirectory());
		assertEquals(new File("kamarillo"), service2.getDataDirectory());
	}
	
	@Test
	public void testGetDataDir_ThrowsIfNotDefined() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("qforts-data-dir option expected to be not null");
		
		service2.getDataDirectory();
	}
	
	@Test
	public void testGetLiquidityMode() throws Exception {
		options1.put("qforts-liquidity-mode", "0");
		assertEquals(QForts.LIQUIDITY_LIMITED, service1.getLiquidityMode());
		
		options1.put("qforts-liquidity-mode", "1");
		assertEquals(QForts.LIQUIDITY_APPLY_TO_ORDER, service1.getLiquidityMode());
		
		options1.put("qforts-liquidity-mode", "2");
		assertEquals(QForts.LIQUIDITY_UNLIMITED, service1.getLiquidityMode());
	}
	
	@Test
	public void testGetLiquidityMode_ThrowsIfModeIsNotKnown() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("qforts-liquidity-mode expected to be 0, 1 or 2 but: 3");
		options1.put("qforts-liquidity-mode", "3");
		
		service1.getLiquidityMode();
	}
	
	@Test
	public void testEquals() {
		OptionProvider
			opMock1 = control.createMock(OptionProvider.class),
			opMock2 = control.createMock(OptionProvider.class);
		QFTerminalConfig service = new QFTerminalConfig(opMock1);
		
		assertTrue(service.equals(service));
		assertTrue(service.equals(new QFTerminalConfig(opMock1)));
		assertFalse(service.equals(new QFTerminalConfig(opMock2)));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}

}
