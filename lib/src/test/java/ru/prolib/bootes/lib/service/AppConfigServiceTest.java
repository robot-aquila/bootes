package ru.prolib.bootes.lib.service;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ru.prolib.bootes.lib.config.AppConfig;
import ru.prolib.bootes.lib.config.AppConfigBuilder;
import ru.prolib.bootes.lib.config.AppConfigLoader;
import ru.prolib.bootes.lib.config.BasicConfigBuilder;
import ru.prolib.bootes.lib.config.ConfigException;
import ru.prolib.bootes.lib.config.OptionProvider;

public class AppConfigServiceTest {
	private IMocksControl control;
	private AppConfigService.Factory factory, factoryMock;
	private AppConfigService service, serviceWithMocks;
	private File temp;
	
	@Rule
	public ExpectedException eex = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		control = createStrictControl();
		factoryMock = control.createMock(AppConfigService.Factory.class);
		factory = new AppConfigService.Factory();
		service = new AppConfigService();
		serviceWithMocks = new AppConfigService(factoryMock);
		temp = File.createTempFile("bootes-app-config-test", ".ini");
	}
	
	@After
	public void tearDown() throws Exception {
		if ( temp != null ) {
			temp.delete();
		}
	}
	
	@Test
	public void testCtor0() {
		assertNotNull(service.getFactory());
	}
	
	@Test
	public void testCtor1() {
		assertSame(factoryMock, serviceWithMocks.getFactory());
	}
	
	@Test
	public void testFactory_CreateAppConfigBuilder() {
		AppConfigBuilder actual = factory.createAppConfigBuilder();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateAppConfigLoader() {
		AppConfigLoader actual = factory.createAppConfigLoader();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateOptions() {
		Options actual = factory.createOptions();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateHelpFormatter() {
		HelpFormatter actual = factory.createHelpFormatter();
		
		assertNotNull(actual);
	}
	
	@Test
	public void testFactory_CreateOptionProviderCli() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder().longOpt("foo").hasArg().build());
		options.addOption(Option.builder().longOpt("bar").hasArg().build());
		options.addOption(Option.builder().longOpt("buz").build());
		String args[] = {
				"--foo", "1",
				"--bar", "gizmo",
				"--buz"
		};
		
		OptionProvider op = factory.createOptionProviderCli(options, args);
		
		assertEquals("1", op.getString("foo"));
		assertEquals("gizmo", op.getString("bar"));
		assertTrue(op.hasOption("buz"));
	}
	
	@Test
	public void testFactory_CreateOptionProviderCli_ThrowsIfParseException() throws Exception {
		Options options = new Options();
		options.addOption(Option.builder().longOpt("foo").hasArg().build());
		String args[] = { "--foo" };
		eex.expect(ConfigException.class);
		eex.expectMessage("Missing argument for option: foo");
		
		factory.createOptionProviderCli(options, args);
	}

	@Test
	public void testFactory_CreateOptionProviderIni() throws Exception {
		FileUtils.writeStringToFile(temp, "[aquila-test]\nfoo=bar\nzulu=true\n");
		
		OptionProvider op = factory.createOptionProviderIni(temp, "aquila-test");
		
		assertEquals("bar", op.getString("foo"));
		assertTrue(op.getBoolean("zulu"));
	}

	@Test
	public void testFactory_CreateOptionProviderIni_ThrowsIfIOException() throws Exception {
		eex.expect(ConfigException.class);
		eex.expectMessage("Error loading config file: /a/b/c/d/hello (No such file or directory)");
		
		factory.createOptionProviderIni(new File("/a/b/c/d/hello"), "aquila-test");
	}
	
	@Test
	public void testFactory_CreateOptionProviderIni_ThrowsIfSectionNotFound() throws Exception {
		FileUtils.writeStringToFile(temp, "[aquila-test]\nfoo=bar\nzulu=true\n");
		eex.expect(ConfigException.class);
		eex.expectMessage("Section chuchumbara is not found in config file: " + temp);
		
		factory.createOptionProviderIni(temp, "chuchumbara");
	}

	@Test
	public void testFactory_CreatePrintWriter() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		
		PrintWriter writer = factory.createPrintWriter(stream);
		
		assertNotNull(writer);
		writer.println("hello, world!");
		writer.close();
		assertEquals("hello, world!\n", new String(stream.toByteArray()));
	}
	
	@Test
	public void testLoadConfig2() throws Exception {
		String args[] = {
				"--foo=bar",
				"--zulu=24"
		};
		AppConfig resultMock = control.createMock(AppConfig.class);
		BasicConfigBuilder bcBuilder = new BasicConfigBuilder();
		AppConfigBuilder builderMock = control.createMock(AppConfigBuilder.class);
		AppConfigLoader loaderMock = control.createMock(AppConfigLoader.class);
		Options optionsMock = control.createMock(Options.class);
		OptionProvider opMock = control.createMock(OptionProvider.class);
		expect(factoryMock.createAppConfigBuilder()).andReturn(builderMock);
		expect(factoryMock.createOptions()).andReturn(optionsMock);
		expect(factoryMock.createAppConfigLoader()).andStubReturn(loaderMock);
		loaderMock.configureOptions(optionsMock);
		expect(factoryMock.createOptionProviderCli(eq(optionsMock), aryEq(args))).andReturn(opMock);
		loaderMock.load(builderMock, opMock);
		expect(builderMock.getBasicConfigBuilder()).andReturn(bcBuilder);
		expect(builderMock.build()).andReturn(resultMock);
		control.replay();
		
		assertSame(resultMock, serviceWithMocks.loadConfig(args, "hello"));
		
		control.verify();
	}

	@Test
	public void testLoadConfig2_WithIniOverride() throws Exception {
		String args[] = {
				"--foo=bar",
				"--zulu=24"
		};
		AppConfig resultMock = control.createMock(AppConfig.class);
		BasicConfigBuilder bcBuilder = new BasicConfigBuilder().withConfigFile(new File("my-config.ini"));
		AppConfigBuilder builderMock = control.createMock(AppConfigBuilder.class);
		AppConfigLoader loaderMock = control.createMock(AppConfigLoader.class);
		Options optionsMock = control.createMock(Options.class);
		OptionProvider opCliMock = control.createMock(OptionProvider.class),
					   opIniMock = control.createMock(OptionProvider.class);
		expect(factoryMock.createAppConfigBuilder()).andReturn(builderMock);
		expect(factoryMock.createOptions()).andReturn(optionsMock);
		expect(factoryMock.createAppConfigLoader()).andStubReturn(loaderMock);
		loaderMock.configureOptions(optionsMock);
		expect(factoryMock.createOptionProviderCli(eq(optionsMock), aryEq(args))).andReturn(opCliMock);
		loaderMock.load(builderMock, opCliMock);
		expect(builderMock.getBasicConfigBuilder()).andReturn(bcBuilder);
		expect(factoryMock.createOptionProviderIni(new File("my-config.ini"), "tutumba")).andReturn(opIniMock);
		loaderMock.load(builderMock, opIniMock);
		loaderMock.load(builderMock, opCliMock);
		expect(builderMock.build()).andReturn(resultMock);
		control.replay();
		
		assertSame(resultMock, serviceWithMocks.loadConfig(args, "tutumba"));
		
		control.verify();
	}

	@Test
	public void testLoadConfig1() throws Exception {
		String args[] = {
				"--foo=bar",
				"--zulu=24"
		};
		AppConfig resultMock = control.createMock(AppConfig.class);
		BasicConfigBuilder bcBuilder = new BasicConfigBuilder();
		AppConfigBuilder builderMock = control.createMock(AppConfigBuilder.class);
		AppConfigLoader loaderMock = control.createMock(AppConfigLoader.class);
		Options optionsMock = control.createMock(Options.class);
		OptionProvider opMock = control.createMock(OptionProvider.class);
		expect(factoryMock.createAppConfigBuilder()).andReturn(builderMock);
		expect(factoryMock.createOptions()).andReturn(optionsMock);
		expect(factoryMock.createAppConfigLoader()).andStubReturn(loaderMock);
		loaderMock.configureOptions(optionsMock);
		expect(factoryMock.createOptionProviderCli(eq(optionsMock), aryEq(args))).andReturn(opMock);
		loaderMock.load(builderMock, opMock);
		expect(builderMock.getBasicConfigBuilder()).andReturn(bcBuilder);
		expect(builderMock.build()).andReturn(resultMock);
		control.replay();
		
		assertSame(resultMock, serviceWithMocks.loadConfig(args));
		
		control.verify();
	}

	@Test
	public void testLoadConfig1_WithIniOverride() throws Exception {
		String args[] = {
				"--foo=bar",
				"--zulu=24"
		};
		AppConfig resultMock = control.createMock(AppConfig.class);
		BasicConfigBuilder bcBuilder = new BasicConfigBuilder().withConfigFile(new File("my-config.ini"));
		AppConfigBuilder builderMock = control.createMock(AppConfigBuilder.class);
		AppConfigLoader loaderMock = control.createMock(AppConfigLoader.class);
		Options optionsMock = control.createMock(Options.class);
		OptionProvider opCliMock = control.createMock(OptionProvider.class),
					   opIniMock = control.createMock(OptionProvider.class);
		expect(factoryMock.createAppConfigBuilder()).andReturn(builderMock);
		expect(factoryMock.createOptions()).andReturn(optionsMock);
		expect(factoryMock.createAppConfigLoader()).andStubReturn(loaderMock);
		loaderMock.configureOptions(optionsMock);
		expect(factoryMock.createOptionProviderCli(eq(optionsMock), aryEq(args))).andReturn(opCliMock);
		loaderMock.load(builderMock, opCliMock);
		expect(builderMock.getBasicConfigBuilder()).andReturn(bcBuilder);
		expect(factoryMock.createOptionProviderIni(new File("my-config.ini"), "aquila")).andReturn(opIniMock);
		loaderMock.load(builderMock, opIniMock);
		loaderMock.load(builderMock, opCliMock);
		expect(builderMock.build()).andReturn(resultMock);
		control.replay();
		
		assertSame(resultMock, serviceWithMocks.loadConfig(args));
		
		control.verify();
	}
	
	@Test
	public void testShowHelp5() throws Exception {
		PrintWriter writerMock = control.createMock(PrintWriter.class);
		HelpFormatter helpFormatterMock = control.createMock(HelpFormatter.class);
		AppConfigLoader loaderMock = control.createMock(AppConfigLoader.class);
		Options optionsMock = control.createMock(Options.class);
		expect(factoryMock.createHelpFormatter()).andReturn(helpFormatterMock);
		expect(factoryMock.createOptions()).andReturn(optionsMock);
		expect(factoryMock.createAppConfigLoader()).andStubReturn(loaderMock);
		loaderMock.configureOptions(optionsMock);
		helpFormatterMock.printHelp(writerMock, 120, "my-syntax", "hello, world!", optionsMock, 0, 1, "bye");
		control.replay();
		
		serviceWithMocks.showHelp(writerMock, 120, "my-syntax", "hello, world!", "bye");
		
		control.verify();
	}

	@Test
	public void testShowHelp4() throws Exception {
		PrintWriter writerMock = control.createMock(PrintWriter.class);
		HelpFormatter helpFormatterMock = control.createMock(HelpFormatter.class);
		AppConfigLoader loaderMock = control.createMock(AppConfigLoader.class);
		Options optionsMock = control.createMock(Options.class);
		expect(factoryMock.createPrintWriter(System.out)).andReturn(writerMock);
		expect(factoryMock.createHelpFormatter()).andReturn(helpFormatterMock);
		expect(factoryMock.createOptions()).andReturn(optionsMock);
		expect(factoryMock.createAppConfigLoader()).andStubReturn(loaderMock);
		loaderMock.configureOptions(optionsMock);
		helpFormatterMock.printHelp(writerMock, 100, "utility [options]", "welcome", optionsMock, 0, 1, "regards");
		writerMock.close();
		control.replay();
		
		serviceWithMocks.showHelp(100, "utility [options]", "welcome", "regards");
		
		control.verify();
	}

}
