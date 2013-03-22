package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;

public class ServerCommsOut extends Thread 
{
	private final String TAG = "Server Communication Thread";
	InputStream dataIn;
	String acceloData;
	CursorRobot cr;

	public ServerCommsOut(InputStream dataIn) 
	{
		System.out.println(TAG);
		this.dataIn = dataIn;
	}

	public void run() 
	{

		Deque<String> queue = new ArrayDeque<String>();

		try 
		{
			while (true)
			{
				try 
				{
					byte[] bytes = new byte[1024];
					int r;
					while ((r = dataIn.read(bytes)) > 0)
					{
						acceloData = (new String(bytes, 0, r));

						synchronized (acceloData)
						{

							cr = new CursorRobot();
							queue.addFirst(acceloData);

							while (!queue.isEmpty()) 
							{
								cr.sendToRobot(queue.removeFirst());
							}

						}
					}

				} 
				catch (IOException e)
				{
					// print the error stack
					e.printStackTrace();
					e.getCause();
					System.exit(-1);
				}
			}
		}
		catch (Exception e) 
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);

		} 
		finally 
		{
			try 
			{
				if (dataIn != null)
				{
					dataIn.close();
				}
			} 
			catch (IOException e) 
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.exit(-1);
			}
		}
	}
}// end of Class