
package itt.t00154755.mouseserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.microedition.io.StreamConnection;

public class ServerCommsThread implements Runnable
{

	private final String TAG = "Server Communication Thread";

	private StreamConnection streamConn; // client

	public ServerCommsThread ( StreamConnection streamConn )
	{

		System.out.println(TAG + " -constructor");
		this.streamConn = streamConn;
	}

	@Override 
	public void run()
	{

		InputStream streamIn = null;
		try
		{
			streamIn = streamConn.openInputStream();
		}
		catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}
		BufferedReader buffIn = new BufferedReader(new InputStreamReader(streamIn));

		// keep reading
		while ( true )
		{
			try
			{
				String dataIn = buffIn.readLine();

				sendDataToCursorRobot(dataIn);
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

	private void sendDataToCursorRobot( String dataIn )
	{

		System.out.println(TAG + " -sending the data");
		System.out.println(TAG + " " + dataIn);
		CursorRobot cr = new CursorRobot();
		cr.dataFromServer(dataIn);
	}

}// end of Class