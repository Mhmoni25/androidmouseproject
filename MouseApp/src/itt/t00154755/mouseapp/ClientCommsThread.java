package itt.t00154755.mouseapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;

public class ClientCommsThread extends Thread
{
	private static final String	TAG	= "Client Comms Thread";
	private BluetoothSocket		socket;
	private String				acceloData;
	private boolean				isRunning;
	private InputStream			dataIn;

	public ClientCommsThread ( BluetoothSocket socket, String acceloData )
	{
		System.out.println(TAG);
		this.socket = socket;
		this.acceloData = acceloData;
	}

	@Override
	public void run( )
	{
		String serverData = null;
		try
		{
			serverData = readInDataFromTheServer();
			while (serverData == null && isRunning)
			{
				if (acceloData.length() < 1 || acceloData == "" || acceloData == null)
				{
					isRunning = false;
				}else
				{
					sendDataToTheServer(acceloData);
				}
			}
		} catch ( IOException e1 )
		{
			e1.printStackTrace();
		}finally
		{
			closeTheSocket();
		}
	}

	/**
	 * @param acceloData 
	 * @throws IOException
	 */
	private void sendDataToTheServer(String acceloData ) throws IOException
	{
		
		OutputStream dataOut = socket.getOutputStream();
		PrintWriter writeOut = new PrintWriter(dataOut);
		writeOut.print(acceloData);
		writeOut.flush();
	}

	private String readInDataFromTheServer( ) throws IOException
	{
		dataIn = socket.getInputStream();
		BufferedReader buffIn = new BufferedReader(
				new InputStreamReader(dataIn));
		isRunning = true;
		String serverData = null;
		while ( (serverData = buffIn.readLine()) != null  )
		{
			System.out.println("server is: " + serverData);
		}
		return serverData;
	}

	public void closeTheSocket( )
	{
		if ( socket != null )
		{
			try
			{
				socket.close();
			} catch ( IOException e )
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.exit(-1);
			}
		}
	}
}