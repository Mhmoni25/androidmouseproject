// package
package itt.t00154755.mouseapp;
// imports
import java.io.IOException;
import java.util.UUID;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

/**
 * 
 * @author Christopher Donovan
 * <p>
 * {@link http://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-and-a-desktop/}
 */
public class AppClient {

	private static final String TAG = "Android Phone";
	BluetoothAdapter btAdapter;
	private boolean available = false;
	String acceloData;
	BluetoothSocket socket;

	public AppClient() 
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	public void connectToSever() 
	{
		try 
		{
			Log.d(TAG, "getting local device");
			// remote MAC here:
			BluetoothDevice device = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
			Log.d(TAG, "connecting to service");
			socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00000000-0000-0000-0000-00000000ABCD"));
			Log.d(TAG, "about to connect");

			btAdapter.cancelDiscovery();
			socket.connect();
			Log.d(TAG, "Connected!");
			available = true;

		} 
		catch (Exception e) 
		{
			Log.e(TAG, "Error connecting to device", e);
		}
	}

	/**
	 * @param socket
	 * @throws IOException
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void writeOutToTheServer(String acceloData) throws IOException 
	{
		ClientCommsThread cct = new ClientCommsThread(socket, acceloData);
		cct.start();
	}

	public boolean isAvailable() 
	{
		return available;
	}

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
					try 
					{
						socket.getOutputStream().write(acceloData.getBytes());
					} 
					catch (IOException e) 
					{
						System.err.println(e.getMessage());
					}
				}
			} 
			catch (Exception e) 
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();

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
					e.printStackTrace();
				}
			}
		}
	}
}