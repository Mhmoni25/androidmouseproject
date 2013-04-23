package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;

public class ServerCommsThread implements Runnable
{

	private final String TAG = "Server Communication Thread";
	private InputStream in = null;


	public ServerCommsThread( InputStream inStream )
	{
		System.out.println(TAG + "...constructor");
		in = inStream;
	}


	@Override
	public void run()
	{
		System.out.println(TAG + " in the run() ");

		byte[] bytes = new byte[1024];
		int r;
		try
		{
			while ( ( r = in.read(bytes) ) > 0 )
			{
				String acceloData = ( new String(bytes, 0, r) );
				System.out.println(TAG + acceloData);

				Thread sendDataThread = new Thread(new CursorRobot(acceloData)); // Thread created
				sendDataThread.start(); // Thread started
			}
		}
		catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();

			try
			{
				in.close();
			}
			catch ( IOException ex )
			{
				System.out.println("error closing the input stream");
			}
			System.out.println(TAG + " - shutting down the server 1");
			System.exit(-1);
		}
		finally
		{
			try
			{
				in.close();
			}
			catch ( IOException e )
			{
				System.out.println("error closing the input stream");
			}
			System.out.println(TAG + " - shutting down the server - finally");
			System.exit(-1);
		}
	}

}// end of Class
