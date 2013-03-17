package itt.t00154755.mouseserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class AppServer {
	private final LocalDevice pcDevice;
	private final String connString = "btspp://localhost:"
			+ "0000000000000000000000000000ABCD;name=BtExample;"
			+ "authenticate=false;encrypt=false;master=false";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new AppServer().initServer();
	}

	public AppServer() throws IOException {
		pcDevice = LocalDevice.getLocalDevice();
	}

	private void initServer() throws IOException {
		StreamConnectionNotifier connectionNotifier = (StreamConnectionNotifier) Connector
				.open(connString);
		System.out.println("accepting on " + pcDevice.getBluetoothAddress());
		StreamConnection streamConnection = connectionNotifier.acceptAndOpen();
		InputStream dataIn = new DataInputStream(streamConnection.openInputStream());

		ServerCommsThread sct = new ServerCommsThread(dataIn);
		sct.start();

	}
}
