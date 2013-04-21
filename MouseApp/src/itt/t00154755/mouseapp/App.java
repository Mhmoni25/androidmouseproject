package itt.t00154755.mouseapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Christopher
 * 
 *         This is the main activity and the point at which the user interacts
 *         with the application. The Accelerometer data is read from here and
 *         passed on to the client and from the client to the server.
 * 
 *         ref: BluetoothChat application in the SDK examples
 * 
 */
public class App extends Activity
{

	// used for debugging
	private static final String TAG = "Main App";
	private static final boolean D = true;

	// used to handle messages from the service and the client
	public static final int MESSAGE_STATE_CHANGED = 1;
	public static final int MESSAGE_DEVICE_NAME = 2;
	public static final int MESSAGE_TOAST_ACCELO = 3;
	public static final int MESSAGE_TOAST_CLIENT = 4;
	public static final int MESSAGE_DATA_ACCELO = 5;
	
	// used to signal which mouse option is selected
	// started an 6 - 9 because the direction value
	// is add to the same type of string that counts 1-4
	public static final int MOUSE_MOVE = 6;
	public static final int RIGHT_BUTTON_CLICK = 7;
	public static final int LEFT_BUTTON_CLICK = 8;
	public static final int SCROOL_WHEEL_CLICK = 9;

	// message types
	public static final String DEVICE_NAME = "device";
	public static final String TOAST = "toast";
	public static final String DATA = "data";
	
	// request types
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	// class variables
	private BluetoothAdapter btAdapter;
	private AppClient2 appClient2;
	
	// service variables
	private AccelometerService accService;
	public TextView title;


	/*
	 * use the onCreate() to instantiate the Objects that will be needed
	 * for the activity to start-up.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		if ( D )
			Log.i(TAG, "+++ ON CREATE +++");
		setContentView(R.layout.main);

		setUpApp();

		// ensure that bluetooth isEnabled before continuing
		ensureBluetoothIsEnabled();

		final TextView title = (TextView ) findViewById(R.id.title);
		title.setText(R.string.title);

	}// end of onCreate() method


	/**
	 * 
	 */
	private void ensureBluetoothIsEnabled()
	{
		// check to ensure the device has bluetooth capabilities
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		// if it dosen't finish the app here
		if ( btAdapter == null )
		{
			// toast are short pop-up messages
			Toast.makeText(this, "bluetooth not available on you device....exiting", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}


	// executes immediately after the onCreate()
	@Override
	public void onStart()
	{
		super.onStart();
		if ( D )
			Log.i(TAG, "+++ ON START +++");

		// check to ensure that the bluetooth is turned on
		if ( !btAdapter.isEnabled() )
		{
			// if not start a new activity by request the permission for the user
			Intent requestBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(requestBT, REQUEST_ENABLE_BT);
		}
		else
		{
			// set the service if buletooth isEabled
			// and the service is not already running
			if ( appClient2 == null )
			{
				if ( D )
					Log.i(TAG, "+++ ON START - SET UP THE APPCLIENT +++");
				appClient2 = new AppClient2(appHandler);
				appClient2.start();
			}

		}

	}// end of onStart() method


	// this method is called after the app has been paused
	// if the bluetooth setup is slow and the app is paused
	// this method will be called
	@Override
	public synchronized void onResume()
	{
		super.onResume();
		if ( D )
			Log.i(TAG, "+++ ON RESUME +++");

		// check to see if a service has been started
		if ( appClient2.getState() == AppClient2.WAITING )
		{
			Log.i(TAG, "+++ ON RESUME - START THE APP CLIENT+++");
			appClient2.start();
		}
		if ( appClient2.getState() == AppClient2.CONNECTED )
		{
			Log.i(TAG, "+++ ON RESUME - START THE SERVICE+++");
			makeShortToast("start the accelerometer service");
			AccelometerService accelometerService = new AccelometerService(this.getApplicationContext(), appHandler);
			accelometerService.initAccelometerService();
			
			
		}

	}


	// utility method to make a short toast
	public void makeShortToast( String toast )
	{
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}


	public synchronized void write( String acceloData )
	{
		if(D)
			Log.i(TAG, "App write: " + acceloData);
		
		
		if ( appClient2.getState() != AppClient2.CONNECTED )
		{
			return;
		}
		 // Check that there's actually something to send
        if (acceloData.length() > 0) {
            // Get the message bytes and tell the AppClient2 to write
            byte[] send = acceloData.getBytes();
            appClient2.write(send);
        }
	}


	/**
		 * 
		 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}// end of onCreateOptionsMenu() method


	private void setUpApp()
	{
		if ( D )
			Log.i(TAG, "+++ SET UP APP +++");

		final EditText editText = (EditText ) findViewById(R.id.edText);

		final Button rightbtn = (Button ) findViewById(R.id.bRight);
		rightbtn.setOnClickListener(new View.OnClickListener()
		{

			public void onClick( View v )
			{
				// Perform action on click
				// write right click to server
				if ( D )
					Log.i(TAG, "+++ RIGHT CLICK +++");
				write("" + RIGHT_BUTTON_CLICK);

				makeShortToast("right click");

			}
		});

		final Button leftbtn = (Button ) findViewById(R.id.bLeft);
		leftbtn.setOnClickListener(new View.OnClickListener()
		{

			public void onClick( View v )
			{
				// Perform action on click
				// write left click to server
				if ( D )
					Log.i(TAG, "+++ LEFT CLICK +++");
				write("" + LEFT_BUTTON_CLICK);

				makeShortToast("left click");
			}
		});
		final Button sendbtn = (Button ) findViewById(R.id.sendTextToServer);
		sendbtn.setOnClickListener(new View.OnClickListener()
		{

			public void onClick( View v )
			{
				// Perform action on click
				// write right click to server
				if ( D )
					Log.i(TAG, "+++ SEND TEXT TO SERVER +++");
				if ( editText.getText().length() > 0 )
				{
					write(editText.getText().toString());
				}
				else
				{
					makeShortToast("edit text box is empty, enter text to send");
				}
			}
		});
	}


	/**
	 * This method determines the current activity request and processes
	 * the request.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		switch ( requestCode )
		{
			case REQUEST_CONNECT_DEVICE:
				if ( resultCode == Activity.RESULT_OK )
				{
					Log.i(TAG, "+++ ON ACTIVITY REQUEST - CONNECT +++");
					makeShortToast("connect to device");
					connectToServer(data);

				}
			break;
			case REQUEST_ENABLE_BT:
				if ( resultCode == Activity.RESULT_OK )
				{
					Log.i(TAG, "+++ ON ACTIVITY REQUEST - SETUP +++");
					setUpApp();
				}
				else
				{
					makeShortToast("bluetooth not available");
					finish();
				}
			break;
		}
	}


	private void connectToServer( Intent data )
	{
		// remote MAC:
		Log.i(TAG, "+++ CONNECT TO SERVER - USING THE REMOTE ADDRESS +++");
		String remoteDeviceMacAddress = data.getExtras().getString(CheckBTDevices.EXTRA_DEVICE_ADDRESS);
		// String remoteDeviceMacAddress = "00:15:83:3D:0A:57";

		BluetoothDevice device = btAdapter.getRemoteDevice(remoteDeviceMacAddress);
		// BluetoothDevice device = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
		if ( device != null )
		{
			appClient2.start();
		}

	}


	/**
	 * This method is used to ensure that the current device is discoverable to
	 * others bluetooth device.
	 */

	public void ensureDiscoverable()
	{
		if ( btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE )
		{
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}


	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.connect:
				if ( D )
					Log.e(TAG, "+++ CONNECT +++");
				Intent btSearchIntent = new Intent(this, CheckBTDevices.class);
				startActivityForResult(btSearchIntent, REQUEST_CONNECT_DEVICE);
				return true;

			case R.id.discoverable:
				if ( D )
					Log.e(TAG, "+++ DISCOVERABLE +++");
				ensureDiscoverable();
				return true;
			case R.id.prefs:
				if ( D )
					Log.e(TAG, "+++ PREFS +++");
				Intent btPrefsIntent = new Intent(this, Prefs.class);
				startActivity(btPrefsIntent);
				return true;
		}
		return false;
	}

	/*
	 * this method handles incoming messages from the service
	 * these message include the current state of the service,
	 * or any error messages.
	 */
	private final Handler appHandler = new Handler(Looper.getMainLooper())
	{

		@Override
		public void handleMessage( Message message )
		{
			switch ( message.what )
			{
				case MESSAGE_DATA_ACCELO:
					write(message.getData().getString(DATA));
					if ( D )
						Log.i(TAG, "+++ MESSAGE_DATA +++");
				break;
				case MESSAGE_DEVICE_NAME:
					String connectDeviceName = message.getData().getString(DEVICE_NAME);
					title.setText(connectDeviceName);
					Toast.makeText(getApplicationContext(), "Connected to: " + connectDeviceName, Toast.LENGTH_SHORT).show();
					if ( D )
						Log.i(TAG, "+++ MESSAGE_DEVICE_NAME +++");
				break;
				case MESSAGE_TOAST_ACCELO:
					makeShortToast(message.getData().getString(TOAST));
					if ( D )
						Log.i(TAG, "+++ MESSAGE_TOAST +++");
				break;
				case MESSAGE_TOAST_CLIENT:
					makeShortToast(message.getData().getString(TOAST));
					if ( D )
						Log.i(TAG, "+++ MESSAGE_TOAST +++");
				break;
			}

		}
	};


	// ++++++++++++++++++++++++++++when the program ends clean up here+++++++++++++++++++++++++++++++++++++++

	@Override
	public synchronized void onPause()
	{
		if ( D )
			Log.i(TAG, "+++ ON PAUSE +++");
		super.onPause();
		accService.endAccelometerService();
	}// end of onPause() method


	// if the user stops the app cancel the timer
	// to free up resources and battery life
	@Override
	public void onStop()
	{
		super.onStop();
		if ( D )
			Log.i(TAG, "+++ ON STOP +++");
		accService.endAccelometerService();
	}// end of onStop() method


	// destroy the service
	// called after onStop()
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( D )
			Log.i(TAG, "+++ ON DESTROY +++");
		// stop the Service

	}

}// end of the class
