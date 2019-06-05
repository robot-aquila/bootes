package xx.mix.bootes.kinako;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.prolib.bootes.lib.app.App;
import ru.prolib.bootes.lib.app.AppRuntimeService;

public class KINAKO extends App {

	private static String[] patch_options(String[] args) {
		List<String> options = new ArrayList<>(Arrays.asList(args));
		boolean found_data_dir = false;
		for ( String str : options ) {
			if ( str.startsWith("--data-dir=") ) {
				found_data_dir = true;
				break;
			}
		}
		if ( ! found_data_dir ) {
			options.add("--data-dir=.");
		}
		return options.toArray(new String[options.size()]);
	}
	
	public static void main(String[] args) throws Throwable {
		System.exit(new KINAKO().run(patch_options(args)));
	}

	@Override
	protected void registerApplications(AppRuntimeService ars) {
		ars.addApplication(new KINAKORobotComp(serviceLocator));
	}

}
