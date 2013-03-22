// package
package itt.t00154755.mouseapp;
// imports
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 
 * @author Christopher Donovan
 * <p>
 * {@link http://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-and-a-desktop/}
 * {@link http://developer.android.com/guide/topics/connectivity/bluetooth.html#EnablingDiscoverability}
 */
public class AppClient
{
	private static final String TAG = "Android Phone";
	BluetoothAdapter btAdapter;
	private boolean available = false;
	String acceloData;
	BluetoothSocket socket;

	public AppClient() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}


	public void connectToServer() {
		try {
			Log.d(TAG, "getting local device");
			// remote MAC here:
			BluetoothDevice device = btAdapter
					.getRemoteDevice("00:15:83:3D:0A:57");
			Log.d(TAG, "connecting to service");
			socket = device.createRfcommSocketToServiceRecord(UUID
					.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
			Log.d(TAG, "about to connect");

			btAdapter.cancelDiscovery();
			socket.connect();
			Log.d(TAG, "Connected!");
			available = true;

		} catch (Exception e) {
			Log.e(TAG, "Error connecting to device", e);
		}
	}

	/**
	 * @param socket
	 * @throws IOException
	 */
	public void writeOutToTheServer(String acceloData) throws IOException {

		ClientCommsThread cct = new ClientCommsThread(socket, acceloData);
		cct.start();

	}

	public boolean isAvailable() {
		return available;
	}

	private class ClientCommsThread extends Thread {
		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket socket;
		private String acceloData;

		public ClientCommsThread(BluetoothSocket socket, String acceloData) {
			System.out.println(TAG);
			this.socket = socket;
			this.acceloData = acceloData;
		}

		@Override
		public void run() {
			try {
				while (true) {
					try {
						socket.getOutputStream().write(acceloData.getBytes());
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
			} catch (Exception e) {
				// print the error stack
				e.printStackTrace();
				e.getCause();

			} finally {
				try {
					if (socket != null) {
						socket.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	
	
	
	/*private final String TAG = "Android Phone";
	private final UUID SPP_UUID = UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a");
	private final String ADDRESS = "00:15:83:3D:0A:57";
	private BluetoothAdapter btAdapter;
	private String acceloData;
	private AppUtils cUtils = new AppUtils();
	private BluetoothSocket btSocket;
	private BluetoothDevice btDevice;

	public AppClient() 
	{
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		cUtils.info(TAG, "in the app client constructor");
	}
	
	public void connectToServer() 
	{
		// MY_UUID is the app's UUID string, also used by the server code

	        	cUtils.info(TAG, "getting local device");
	        	// remote MAC here:
	        	btDevice = btAdapter.getRemoteDevice(ADDRESS);
				
	        	cUtils.info(TAG, "create the RFComm record");
	        	try {
					btSocket = btDevice.createRfcommSocketToServiceRecord(SPP_UUID);
				} catch (IOException ioe){
					Log.e(TAG, ioe.getMessage());
				}
		
	        	// Cancel discovery because it will slow down the connection
	        	cUtils.info(TAG, "cancel discovery");
	        	btAdapter.cancelDiscovery();
	        
	        	cUtils.info(TAG, "app client connect");
	        	try {
					btSocket.connect();
					
					if(btSocket != null)
					{
						// Do work to manage the connection (in a separate thread)
						ClientCommsThread cct = new ClientCommsThread(btSocket, acceloData);
						cct.start();
					}
					
					
				} catch (IOException ioe) {
					// 
					Log.e(TAG, ioe.getMessage());
				}
				
	        	

	        // print the error stack
			//cUtils.error(TAG, "failed to connect to the server ", ioe);
   
	}

	public void getAccelerometerDataString(String acceloData) 
	{
		this.acceloData = acceloData;
	}
	
	private class ClientCommsThread extends Thread 
	{
		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket socket;
		private String acceloData;

		public ClientCommsThread(BluetoothSocket socket, String acceloData) 
		{
			cUtils.info(TAG, "in the constructor..");
			this.socket = socket;
			this.acceloData = acceloData;
		}

		@Override
		public void run() 
		{
			try 
			{
				socket.getOutputStream().write(acceloData.getBytes());
				cUtils.info(TAG, acceloData);	
			} 
			catch (Exception e) 
			{
				// print the error stack
				e.getMessage();
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
					// print the error stack
					Log.e(TAG, e.getMessage());
				}
			}// end of finally
		}// outer try
	}// end of Client Comms Thread
*/}// end of the class