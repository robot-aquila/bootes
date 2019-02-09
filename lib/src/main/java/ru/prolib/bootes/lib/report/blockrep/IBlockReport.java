package ru.prolib.bootes.lib.report.blockrep;

import java.util.List;

public interface IBlockReport {
	IBlock getBlock(String typeID);
	List<IBlock> getBlocks();
	void setBlock(IBlock block);
	IBlock getEarlyBlock();
}
