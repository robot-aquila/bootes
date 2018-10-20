package ru.prolib.bootes.tsgr001a.robot;

import ru.prolib.aquila.core.BusinessEntities.Terminal;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2DataProvider;
import ru.prolib.aquila.utils.experimental.sst.sdp2.SDP2Key;

public interface RobotServiceLocator {
	
	SDP2DataProvider<SDP2Key> getDataSliceProvider();
	
	Terminal getTerminal();

}
