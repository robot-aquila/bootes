package ru.prolib.bootes.protos.sos;

public class SOSExtensionChain implements SOSExtension {
	private final SOSExtension parent;
	private SOSExtensionChain child;
	
	public SOSExtensionChain(SOSExtension parent) {
		this.parent = parent;
	}
	
	public SOSExtensionChain and(SOSExtension child) {
		if ( this.child == null ) {
			this.child = new SOSExtensionChain(child);
		} else {
			this.child.and(child);
		}
		return this;
	}

	@Override
	public void apply(SOSComp comp) {
		try {
			parent.apply(comp);
		} catch ( Throwable t ) {
			t.printStackTrace();
		}
		if ( child != null ) {
			child.apply(comp);
		}
	}

}
