package ru.prolib.bootes.tsgr001a;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppRuntimeService;

public class TSGR001A extends App {

	public static void main(String[] args) throws Throwable {
		System.exit(new TSGR001A().run(args));
	}

	@Override
	protected void registerApplications(AppRuntimeService ars) {
		// TODO Auto-generated method stub
		System.out.println("Register APPs");
	}

}
