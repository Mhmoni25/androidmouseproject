// packages

package itt.t00154755.mouseserver;

// imports
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.StringTokenizer;

/**
 * 
 * @author Christopher Donovan
 * 
 */
public class CursorRobot implements Runnable
{

	private static final String TAG = "Cursor Robot Thread";

	private int[] convertedValues;
	private Robot robot;
	private Point startLocation;


	public CursorRobot( String dataIn )
	{
		System.out.print(TAG + "\ncreating the robot");
		initRobot();

		convertedValues = covertStringToIntArray(dataIn);

	}


	private void initRobot()
	{

		try
		{
			robot = new Robot();

			startLocation = MouseInfo.getPointerInfo().getLocation();

		}
		catch ( AWTException eAWT )
		{
			// print the error stack
			System.out.print(TAG + "\n");
			eAWT.printStackTrace();
			eAWT.getCause();
			System.exit(-1);
		}
	}


	@Override
	public void run()
	{

		System.out.println(TAG + "\nmoving the mouse");
		// the force of the movement
		int direction = convertedValues[0];
		int moveX = convertedValues[1];
		int moveY = convertedValues[2];

		// the current location of the cursor
		int startX = startLocation.x;
		int startY = startLocation.y;

		// amount to move = force + current
		int moveToX = moveX + startX;
		int moveToY = moveY + startY;

		while ( true )
		{

			if ( direction == 1 )
			{
				robot.mouseMove(-moveToX, -moveToY);
			}
			else
				if ( direction == 2 )
				{
					robot.mouseMove(moveToX, moveToY);
				}
				else
					if ( direction == 3 )
					{
						robot.mouseMove(-moveToX, moveToY);
					}
					else
						if ( direction == 4 )
						{
							robot.mouseMove(moveToX, -moveToY);
						}

		}
	}


	/**
	 * 
	 * @param acceloData
	 * @return
	 */
	private synchronized int[] covertStringToIntArray( String acceloData )
	{
		// TODO: convert the incoming String to an int[] that will
		StringTokenizer st = new StringTokenizer(acceloData);
		int[] data = new int[acceloData.length()];
		int i = 0;
		while ( st.hasMoreTokens() )
		{
			int val = Integer.parseInt(st.nextToken());
			if ( val >= 0 || val <= 9 )
			{
				data[i] = val;
			}
			i++;
		}

		return data;
	}

}// end of class

