// package

package itt.t00154755.mouseserver;

// imports
import java.io.IOException;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 
 * @author Christopher Donovan
 *         <p>
 *         This class creates a stream connection using the Bluecove API. The stream listens for a RFComm client once a client is found the stream connection is
 *         closed, to ensure that only one client is connected.
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

	public AppServer ( )
	{

		System.out.println("app server constructor");
	}

	@Override public void run()
	{

		createConnection();
	}

	private void createConnection()
	{

		LocalDevice pcDevice = null;

		StreamConnectionNotifier connNotifier = null;
		StreamConnection streamConn;
		try
		{
			pcDevice = LocalDevice.getLocalDevice();
			pcDevice.setDiscoverable(DiscoveryAgent.GIAC);

			UUID uuid = new UUID("04c603b00001000800000805f9b34fb", false);
			String connString = "btspp://localhost:" + uuid + ";name=Java_Server";

			System.out.println(TAG + "...Server Running on : \n ");
			System.out.println("Local Device Name: " + pcDevice.getFriendlyName());
			System.out.println("Local Device MAC: " + pcDevice.getBluetoothAddress());

			connNotifier = (StreamConnectionNotifier ) Connector.open(connString);

		}
		catch ( BluetoothStateException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}
		catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}

		while ( true )
		{
			try
			{
				System.out.println("...waiting for the client...");
				streamConn = connNotifier.acceptAndOpen();
				
				RemoteDevice reDevice;
				try
				{
					reDevice = RemoteDevice.getRemoteDevice(streamConn);

					System.out.println(TAG + "...Server is Connected to: \n" +
					reDevice.getBluetoothAddress() +
					"\n" + reDevice.getFriendlyName(false));
				}
				catch ( IOException e )
				{
					// print the error stack
					e.printStackTrace();
					e.getCause();
					System.exit(-1);
				}
				
				Thread clientThread = new Thread(new ServerCommsThread(streamConn));
				clientThread.start();
			}
			catch ( IOException e )
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.exit(-1);
			}
		}
	}

}// end of Class
