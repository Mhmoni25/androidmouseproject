// packages
package itt.t00154755.mouseserver;

// imports
import java.awt.AWTException;
import java.awt.Robot;

/**
 * 
 * @author Christopher Donovan
 * 
 */
public class CursorRobot 
{
	private static final String TAG = "Server Communication Thread";
	String acceloData;
	int moveX = 640;
	int moveY = 400;

	public void sendToRobot(String acceloData)
	{
		this.acceloData = acceloData;
	}

	private synchronized int[] covertStringToIntArray(String acceloData) 
	{
		String delims = "[,]";
		String[] tokens = acceloData.split(delims);
		int[] data = new int[tokens.length];

		for (int i = 0; i < tokens.length; i++) 
		{
			try
			{
				data[i] = Integer.parseInt(tokens[i]);
			} 
			catch (NumberFormatException nfe) 
			{
				// print the error stack
				nfe.printStackTrace();
				nfe.getCause();
				System.exit(-1);
			}
		}
		notify();
		return data;
	}

	public CursorRobot() 
	{
		execute();
	}

	/**
	 * this method is call on the Class object in order to start the running.
	 * 
	 * e.g. robot.execute()
	 */
	public void execute() 
	{
		new Thread(new MouseMoveThread()).start();
	}

	
}// end of class

