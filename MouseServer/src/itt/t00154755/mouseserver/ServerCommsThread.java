// package
package itt.t00154755.mouseserver;

//imports
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 
 * @author Christopher Donovan
 * <p>
 * This is the Server Communication Thread, used to
 * send the Accelerometer data to the Client.
 * 
 */
public class ServerCommsThread extends Thread 
{
	private static final String TAG = "Server Communication Thread";
	private ServerUtils sUtils = new ServerUtils();
	private InputStream dataIn;
	private String acceloData;
	private CursorRobot cRobot;

	/**
	 * 
	 * AppServer constructor initializes a new server once it is calls.
	 * 
	 * @param dataIn 
	 * 		the InputStream that was opened in the Server Class.
	 * 
	 */
	public ServerCommsThread(InputStream dataIn) 
	{
		sUtils.info( TAG );
		this.dataIn = dataIn;
	}
	
	/**
	 * 
	 * Override the run method of the Thread Class.
	 */
	@Override
	public void run() 
	{
		// set up the queue to store the strings while they
		// are being processed
		Deque<String> queue = new ArrayDeque<String>();
		
		try 
		{
			// create a sort of infinite loop that runs
			// while the program is running
			while(true) 
			{
				try 
				{
					// create a new byte array
					byte[] bytes = new byte[1024];
					int r;
					// read in the bytes
					while ((r = dataIn.read(bytes)) > 0) 
					{
						acceloData = (new String(bytes, 0, r));
						
						synchronized (acceloData) 
						{
							cRobot = new CursorRobot();
							queue.addFirst(acceloData);

							while (!queue.isEmpty()) 
							{
								cRobot.sendToRobot(queue.removeFirst());
							}
						}
					}
				} 
				catch (IOException ioe) {
					// print the error stack
					sUtils.error( TAG, ioe );
					// exit the program
					System.exit(-1);
				}
			}
		} 
		catch (Exception e) {
			// print the error stack
			sUtils.error( TAG, e );
			// exit the program
			System.exit(-1);
		} 
		finally 
		{
			try 
			{
				if (dataIn != null) 
				{
					// ensure the InputStream is closed on exit
					dataIn.close();
					// exit the program
					System.exit(0);
				}
			} 
			catch (IOException ioe) 
			{
				sUtils.error( TAG, ioe );
				// exit the program
				System.exit(-1);
			}
		}
		// exit the program
		System.exit(0);
	}
}// end of Class