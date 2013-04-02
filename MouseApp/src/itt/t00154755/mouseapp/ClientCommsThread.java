
package itt.t00154755.mouseapp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;

public class ClientCommsThread extends Thread
{

	private static final String TAG = "Client Comms Thread";

	private BluetoothSocket socket;

	private String acceloData;

	public boolean isRunning;

	//private InputStream dataIn;

	public ClientCommsThread ( BluetoothSocket socket, String acceloData )
	{

		System.out.println(TAG);
		this.socket = socket;
		this.acceloData = acceloData;
	}

	@Override public void run()
	{

		//String serverData = null;
		isRunning = true;
		try
		{
			//serverData = readInDataFromTheServer();
			while ( isRunning )
			{
				if ( acceloData.length() < 1 || acceloData == "" || acceloData == null )
				{
					isRunning = false;
				}
				else
				{
					sendDataToTheServer(acceloData);
				}
			}
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace();
		}
		finally
		{
			closeTheSocket();
		}
	}

	/**
	 * @param acceloData
	 * @throws IOException
	 */
	private void sendDataToTheServer( String acceloData ) throws IOException
	{

		OutputStream dataOut = socket.getOutputStream();
		PrintWriter writeOut = new PrintWriter(dataOut);
		writeOut.print(acceloData);
		writeOut.flush();
	}

	
	/*
	 * private class InputThread implements Runnable
	 * {
	 * 
	 * private String readInDataFromTheServer() throws IOException
	 * {
	 * 
	 * dataIn = socket.getInputStream();
	 * BufferedReader buffIn = new BufferedReader(new InputStreamReader(dataIn));
	 * isRunning = true;
	 * String serverData = null;
	 * while ( buffIn.readLine() != null )
	 * {
	 * serverData = buffIn.readLine();
	 * System.out.println("server is: " + serverData);
	 * }
	 * return serverData;
	 * }
	 * 
	 * @Override public void run()
	 * {
	 * 
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * }
	 */
	public void closeTheSocket()
	{

		if ( socket != null )
		{
			try
			{
				socket.close();
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
}
