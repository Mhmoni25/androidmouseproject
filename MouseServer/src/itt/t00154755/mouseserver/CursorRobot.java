// packages
package itt.t00154755.mouseserver;
// imports
import java.awt.AWTException;
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
	private AccelerometerObject accobj = new AccelerometerObject();

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

						int startX = 450;
						int startY = 450;

						accobj.setX(startX);
						accobj.setY(startY);

						int x = (int) Math.ceil(accData[0]);
						int y = (int) Math.ceil(accData[1]);
						int z = (int) Math.ceil(accData[2]);

						try 
						{
							while (true) 
							{
								robot = new Robot();
								robot.mouseMove(startX, startY);
								if (x < 0 && y < 0) 
								{
									accobj.setX(startX = -x);
									accobj.setY(startY = -y);
									robot.mouseMove(accobj.getX(),
											accobj.getY());
								} 
								else if (x > 0 && y > 0) 
								{
									accobj.setX(startX = +x);
									accobj.setY(startY = +y);
									robot.mouseMove(accobj.getX(),
											accobj.getY());
								} 
								else if (x > 0 && y < 0) 
								{
									accobj.setX(startX = +x);
									accobj.setY(startY = -y);
									robot.mouseMove(accobj.getX(),
											accobj.getY());
								} 
								else if (x < 0 && y > 0) 
								{
									accobj.setX(startX = -x);
									accobj.setY(startY = +y);
									robot.mouseMove(accobj.getX(),
											accobj.getY());
								}

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
	}// end of MouseMoveThread 
	
}// end of class

