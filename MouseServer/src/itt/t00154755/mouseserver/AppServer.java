// package
package itt.t00154755.mouseserver;

// imports
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 
 * @author Christopher Donovan
 *         <p>
 *         This class creates a stream connection using the Bluecove API. The
 *         stream listens for a RFComm client once a client is found the stream
 *         connection is closed, to ensure that only one client is connected.
 *         <p>
 *         {@link http://docs.oracle.com/javase/tutorial/networking/index.html}
 *         <p>
 *         {@link http
 *         ://docs.oracle.com/javase/tutorial/essential/exceptions/index.html}
 * 
 */
public class AppServer extends Thread 
{
	// string name of class
	private final String TAG = "App Server";
	private final LocalDevice pcDevice;
	private final String connString = "btspp://localhost:"
			+ "27012f0c68af4fbf8dbe6bbaf7aa432a;name=Java Server;"
			+ "authenticate=false;encrypt=false;master=false";

	public AppServer() throws IOException {
		pcDevice = LocalDevice.getLocalDevice();
	}

	public void run() {
		StreamConnectionNotifier connectionNotifier = null;
		try {
			connectionNotifier = (StreamConnectionNotifier) Connector
					.open(connString);
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
		System.out.println(TAG + "...accepting on " + pcDevice.getBluetoothAddress());
		StreamConnection streamConnection = null;
		try {
			streamConnection = connectionNotifier.acceptAndOpen();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
		InputStream dataIn = null;
		try {
			dataIn = new DataInputStream(streamConnection.openInputStream());
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}

		ServerCommsOut sct = new ServerCommsOut(dataIn);
		sct.start();
	}
		
		
	/*private ServerUtils sUtils = new ServerUtils();
	private final UUID SPP_UUID = new UUID("27012f0c68af4fbf8dbe6bbaf7aa432a", false);
	// host device
	private LocalDevice pcDevice;
	// the connection string
	private final String connString = "btspp://localhost:" + SPP_UUID
			+ ";name=Java Server for Mouse App";
	// create a new InputStream
	private InputStream dataIn;

	*//**
	 * AppServer constructor initializes a new server once it is calls.
	 * 
	 *//*
	public AppServer() 
	{
		init();
	}// end of constructor

	public void init()
	{
		try 
		{
			sUtils.info("connected devices");
			pcDevice = LocalDevice.getLocalDevice();
		} 
		catch (BluetoothStateException bse)
		{
			sUtils.error(TAG, bse, 1);
		}
		// create a new Stream Connection Notifier
		StreamConnectionNotifier connectionNotifier = null;

		// register the notifier
		try 
		{
			sUtils.info("open the connector");
			connectionNotifier = (StreamConnectionNotifier) Connector
					.open(connString);
		} 
		catch (IOException e) 
		{
			// 
			e.printStackTrace();
		}

		// display the details of the connected host device
		String hostDetails = "\nServer running on:\n" + "PC Address: "
				+ pcDevice.getBluetoothAddress() + "\n" + "PC Name: "
				+ pcDevice.getFriendlyName() + "\n";

		sUtils.info(hostDetails);

		try 
		{
			sUtils.info("make discovery");
			pcDevice.setDiscoverable(DiscoveryAgent.GIAC);
		} 
		catch (BluetoothStateException e)
		{
			// 
			e.printStackTrace();
		}
		
		// create a new Stream Connection
		StreamConnection streamConnection = null;

		// register the connection
		try 
		{
			sUtils.info("accept and open");
			streamConnection = connectionNotifier.acceptAndOpen();
		}
		catch (IOException e) 
		{
			// 
			e.printStackTrace();
		}
	
		// display the connected phone
		RemoteDevice androidPhone = null;

		try 
		{
			sUtils.info("remote device");
			androidPhone = RemoteDevice.getRemoteDevice(streamConnection);
		} 
		catch (IOException e) 
		{
			// 
			e.printStackTrace();
		}

		sUtils.info("Remote device address: "
				+ androidPhone.getBluetoothAddress());

		try
		{
			sUtils.info("Remote device name: " + androidPhone.getFriendlyName(false));
		} 
		catch (IOException e)
		{
			// 
			e.printStackTrace();
		}
		
		try {
			dataIn = new DataInputStream(streamConnection.openInputStream());
		} catch (IOException e) {
			//
			e.printStackTrace();
		}
	}// end of the run method
	*//**
	 * Override the run method from the Thread Class.
	 * 
	 *//*

	@Override
	public void run()
	{
		while (true)
		{
			passTheDataToTheServer(dataIn);
		}
	}

	private void passTheDataToTheServer(InputStream dataIn) 
	{
		ServerCommsOut serverOut = null;
		serverOut = new ServerCommsOut(dataIn);
		serverOut.start();	
	}*/

}// end of Class
