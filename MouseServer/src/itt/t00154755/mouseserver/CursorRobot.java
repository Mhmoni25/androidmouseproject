// packages
package itt.t00154755.mouseserver;
// imports
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;

/**
 * 
 * @author Christopher
 *
 */
public class CursorRobot 
{
	private static final String TAG = "Server Communication Thread";
	private ServerUtils sUtils = new ServerUtils();
	private String acceloData;

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
				sUtils.error( TAG, nfe );
			}
		}
		return data;
	}
	
	/**
	 * 
	 * CursorRobot constructor initializes a new Robot object using the
	 * execute.
	 *  
	 */
	public CursorRobot() 
	{
		execute();
	}

	/*
	 * This method is call on the Class object in order to start the running.
	 * 
	 * e.g. robot.execute()
	 */
	private void execute() 
	{
		new Thread(new MouseMoveThread()).start();
	}

	/*
	 * This thread will allow the data passed from the accelerometer to control
	 * the on screen cursor via the Robot object.
	 */
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
					System.out.println("Waiting for data");
				}
				else 
				{
					float[] accData = covertStringToFloatArray(acceloData);

					if (accData.length == 0 || accData == null) 
					{
						sUtils.info("Waiting for data");
					} 
					else
					{
						Point p = new Point(450, 450);
						p.getLocation();
						int startX = p.x;
						int startY = p.y;

						int x = (int) Math.abs(accData[0]);
						int y = (int) Math.abs(accData[1]);
						int z = (int) Math.abs(accData[2]);
						
						int currentX = findNewX(startX, x);
						int currentY = findNewY(startY, y);		
						
						try 
						{
							while (true) 
							{
								robot = new Robot();
								robot.mouseMove(currentX, currentY);
								
								System.out.println("mouse started at: " + x
										+ " : " + y + " : " + z);
							}// end of inner while loop
						} 
						catch (AWTException eAWT) 
						{
							sUtils.error( TAG, eAWT );
						} 
						catch (Exception e) 
						{
							sUtils.error( TAG, e );
						}
					}// end of run inner else
				}// end of else
			}// end of run while outer loop
		}// end of run method
		private int findNewY(int previousY, int y) {
			return previousY + y;
		}
		private int findNewX(int previousX, int x) {
			return previousX + x;
		}
	}// end of MouseMoveThread
	
}// end of class

