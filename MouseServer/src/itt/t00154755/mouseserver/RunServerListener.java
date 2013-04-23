package itt.t00154755.mouseserver;

import java.io.IOException;

/**
 * 
 * @author Christopher Donovan - T00154755
 * 
 * Class used to run the server listener.
 * 
 */
public class RunServerListener
{

	/**
	 * This is the main method use to start the server app.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main( String[] args ) throws IOException
	{
		// create a new thread that calls the runnable object.
		Thread runningServer = new Thread(new AppServerListener());
		// start the Thread
		runningServer.start();

	}// end of main method

}// end of main method
