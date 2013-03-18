package itt.t00154755.mouseserver;

import java.awt.AWTException;
import java.awt.Robot;

public class CursorRobot {
	String acceloData;

	public void sendToRobot(String acceloData) {
		this.acceloData = acceloData;
	}

	private float[] covertStringToFloatArray(String acceloData) {

		String delims = "[,]";
		String[] tokens = acceloData.split(delims);
		float[] data = new float[tokens.length];

		for (int i = 0; i < tokens.length; i++) {
			try {
				data[i] = Float.parseFloat(tokens[i]);
			} catch (NumberFormatException nfe) {
			}
		}

		return data;

	}

	public CursorRobot() {
		execute();
	}

	/**
	 * this method is call on the Class object in order to start the running.
	 * 
	 * e.g. robot.execute()
	 */
	public void execute() {
		new Thread(new MouseMoveThread()).start();
	}

	/*
	 * this thread will allow the data passed from the accelerometer to control
	 * the on screen cursor via the Robot object
	 */
	private class MouseMoveThread extends Thread {
		//
		Robot robot;

		@Override
		public void run() {
			while(true){
				if (acceloData.length() == 0 || acceloData == null) {
					System.out.println("Waiting for data");
				} else {
					float[] accData = covertStringToFloatArray(acceloData);
					
					if (accData.length == 0 || accData == null) {
						System.out.println("Waiting for data");
					}else{
						
						int startX = 450;
						int startY = 450;
						int x = (int) Math.ceil(accData[0]);
						int y = (int) Math.ceil(accData[1]);
						int z = (int) Math.ceil(accData[2]);

						try {
							while(true){
							robot = new Robot();
							robot.mouseMove(startX, startY);
							if (x < 0 && y < 0){
								robot.mouseMove(startX - x, startY - y);
							}else if (x > 0 && y > 0){
								robot.mouseMove(startX + x, startY + y);
							}else if (x > 0 && y < 0){
								robot.mouseMove(startX + x, startY - y);
							}else if (x < 0 && y > 0){
								robot.mouseMove(startX - x, startY + y);
							}
							
							System.out.println("mouse started at: " + x
									+ " : " + y + " : " + z);
							}
						} catch (AWTException eAWT) {
							eAWT.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}// end of loop
			}
		}
	}
}// end of class

