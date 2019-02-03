package ru.prolib.bootes.lib.report.msr2;

import static org.junit.Assert.*;
import static ru.prolib.aquila.core.BusinessEntities.CDecimalBD.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Before;
import org.junit.Test;

import ru.prolib.aquila.core.utils.Variant;

public class ReportTest {
	
	static Instant T(String timeString) {
		return Instant.parse(timeString);
	}
	
	private IBlock block1, block2, block3;
	private Map<String, IBlock> blocks;
	private Report service;

	@Before
	public void setUp() throws Exception {
		block1 = new Block("foo", of("123.456"), T("2019-01-31T09:41:31Z"));
		block2 = new Block("bar", of("456.123"), T("2019-01-31T09:52:19Z"));
		block3 = new Block("buz", null, null);
		blocks = new LinkedHashMap<>();
		service = new Report(blocks);
	}
	
	@Test
	public void testGetBlocks() {
		service.setBlock(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		
		List<IBlock> expected = new ArrayList<>();
		expected.add(block1);
		expected.add(block2);
		expected.add(block3);
		assertEquals(expected, service.getBlocks());
	}

	@Test
	public void testGetBlock() {
		service.setBlock(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		
		assertSame(block1, service.getBlock("foo"));
		assertSame(block2, service.getBlock("bar"));
		assertSame(block3, service.getBlock("buz"));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testGetBlock_ThrowsIfNotExists() {
		service.setBlock(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		
		service.getBlock("ave");
	}
	
	@Test
	public void testGetEarlyBlock() {
		service.setBlock(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		
		assertSame(block1, service.getEarlyBlock());
		
		IBlock block4 = new Block("zoo", null, T("2019-01-31T00:00:00Z"));
		service.setBlock(block4);
		
		assertSame(block4, service.getEarlyBlock());
	}
	
	
	@Test
	public void testGetEarlyBlock_ReturnAnyIfNoTimedBlocks() {
		IBlock block4 = new Block("alp", null, null);
		service.setBlock(block4);
		service.setBlock(new Block("goo", null, null));
		
		assertEquals(block4, service.getEarlyBlock());
	}
	
	@Test (expected=IllegalStateException.class)
	public void testGetEarlyBlock_ThrowsIfNoBlocks() {
		service.getEarlyBlock();
	}
	
	@Test
	public void testEquals_SpecialCases() {
		assertTrue(service.equals(service));
		assertFalse(service.equals(null));
		assertFalse(service.equals(this));
	}
	
	@Test
	public void testEquals() {
		service = new Report(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		List<IBlock> blocks1 = new ArrayList<>(), blocks2 = new ArrayList<>();
		blocks1.add(block1);
		blocks1.add(block2);
		blocks1.add(block3);
		blocks2.add(block3);
		blocks2.add(block1);
		Variant<List<IBlock>> vBlocks = new Variant<>(blocks1, blocks2);
		Variant<?> iterator = vBlocks;
		int foundCnt = 0;
		Report x, found = null;
		do {
			List<IBlock> block_list = vBlocks.get();
			x = new Report(block_list.get(0));
			for ( int i = 1; i < block_list.size(); i ++ ) {
				x.setBlock(block_list.get(i));
			}
			if ( service.equals(x) ) {
				foundCnt ++;
				found = x;
			}
		} while ( iterator.next() );
		assertEquals(1, foundCnt);
		assertEquals(service.getBlocks(), found.getBlocks());
	}
	
	@Test
	public void testToString() {
		service.setBlock(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		String expected = new StringBuilder()
				.append("Report[blocks={")
				.append("foo=").append(block1).append(", ")
				.append("bar=").append(block2).append(", ")
				.append("buz=").append(block3)
				.append("}]")
				.toString();
		
		assertEquals(expected, service.toString());
	}
	
	@Test
	public void testHashCode() {
		service.setBlock(block1);
		service.setBlock(block2);
		service.setBlock(block3);
		int expected = new HashCodeBuilder(1766257, 924053)
				.append(blocks)
				.build();
		
		assertEquals(expected, service.hashCode());
	}

}
