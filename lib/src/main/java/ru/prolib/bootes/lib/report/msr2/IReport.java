package ru.prolib.bootes.lib.report.msr2;

import java.util.List;

public interface IReport {
	IBlock getBlock(String typeID);
	List<IBlock> getBlocks();
	void setBlock(IBlock block);
	IBlock getEarlyBlock();
}
