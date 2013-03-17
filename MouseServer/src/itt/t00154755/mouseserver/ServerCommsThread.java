package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;

public class ServerCommsThread extends Thread {
	private static final String TAG = "Server Comms Thread";
	InputStream dataIn;

	public ServerCommsThread(InputStream dataIn) {
		System.out.println(TAG);
		this.dataIn = dataIn;
	}

	public void run() {
		try {
			while (true) {
				try {
					byte[] bytes = new byte[1024];
					int r;
					while ((r = dataIn.read(bytes)) > 0) {
						System.out.println(new String(bytes, 0, r));
					}
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (Exception e) {
			// print the error stack
			e.printStackTrace();
			e.getCause();

		} finally {
			/*
			 * try { if (dataIn != null) { dataIn.close(); }
			 * 
			 * } catch (IOException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } try { if (streamConnection != null) {
			 * streamConnection.close(); } } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); }
			 */
		}

	}
}
