// packages

package itt.t00154755.mouseserver;

// imports

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

/**
 * @author Christopher Donovan
 * 
 */
public class CursorRobot extends ServerUtils implements Runnable
{

	private static final String TAG = "Cursor Robot Thread";

	private Robot robot;
	// class variable that gets the screen size of the connected monitor
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit()
													   .getScreenSize();

	//
	private String acceloData;
	private int[] convertedValues;

	private Point currentLocation;
	private Point startLocation;

	public static double width;
	public static double height;
	public static int ctrWidth;
	public static int ctrHeight;

	private int currentX;
	private int currentY;

	// sensor movement direction
	public static final int LEFTDOWN = 1;
	public static final int RIGHTUP = 2;
	public static final int LEFTUP = 3;
	public static final int RIGHTDOWN = 4;

	// used to signal which mouse option is selected
	// started an 6 - 9 because the direction value
	// is add to the same type of string that counts 1-4
	public static final int MOUSE_MOVE = 6;
	public static final int RIGHT_BUTTON_CLICK = 7;
	public static final int LEFT_BUTTON_CLICK = 8;
	public static final int SEND_TEXT_CLICK = 9;


	public CursorRobot( String acceloData )
	{
		initRobot();
		this.acceloData = acceloData;
		convertedValues = convertStringToIntArray(acceloData);
	}


	private void initRobot()
	{
		height = getHeight();
		width = getWidth();
		ctrHeight = getCtrHeight();
		ctrWidth = getCtrWidth();

		try
		{
			robot = new Robot();
			startLocation = new Point(ctrHeight, ctrWidth);
		}
		catch ( AWTException eAWT )
		{
			// print the error stack
			System.out.print(TAG + "\n");
			eAWT.printStackTrace();
			eAWT.getCause();
			System.exit(-1);
			// reset the robot object
			robot = null;
		}

		System.out.println("start position: x=" + startLocation.x
						   + ", y="
						   + startLocation.y);
	}


	@Override
	public void run()
	{
		int state = convertedValues[0];

		if ( state > 0 && state < 5 )
		{
			// move the mouse if the state is between 1 and 4 inclusive
			moveCursor();
		}
		else
		{
			// mouse click
			moveClicked(state);
		}

	}


	private synchronized void moveClicked( int state )
	{
		switch ( state )
		{
			case SEND_TEXT_CLICK:
				robot.keyPress(KeyEvent.VK_ENTER);
				System.out.println(acceloData.subSequence(1,
														  acceloData.length() - 1));
				robot.keyRelease(KeyEvent.VK_ENTER);
				break;
			case LEFT_BUTTON_CLICK:
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				System.out.println("left click");
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case RIGHT_BUTTON_CLICK:
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				System.out.println("right click");
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				break;
		}
	}


	/**
	 * @throws HeadlessException
	 */
	private synchronized void moveCursor() throws HeadlessException
	{

		System.out.println(TAG + "\nmoving the mouse");
		// the direction of the movement
		int direction = convertedValues[0];
		int x = convertedValues[1];
		int y = convertedValues[2];

		switch ( direction )
		{
			case LEFTDOWN:
				moveLeftDown(x, y);
				break;
			case RIGHTUP:
				moveRightUp(x, y);
				break;
			case LEFTUP:
				moveLeftUp(x, y);
				break;
			case RIGHTDOWN:
				moveRightDown(x, y);
				break;
		}
	}


	private void moveRightDown( int x, int y )
	{
		int currentX = startLocation.x + getCurrentX();
		int currentY = startLocation.x + getCurrentY();

		robot.mouseMove(currentX + x, currentY - y);
		System.out.println("moveRightDown" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentX(MouseInfo.getPointerInfo().getLocation().y);
	}


	private void moveLeftUp( int x, int y )
	{
		int currentX = startLocation.x + getCurrentX();
		int currentY = startLocation.x + getCurrentY();
		robot.mouseMove(currentX - x, currentY + y);
		System.out.println("moveRightDown" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentX(MouseInfo.getPointerInfo().getLocation().y);
	}


	private void moveRightUp( int x, int y )
	{
		int currentX = startLocation.x + getCurrentX();
		int currentY = startLocation.x + getCurrentY();
		robot.mouseMove(currentX + x, currentY + y);
		System.out.println("moveRightDown" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentX(MouseInfo.getPointerInfo().getLocation().y);
	}


	private void moveLeftDown( int x, int y )
	{
		int currentX = startLocation.x + getCurrentX();
		int currentY = startLocation.x + getCurrentY();
		robot.mouseMove(currentX - x, currentY - y);
		System.out.println("moveRightDown" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentX(MouseInfo.getPointerInfo().getLocation().y);
	}


	/**
	 * 
	 * @param acceloData
	 * @return
	 */
	private synchronized int[] convertStringToIntArray( String acceloData )
	{
		// TODO: convert the incoming String to an int[] that will
		StringTokenizer st = new StringTokenizer(acceloData);
		int[] data = new int[acceloData.length()];
		int i = 0;
		while ( st.hasMoreTokens() )
		{
			int val = Integer.parseInt(st.nextToken());
			if ( val >= 0 && val <= 9 )
			{
				data[i] = val;
			}
			i++;
		}

		return data;
	}


	/**
	 * @return the currentLocation
	 */
	public Point getCurrentLocation()
	{
		return currentLocation;
	}


	/**
	 * @param currentLocation
	 *        the currentLocation to set
	 */
	public void setCurrentLocation( Point currentLocation )
	{

		this.currentLocation = currentLocation;
	}


	/**
	 * @return the width
	 */
	private double getWidth()
	{
		return SCREENSIZE.width;
	}


	/**
	 * @return the height
	 */
	private double getHeight()
	{
		return SCREENSIZE.height;
	}


	/**
	 * @return the ctrWidth
	 */
	private int getCtrWidth()
	{
		return SCREENSIZE.width / 2;
	}


	/**
	 * @return the ctrHeight
	 */
	private int getCtrHeight()
	{
		return SCREENSIZE.height / 2;
	}


	/**
	 * @return the currentX
	 */
	public int getCurrentX()
	{
		return currentX;
	}


	/**
	 * @param currentX
	 *        the currentX to set
	 */
	public void setCurrentX( int currentX )
	{
		this.currentX = currentX;
	}


	/**
	 * @return the currentY
	 */
	public int getCurrentY()
	{
		return currentY;
	}


	/**
	 * @param currentY
	 *        the currentY to set
	 */
	public void setCurrentY( int currentY )
	{
		this.currentY = currentY;
	}

}// end of class

