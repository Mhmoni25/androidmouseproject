// package
package itt.t00154755.mouseserver;

// imports
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 
 * @author Christopher Donovan
 * <p>
 * This class creates a stream connection using the Bluecove API.
 * The stream listens for a RFComm client once a client is found the
 * stream connection is closed, to ensure that only one client is connected.
 * <p>
 * {@link http://docs.oracle.com/javase/tutorial/networking/index.html}
 * <p>
 * {@link http://docs.oracle.com/javase/tutorial/essential/exceptions/index.html}
 * 
 */
public class AppServer extends Thread
{
	// string name of class
	private final String TAG = "App Server";
	private ServerUtils sUtils = new ServerUtils();
	// host device
	private LocalDevice pcDevice;
	// the connection string
	private final String connString = "btspp://localhost:"
			+ "0000000000000000000000000000ABCD;name=App Server;"
			+ "authenticate=false;encrypt=false;master=false";

	/**
	 * AppServer constructor initializes a new server once it is calls.
	 * 
	 */
	public AppServer() 
	{
		try 
		{
			pcDevice = LocalDevice.getLocalDevice();
		} 
		catch (BluetoothStateException bse) 
		{
			sUtils.error( TAG, bse );
		}
	}// end of constructor

	
	/**
	 * Override the run method from the Thread Class.
	 * 
	 */
	@Override
	public void run() 
	{
		// create a new Stream Connection Notifier
		StreamConnectionNotifier connectionNotifier = null;
		try 
		{
			// register the notifier
			connectionNotifier = (StreamConnectionNotifier) Connector.open(connString);
		} 
		catch (IOException ioe) 
		{
			// throw the error
			sUtils.error( TAG, ioe );
		}
		
		// display the details of the connected host device
		String hostDetails = "\nServer running on:\n" + 
				"PC Address: " + pcDevice.getBluetoothAddress() + "\n" + 
				"PC Name: " + pcDevice.getFriendlyName() + "\n";
		
		sUtils.info( hostDetails );
		
		// create a new Stream Connection
		StreamConnection streamConnection = null;
		try 
		{
			// register the connection
			streamConnection = connectionNotifier.acceptAndOpen();
		} 
		catch (IOException ioe) 
		{
			// throw the error
			sUtils.error( TAG, ioe );
		}
		
		// create a new InputStream
		InputStream dataIn = null;
		try 
		{
			// register the InputStream
			dataIn = new DataInputStream(streamConnection.openInputStream());
		} 
		catch (IOException ioe) 
		{
			// throw the error
			sUtils.error( TAG, ioe );
		}
		
		// create a new Server Communication Thread, using the InputStream
		ServerCommsThread sct = new ServerCommsThread(dataIn);
		// start the thread
		sct.start();
		
	}// end of the run method
	
}// end of Class
