package itt.t00154755.mouseserver;

/**
 * 
 * @author Christopher Donovan
 * @author RunServer.java
 * @since 10/02/2015
 * @version 2.0
 * 
 * Class used to run the server listener.
 * This class simply creates an reference to the Server,
 * and a thread to run the Server on.
 * 
 * * @author Christopher Donovan
 * 
 * @since 26/04/2013
 * @version 4.06
 */
public class RunServer
{

	/**
	 * Constructor:
	 * used by the ServerGUI GUI to start the server.
	 */
	public void startServer()
	{
		// create a new thread that calls the runnable object.
		Thread runningServer = new Thread(new Server());
		// start the Thread
		runningServer.start();
	}

}// end of main method
