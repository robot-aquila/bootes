package ru.prolib.bootes.lib.robo.sh.statereq;

import ru.prolib.bootes.lib.robo.ISessionDataHandler;

public interface ISessionDataTrackable extends IStateObservable {
	ISessionDataHandler getSessionDataHandler();
}
