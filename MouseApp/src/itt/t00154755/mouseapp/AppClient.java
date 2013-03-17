package itt.t00154755.mouseapp;

import java.util.UUID;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

public class AppClient {

	private static final String TAG = "Android Phone";
	BluetoothAdapter btAdapter;
	
	public AppClient() {
		connectToSever();
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private void connectToSever() {
		try {
			btAdapter = BluetoothAdapter.getDefaultAdapter();
			
			Log.d(TAG, "getting local device");
			// remote MAC here:
			BluetoothDevice device = btAdapter
					.getRemoteDevice("00:15:83:3D:0A:57");
			Log.d(TAG, "connecting to service");
			BluetoothSocket socket = device
					.createRfcommSocketToServiceRecord(UUID
							.fromString("00000000-0000-0000-0000-00000000ABCD"));
			Log.d(TAG, "about to connect");
			socket.connect();
			Log.d(TAG, "Connected!");
			socket.getOutputStream().write("Hello, world!".getBytes());
		} catch (Exception e) {
			Log.e(TAG, "Error connecting to device", e);
		}
	}

}