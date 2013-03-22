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

	public void sendToRobot(String acceloData)
	{
		this.acceloData = acceloData;
	}

	private float[] covertStringToFloatArray(String acceloData) 
	{
		String delims = "[,]";
		String[] tokens = acceloData.split(delims);
		float[] data = new float[tokens.length];

		for (int i = 0; i < tokens.length; i++) 
		{
			try 
			{
				data[i] = Float.parseFloat(tokens[i]);
			} 
			catch (NumberFormatException nfe) 
			{
				
			}
		}
		return data;
	}
	
	private int findNewY(int currentY, int y) 
	{
		int	newPos = currentY - (y - 1);
		return newPos;
	}

	private int findNewX(int currentX, int x) 
	{
		int newPos = currentX + x;
		return newPos;
	}

	private int[] covertFloatArrayToIntArray(float[] accData)
	{
		int[] xy = new int[2];
		xy[0] = (int) Math.ceil(accData[0]);
		xy[1] = (int) Math.ceil(accData[1]);

		return xy;
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

	/*
	 * this thread will allow the data passed from the accelerometer to control
	 * the on screen cursor via the Robot object
	 */
	private class MouseMoveThread extends Thread 
	{
		//
		Robot robot;

		@Override
		public void run() 
		{
			while(true)
			{
				if (acceloData.length() == 0 || acceloData == null) 
				{
					System.out.println("Waiting for data");
				} 
				else 
				{
					float[] accData = covertStringToFloatArray(acceloData);

					if (accData.length == 0 || accData == null) 
					{
						System.out.println(TAG + "Waiting for data");
					}
					else
					{
						int[] xyAccData = covertFloatArrayToIntArray(accData);
						int x = xyAccData[0];
						int y = xyAccData[1];
						int moveX = 640;
						int moveY = 400;

						try 
						{
							while(true)
							{
							robot = new Robot();

							moveX = findNewX(moveX, x);
							moveY = findNewY(moveY, y);
							robot.mouseMove(moveX, moveY);
							System.out.println("mouse started at: " + x
									+ " : " + y);
							}
						}
						catch (AWTException eAWT) 
						{
							eAWT.printStackTrace();
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				}// end of loop
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*private ServerUtils sUtils = new ServerUtils();
	private String acceloData;

	public void sendToRobot(String acceloData) 
	{
		this.acceloData = acceloData;
	}

	private float[] covertStringToFloatArray(String acceloData) {
		String delims = "[,]";
		String[] tokens = acceloData.split(delims);
		float[] data = new float[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			try {
				synchronized (data) {
					while (true) {
						data[i] = Float.parseFloat(tokens[i]);
					}
				}
			} catch (NumberFormatException nfe) {
				sUtils.error(TAG, nfe, 1);

			}
		}
		return data;
	}
	
	*//**
	 * 
	 * CursorRobot constructor initializes a new Robot object using the
	 * execute.
	 *  
	 *//*
	public CursorRobot() 
	{
		execute();
	}

	
	 * This method is call on the Class object in order to start the running.
	 * 
	 * e.g. robot.execute()
	 
	private void execute() 
	{
		new Thread(new MouseMoveThread()).start();
	}
	
	private int findNewY(int currentY, int y) {

		int newPos;
		if (y < 0) {
			newPos = currentY - (y * 10);
		} else {
			newPos = currentY + (y * 10);
		}

		return newPos;
	}

	private int findNewX(int currentX, int x) {

		int newPos;
		if (x < 0) {
			newPos = currentX - (x * 10);
		} else {
			newPos = currentX + (x * 10);
		}

		return newPos;
	}

	private int[] covertFloatArrayToIntArray(float[] accData) {
		int[] xy = new int[2];
		xy[0] = (int) Math.ceil(accData[0]);
		xy[1] = (int) Math.ceil(accData[1]);

		return xy;
	}

	
	 * This thread will allow the data passed from the accelerometer to control
	 * the on screen cursor via the Robot object.
	 
	private class MouseMoveThread extends Thread 
	{
		Robot robot;
		@Override
		public void run()
		{
			while (true) 
			{
				if (acceloData.length() == 0 || acceloData == null) 
				{
					System.out.println("acceloData is empty - Robot");
					//do nothing;
				}
				else 
				{
					float[] accData = covertStringToFloatArray(acceloData);
					int[] xyAccData = covertFloatArrayToIntArray(accData);
					int x = xyAccData[0];
					int y = xyAccData[1];
					int moveX = 640;
					int moveY = 400;
					try 
					{
						robot = new Robot();
					} 
					catch (AWTException eAWT) 
					{
						sUtils.error(TAG, eAWT, 2);
					}
					while (true) 
					{
						moveX = findNewX(moveX, x);
						moveY = findNewY(moveY, y);
						robot.mouseMove(moveX, moveY);
							System.out.println("mouse moved: " + moveX + " : " + moveY);
					}
				}
			}
		}// end of run method
	}// end of MouseMoveThread
*/	
}// end of class

