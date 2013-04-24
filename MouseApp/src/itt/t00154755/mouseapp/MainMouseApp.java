package itt.t00154755.mouseapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
public class MainMouseApp extends Activity
{

	// used for debugging
	private static final String TAG = "Main MainMouseApp";
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
	public static final int SEND_DATA_CLICK = 9;

	// mouse colors
	public static final int WHITE = 11;
	public static final int GREEN = 12;
	public static final int RED = 13;
	public static final int BLUE = 14;

	// message types
	public static final String DEVICE_NAME = "btDevice";
	public static final String TOAST = "toast";
	public static final String DATA = "data";

	// request types
	public static final int REQUEST_CONNECT_DEVICE = 1;
	public static final int REQUEST_ENABLE_BT = 2;

	// class variables
	private BluetoothAdapter btAdapter;
	private AppClient appClient;

	// service variables
	private AccelometerService appService;
	public WindowManager appWindow;
	public Display appDisplay;

	// display variables
	public TextView title;
	public TextView xAxis;
	public TextView yAxis;
	public TextView xReadings;
	public TextView yReadings;


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
		appWindow = (WindowManager ) getSystemService(WINDOW_SERVICE);
		appDisplay = appWindow.getDefaultDisplay();

		// ensure that blue-tooth isEnabled before continuing
		ensureBluetoothIsEnabled();

		// set the app display after check for blue-tooth
		setUpApp();

		final TextView title = (TextView ) findViewById(R.id.title);
		title.setText(getUserName());

	}// end of onCreate() method


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
			// set up the client only if blue-tooth isEabled
			// and the client is not already running
			if ( appClient == null )
			{
				if ( D )
					Log.i(TAG, "+++ ON START - SET UP THE APPCLIENT +++");
				appClient = new AppClient(this, appHandler);
				appClient.start();
			}
		}

		setUpAccelerometerService();

	}// end of onStart() method


	// this method is called after the app has been paused
	// if the blue-tooth setup is slow and the app is paused
	// this method will be called
	@Override
	public synchronized void onResume()
	{
		super.onResume();
		if ( D )
			Log.i(TAG, "+++ ON RESUME +++");

		if ( appClient.getState() == AppClient.WAITING )
		{
			appClient.start();
		}
		// if the client is connected start the service
		// need to wait for the client to be connected
		// before starting the service
		if ( appClient.getState() == AppClient.CONNECTED )
		{
			setUpAccelerometerService();
		}

		if ( getMouseColor() != WHITE )
		{
			// appClient.write(getMouseColor());
		}
	}


	/**
	 * 
	 */
	private void setUpAccelerometerService()
	{
		Log.i(TAG, "+++ START THE SERVICE+++");
		makeShortToast("start the accelerometer service");
		// set up a new service
		AccelometerService accelometerService = new AccelometerService(this.getApplicationContext(),
																	   appHandler,
																	   appWindow);
		// start the service
		accelometerService.initAccelometerService();
	}


	/**
	 * Method used to display the Options Menu
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}// end of onCreateOptionsMenu() method


	/**
	 * Method used to handle which menu item is selected.
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.connect:
				if ( D )
					Log.e(TAG, "+++ CONNECT OPTION +++");
				Intent btSearchIntent = new Intent(this, CheckBTDevices.class);
				startActivityForResult(btSearchIntent, REQUEST_CONNECT_DEVICE);
				return true;
			case R.id.discoverable:
				if ( D )
					Log.e(TAG, "+++ DISCOVERABLE OPTION +++");
				ensureDiscoverable();
				return true;
			case R.id.prefs:
				if ( D )
					Log.e(TAG, "+++ PREFS OPTION +++");
				Intent btPrefsIntent = new Intent(this, Prefs.class);
				startActivity(btPrefsIntent);
				return true;
			case R.id.exit:
				if ( D )
					Log.e(TAG, "+++ EXIT OPTION +++");
				if ( appService != null )
				{
					appService.endAccelometerService();
				}
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}


	public String getUserName()
	{
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String username = getPrefs.getString("username", "name not set");
		if ( username != null )
		{
			return username;
		}
		return username;
	}


	/**
	 * 
	 */
	private int getMouseColor()
	{
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		int mouseColor = 0;
		String colours = getPrefs.getString("colorlist", "1");
		if ( colours.contains("2") )
		{
			mouseColor = GREEN;
		}
		else
			if ( colours.contains("3") )
			{
				mouseColor = RED;
			}
			else
				if ( colours.contains("4") )
				{
					mouseColor = BLUE;
				}
				else
				{
					mouseColor = WHITE;
				}
		return mouseColor;
	}


	/*
	 * This method ensures that blue-tooth isEnabled on the btDevice
	 */
	private void ensureBluetoothIsEnabled()
	{
		// check to ensure the btDevice has blue-tooth capabilities
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		// if it dosen't finish the app here
		if ( btAdapter == null )
		{
			// toast are short pop-up messages
			Toast.makeText(this,
						   "bluetooth not available on you btDevice....exiting",
						   Toast.LENGTH_SHORT)
				 .show();
			finish();
			return;
		}
	}


	// utility method to make a short toast
	public void makeShortToast( String toast )
	{
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}


	/**
	 * 
	 * @param acceloData
	 */
	public synchronized void write( String acceloData )
	{
		if ( D )
			Log.i(TAG, "MainMouseApp write: " + acceloData);

		if ( appClient.getState() != AppClient.CONNECTED )
		{
			return;
		}
		// Check that there's actually something to send
		if ( acceloData.length() > 0 )
		{
			// Get the message bytes and tell the AppClient to write
			byte[] send = acceloData.getBytes();
			appClient.write(send);
		}
	}


	/*
	 * this method place the button and text box on the screen
	 * also registers the button click listeners
	 */
	private void setUpApp()
	{
		if ( D )
			Log.i(TAG, "+++ SET UP APP +++");

		final EditText editText = (EditText ) findViewById(R.id.edText);
		final TextView xAxis = (TextView ) findViewById(R.id.tvx);
		final TextView yAxis = (TextView ) findViewById(R.id.tvy);
		final TextView xReadings = (TextView ) findViewById(R.id.tvXReadings);
		final TextView yReadings = (TextView ) findViewById(R.id.tvYReadings);

		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean turnOnReadings = getPrefs.getBoolean("showdata", false);
		if ( turnOnReadings == true )
		{
			xAxis.setVisibility(View.VISIBLE);
			yAxis.setVisibility(View.VISIBLE);
			xReadings.setVisibility(View.VISIBLE);
			yReadings.setVisibility(View.VISIBLE);
		}

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
					makeShortToast("text sent");
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
	 *        the type of request to be carried out
	 * @param resultCode
	 *        did the request passed or fail
	 * @param data
	 *        the data returned from the request in the Intent
	 */
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		switch ( requestCode )
		{
			case REQUEST_CONNECT_DEVICE:
				if ( resultCode == Activity.RESULT_OK )
				{
					Log.i(TAG, "+++ ON ACTIVITY REQUEST - CONNECT +++");
					makeShortToast("connect to btDevice");
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
		if ( D )
			Log.i(TAG, "+++ CONNECT TO SERVER - USING THE REMOTE ADDRESS +++");
		// String remoteDeviceMacAddress = data.getExtras()
		// .getString(CheckBTDevices.EXTRA_DEVICE_ADDRESS);
		// String remoteDeviceMacAddress = "00:15:83:3D:0A:57";

		// BluetoothDevice btDevice = btAdapter.getRemoteDevice(remoteDeviceMacAddress);
		BluetoothDevice btDevice = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
		if ( btDevice != null )
		{
			appClient.start();
		}

	}


	/**
	 * This method is used to ensure that the current btDevice is discoverable to
	 * others blue-tooth btDevice.
	 */
	public void ensureDiscoverable()
	{
		if ( btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE )
		{
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
										300);
			startActivity(discoverableIntent);
		}
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

					String dataIn = message.getData().getString(DATA);
					write(dataIn);
					// setDataIn(dataIn);
					if ( D )
						Log.i(TAG, "+++ MESSAGE_DATA +++");
					break;
				case MESSAGE_DEVICE_NAME:
					String connectDeviceName = message.getData()
													  .getString(DEVICE_NAME);
					title.setText(connectDeviceName);
					Toast.makeText(getApplicationContext(),
								   "Connected to: " + connectDeviceName,
								   Toast.LENGTH_SHORT).show();
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

		/*
		 * private void setDataIn( String datIn )
		 * {
		 * if ( dataIn != null )
		 * {
		 * xReadings.setText(dataIn.substring(1, 3));
		 * yReadings.setText(dataIn.substring(3, 5));
		 * }
		 * 
		 * }
		 */
	};


	// ++++++++++++++++++++++++++++ When the program ends - Clean up here +++++++++++++++++++++++++++++++++++++++

	@Override
	public synchronized void onPause()
	{
		super.onPause();
		if ( D )
			Log.i(TAG, "+++ ON PAUSE +++");
		if ( appClient.getState() == AppClient.CONNECTED )
		{
			appClient.closeClient();
		}

	}// end of onPause() method


	// if the user stops the app cancel the timer
	// to free up resources and battery life
	@Override
	public void onStop()
	{
		super.onStop();
		if ( D )
			Log.i(TAG, "+++ ON STOP +++");

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
		if ( appService != null )
		{
			appService.endAccelometerService();
			appService = null;
		}
		finish();
	}

}// end of the class
