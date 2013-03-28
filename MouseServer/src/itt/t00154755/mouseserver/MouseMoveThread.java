package itt.t00154755.mouseserver;

import java.awt.AWTException;
import java.awt.Robot;

/*
 * this thread will allow the data passed from the accelerometer to control
 * the on screen cursor via the Robot object
 */
public class MouseMoveThread extends Thread 
{
	
	private int startX, startY;
	private String acceloData;
	
	public MouseMoveThread(int startX, int startY, String acceloData) {
		this.startX =startX;
		this.startY = startY;
		this.acceloData = acceloData;
	}
	
	//
	Robot robot;

	@Override
	public void run() 
	{
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if (acceloData.length() == 0 || acceloData == "")
		{
			System.out.println("Waiting for data");
		} 
		else
		{
			int[] accData = covertStringToIntArray(acceloData);
			int x = accData[0];
			int y = accData[1];
			

			if (accData.length == 0 || accData == null) 
			{
				System.out.println(TAG + "Waiting for data");
			} 
			else 
			{
				moveX += x;
				moveY += y;

				try 
				{
					Thread.sleep(100);
					robot.mouseMove(moveX, moveY);

					System.out.println("mouse started at: " + x
						+ " : " + y);
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}// end of loop
	}
}
