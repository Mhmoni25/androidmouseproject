// package
package itt.t00154755.mouseserver;

// imports
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
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
	private final UUID SPP_UUID = new UUID("1011", true);
	// host device
	private LocalDevice pcDevice;
	// the connection string
	private final String connString = "btspp://localhost:" + SPP_UUID
			+ ";name=Java Server for Mouse App";

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
			sUtils.error( TAG, bse, 1);
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
			
			// display the details of the connected host device
			String hostDetails = "\nServer running on:\n" + 
				"PC Address: " + pcDevice.getBluetoothAddress() + "\n" + 
				"PC Name: " + pcDevice.getFriendlyName() + "\n";
		
			sUtils.info( hostDetails );
			
		} 
		catch (IOException ioe) 
		{
			// throw the error
			sUtils.error( TAG, ioe, 2 );
		}
		
		// create a new Stream Connection
		StreamConnection streamConnection = null;
		try 
		{
			// register the connection
			streamConnection = connectionNotifier.acceptAndOpen();
			
			//display the connected phone
			RemoteDevice androidPhone = null;
		    try {
			androidPhone = RemoteDevice
				.getRemoteDevice(streamConnection);
		    } catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		    }
		    sUtils.info("Remote device address: "
			    + androidPhone.getBluetoothAddress());
		    try {
		    	sUtils.info("Remote device name: "
				+ androidPhone.getFriendlyName(true));
		    } catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		    }
			
		} 
		catch (IOException ioe) 
		{
			// throw the error
			sUtils.error( TAG, ioe, 3 );
		}
		
		// create a new InputStream
		InputStream dataIn = null;
		try 
		{
			// register the InputStream
			dataIn = new DataInputStream(streamConnection.openInputStream());
			
			// create a new Server Communication Thread, using the InputStream
			ServerCommsThread sct = new ServerCommsThread(dataIn);
			// start the thread
			sct.start();
		} 
		catch (IOException ioe) 
		{
			// throw the error
			sUtils.error( TAG, ioe, 4 );
		}
		
	}// end of the run method
	
}// end of Class
