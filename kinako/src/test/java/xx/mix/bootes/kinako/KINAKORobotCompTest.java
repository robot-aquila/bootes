package xx.mix.bootes.kinako;

import static org.junit.Assert.*;

import java.io.File;

import org.ini4j.Wini;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.config.KVStoreIni;
import ru.prolib.aquila.core.config.OptionProvider;
import ru.prolib.aquila.core.config.OptionProviderKvs;

public class KINAKORobotCompTest {

	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void testIniWithSemicolon() throws Exception {
		OptionProvider options = new OptionProviderKvs(
				new KVStoreIni(new Wini(new File("fixture/semicolon-test.ini")).get("main"))
			);
		
		String actual = options.getString("sqldb-url");
		
		String expected = "jdbc:hsqldb:file:D:/work/tmp/exante/sqldb/kinakodb;shutdown=true";
		assertEquals(expected, actual);
	}

}
