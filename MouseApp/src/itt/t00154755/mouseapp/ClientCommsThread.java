package itt.t00154755.mouseapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class ClientCommsThread {
	private static final String TAG = "Client Comms Thread";
	private BluetoothSocket socket;
	private String acceloData;

	public ClientCommsThread(BluetoothSocket socket, String acceloData) {
		System.out.println(TAG);
		this.socket = socket;
		this.acceloData =acceloData;
	}
	
	public void run() {

		OutputStream dataOut = null;
		try {
			dataOut = new DataOutputStream(socket.getOutputStream());
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
				if (dataOut != null) {
					dataOut.close();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
