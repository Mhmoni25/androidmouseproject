package itt.t00154755.mouseserver;

import java.io.IOException;

public class RunApp {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		AppServer as = new AppServer();
		as.start();
	}

}
