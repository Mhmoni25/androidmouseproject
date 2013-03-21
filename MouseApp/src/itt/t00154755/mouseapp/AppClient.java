// package
package itt.t00154755.mouseapp;
// imports
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * 
 * @author Christopher Donovan
 * <p>
 * {@link http://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-and-a-desktop/}
 * {@link http://developer.android.com/guide/topics/connectivity/bluetooth.html#EnablingDiscoverability}
 */
public class AppClient
{
	private final String TAG = "Android Phone";
	//private final int REQUEST_ENABLE_BT = 1; // constant from the Bluetooth API
	private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private final String ADDRESS = "00:15:83:3D:0A:57";
	private BluetoothAdapter btAdapter;
	private String acceloData;
	private AppUtils cUtils = new AppUtils();

	public AppClient() 
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		connectToServer();
	}
	
	public void connectToServer() 
	{
		try 
		{
			cUtils.info(TAG, "getting local device");
			// remote MAC here:
			BluetoothDevice device = btAdapter.getRemoteDevice(ADDRESS);
			
			getAccelerometerDataString(acceloData);
			
			ConnectionThread ct = new ConnectionThread(device, acceloData);
			ct.start();
		} 
		catch (Exception e) 
		{
			cUtils.error(TAG, "Error connecting to device", e);
		}
	}

	public void getAccelerometerDataString(String acceloData) 
	{
		this.acceloData = acceloData;
	}
	
//~~~~~~~~~~~~~~~~Start of the ConnectionThread~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/*
	 * 
	 *
	 */
	private class ConnectionThread extends Thread 
	{
	    private final BluetoothSocket btSocket;
	    private final BluetoothDevice btDevice;
	    private String acceloData;
	 
	    public ConnectionThread(BluetoothDevice device, String acceloData) 
	    {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        btDevice = device;
	        this.acceloData = acceloData;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	        try 
	        {
	            // MY_UUID is the app's UUID string, also used by the server code
	            tmp = btDevice.createRfcommSocketToServiceRecord(SPP_UUID);
	        } 
	        catch (IOException e) 
	        {
	        	cUtils.error(TAG, "failed to sreate a RFCOMM service record", e);
	        }
	        btSocket = tmp;
	    }
	 
	    public void run() 
	    {
	        // Cancel discovery because it will slow down the connection
	        btAdapter.cancelDiscovery();
	 
	        try 
	        {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            btSocket.connect();
	        } 
	        catch (IOException connectException) 
	        {
	            // Unable to connect; close the socket and get out
	            try
	            {
	                btSocket.close();
	            } 
	            catch (IOException closeException) 
	            { 
	            	cancel();
	            }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        ClientCommsThread cct = new ClientCommsThread(btSocket, acceloData);
	        cct.start();
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() 
	    {
	        try 
	        {
	            btSocket.close();
	        } 
	        catch (IOException e) 
	        { 
	        	cUtils.error(TAG, "failed to close the socket ", e);
	        }
	    }
	}// end of Connection Thread

	
//~~~~~~~~~~~~~~~~Start of the ClientCommsThread~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/*
	*
	*
	*/
	private class ClientCommsThread extends Thread 
	{
		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket socket;
		private String acceloData;

		public ClientCommsThread(BluetoothSocket socket, String acceloData) 
		{
			System.out.println(TAG);
			this.socket = socket;
			this.acceloData = acceloData;
		}

		@Override
		public void run() 
		{
			try 
			{
				while (true) 
				{
					socket.getOutputStream().write(acceloData.getBytes());
				}
			} 
			catch (Exception e) 
			{
				// print the error stack
				cUtils.error(TAG, "failed to write to the server ", e);
			} 
			finally 
			{
				try 
				{
					if (socket != null) 
					{
						socket.close();
					}
				} 
				catch (IOException e) 
				{
					cUtils.error(TAG, "failed to close the socket ", e);
				}
			}
		}
	}// end of Client Comms Thread
}