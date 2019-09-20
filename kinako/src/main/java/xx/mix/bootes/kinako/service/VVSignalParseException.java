package xx.mix.bootes.kinako.service;

import ru.prolib.aquila.core.CoreException;

public class VVSignalParseException extends CoreException {
	private static final long serialVersionUID = 1L;
	
	private final String signalMessage;
	
	public VVSignalParseException(String msg, String signal_message, Throwable t) {
		super(msg, t);
		this.signalMessage = signal_message;
	}
	
	public VVSignalParseException(String msg, String signal_message) {
		super(msg);
		this.signalMessage = signal_message;
	}
	
	public String getSignalMessage() {
		return signalMessage;
	}

}
