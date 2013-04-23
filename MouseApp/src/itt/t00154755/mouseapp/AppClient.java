package itt.t00154755.mouseapp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Christopher
 * 
 */
public class AppClient
{

	private static final String TAG = "App Client";
	private static final boolean D = true;

	public static final int WAITING = 0;
	public static final int CONNECTED = 1;
	// private final int RUNNING = 2;

	private BluetoothAdapter btAdapter;
	private ClientCommsThread clientCommThread;
	private int state;
	private Handler accHandler;


	public AppClient( Handler accHandler )
	{
		// get the default adapter
		if ( D )
			Log.d(TAG, "getting default adapter");
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		state = WAITING;

		this.accHandler = accHandler;
	}


	public void start()
	{
		connectToSever();
	}


	/**
	 * this method connects to the server it opens an RFCOMM serial port connection
	 * over TCP this ensure that the data
	 */
	private void connectToSever()
	{
		BluetoothDevice btDevice = null;
		BluetoothSocket btSocket = null;
		if ( D )
			Log.d(TAG, "getting local device");
		btDevice = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");

		btAdapter.cancelDiscovery();
		if ( D )
			Log.d(TAG, "connecting to server");
		try
		{
			btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
		}
		catch ( IOException e )
		{
			if ( D )
				Log.e(TAG, "error creating the RFCOMM connection");
		}
		if ( D )
			Log.d(TAG, "about to connect");
		while ( state == WAITING )
		{
			try
			{
				btSocket.connect();
			}
			catch ( IOException e1 )
			{
				//
				e1.printStackTrace();
			}

			if ( D )
				Log.d(TAG, "Connected!");

			sendConnectedToastToUIHandler();

			setState(CONNECTED);
		}
		try
		{
			// String name = btDevice.getName();
			// sendDeviceNameToUIHandler(name);
			createClientCommThread(btSocket);
		}
		catch ( IOException e )
		{
			if ( D )
				Log.e(TAG, "error creating the client comms thread");
		}

	}


	/**
	 * sends a toast back to the UI Handler once the connection is made
	 */
	private void sendConnectedToastToUIHandler()
	{
		// message back to UI
		Message message = accHandler.obtainMessage(App.MESSAGE_TOAST_CLIENT);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "The device is now connected to the server");
		message.setData(bundle);
		accHandler.handleMessage(message);
	}


	/*	*//**
	 * @param name
	 *        the friendly name of the connected blue-tooth device
	 */
	/*
	 * private void sendDeviceNameToUIHandler( String name )
	 * {
	 * // message back to UI
	 * Message message = accHandler.obtainMessage(App.MESSAGE_DEVICE_NAME);
	 * Bundle bundle = new Bundle();
	 * bundle.putString(App.DEVICE_NAME, name);
	 * message.setData(bundle);
	 * accHandler.handleMessage(message);
	 * }
	 */

	/**
	 * @return the state
	 *         the current state of the connection process
	 */
	public synchronized int getState()
	{
		return state;
	}


	/**
	 * @param state
	 *        the state to set
	 */
	public synchronized void setState( int stateIn )
	{
		if ( D )
			Log.i(TAG, "state : " + state + " => to: " + stateIn);
		state = stateIn;
	}


	/**
	 * @param btSocket
	 *        the currently open blue-tooth socket
	 * @throws IOException
	 *         if the socket is not open
	 */
	public synchronized void
			createClientCommThread( BluetoothSocket socket ) throws IOException
	{
		clientCommThread = new ClientCommsThread(socket);
		clientCommThread.start();
	}


	/**
	 * @param acceloData
	 *        a byte[] representation of the accelerometer data reading
	 */
	public synchronized void write( byte[] acceloData )
	{
		if ( D )
			Log.i(TAG, "Pass the data to the thread: " + acceloData.toString());
		// Create temporary object
		ClientCommsThread cct;
		// Synchronize a copy of the ConnectedThread
		synchronized ( this )
		{
			if ( state != CONNECTED )
				return;
			cct = clientCommThread;
		}
		// Perform the write unsynchronized
		cct.writeToAppClient(acceloData);

	}


	/**
	 * @param socket
	 *        cancel the currently running thread
	 */
	public void cancel( BluetoothSocket socket )
	{
		if ( socket != null )
		{
			try
			{
				socket.close();
				socket = null;
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}

		if ( clientCommThread != null )
		{
			clientCommThread.cancel();
			clientCommThread = null;
		}
		setState(WAITING);
	}


	/**
	 * @param socket
	 *        cancel the currently running thread
	 */
	public void closeClient()
	{
		if ( clientCommThread != null )
		{
			// clientCommThread.cancel();
			clientCommThread = null;
		}
		setState(WAITING);
	}

	/**
	 * @author Christopher
	 * 
	 */
	private class ClientCommsThread extends Thread
	{

		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket btSocket = null;
		private final OutputStream outStream;


		/*
		 * 
		 */
		public ClientCommsThread( BluetoothSocket btSocket )
		{
			OutputStream tmpOut = null;
			if ( D )
				Log.i(TAG, "+++ SET UP THE CLIENT COMM THREAD +++");
			try
			{
				tmpOut = btSocket.getOutputStream();
			}
			catch ( IOException e )
			{
				if ( D )
					Log.e(TAG, "error trying to open the output stream");
			}
			outStream = tmpOut;
		}


		/**
		 * @param acceloData
		 */
		public void writeToAppClient( byte[] acceloData )
		{
			try
			{
				outStream.write(acceloData);
			}
			catch ( IOException e )
			{
				if ( D )
					Log.e(TAG, "error while writing to the server");
				try
				{
					outStream.close();
				}
				catch ( IOException e1 )
				{
					if ( D )
						Log.e(TAG, "error trying to close the outstream");
				}
			}
			catch ( Exception nullP )
			{
				if ( D )
					Log.e(TAG, "error trying to write to the server");
			}
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{

		}


		/**
		 * close the btSocket
		 */
		public void cancel()
		{
			try
			{
				btSocket.close();
				btSocket = null;
			}
			catch ( IOException e )
			{
				if ( D )
					Log.e(TAG, "Cancelling the Client Comm Thread");
				if ( D )
					Log.e(TAG, "error closing the socket");
			}
		}
	}

}
