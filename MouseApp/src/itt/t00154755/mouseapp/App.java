package itt.t00154755.mouseapp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author Christopher
 * 
 *         This is the main activity and the point at which the user interacts
 *         with the application. The Accelerometer data is read from here and
 *         passed on to the client and from the client to the server.
 * 
 */
public class App extends Activity
{

	private static final String TAG = "Main App";

	public static final int MESSAGE_STATE_CHANGED = 0;
	public static final int MESSAGE_DEVICE_NAME = 1;
	public static final int MESSAGE_TOAST = 2;
	public static final int READ = 3;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final int REQUEST_CONNECT_DEVICE = 0;
	private static final int REQUEST_ENABLE_BT = 1;

	public String connectDeviceName = null;

	public Button rightbtn;
	public Button leftbtn;
	public EditText editText;
	private Timer updateTimer;
	private BluetoothAdapter btAdapter;
	private AppClientService appClientService;


	/*
	 * i want to start the connection process and ensure that it is running
	 * before i begin the accelerometer thread and start passing the values to
	 * it.
	 */
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		// set the content view to the main xml file
		setContentView(R.layout.main);
		rightbtn = (Button ) findViewById(R.id.bRight);
		leftbtn = (Button ) findViewById(R.id.bLeft);
		editText = (EditText ) findViewById(R.id.edText);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		if ( btAdapter == null )
		{
			Toast.makeText(this, "bluetooth not available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}// end of onStart() method


	@Override
	protected void onStart()
	{
		super.onStart();

		if ( !btAdapter.isEnabled() )
		{
			Intent requestBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(requestBT, REQUEST_ENABLE_BT);
		}
		else
		{
			if ( appClientService == null )
			{
				setUpService();
			}
		}

	}// end of onStart() method


	private void setUpService()
	{
		appClientService = new AppClientService(this, appHandler);

	}


	@Override
	protected void onPause()
	{
		super.onPause();
		if ( appClientService != null )
		{
			if ( appClientService.getState() == AppClientService.NONE )
			{
				appClientService.start();
			}
		}
	}// end of onPause() method


	@Override
	protected void onStop()
	{

		super.onStop();
		// cancel the update timer
		// when the app is stopped
		if ( updateTimer != null )
		{
			updateTimer.cancel();
		}
		System.exit(-1);
	}// end of onStop() method


	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if ( appClientService != null )
		{
			appClientService.stop();
		}
	}
	// ================================================appHandler==========================================================================
	private final Handler appHandler = new Handler()
	{

		@Override
		public void handleMessage( Message message )
		{
			switch ( message.what )
			{
				case MESSAGE_STATE_CHANGED:
					switch ( message.arg1 )
					{
						case AppClientService.CONNECTED:
						break;
						case AppClientService.CONNECTING:
						break;
						case AppClientService.LISTEN:
						case AppClientService.NONE:
						break;
					}
				break;
				case MESSAGE_DEVICE_NAME:
					connectDeviceName = message.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(), "Connected to: " + connectDeviceName, Toast.LENGTH_SHORT).show();
				break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(), message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};


	public void onActivityRequest( int requestCode, int resultCode, Intent data )
	{
		switch ( requestCode )
		{
			case REQUEST_CONNECT_DEVICE:
				if ( resultCode == Activity.RESULT_OK )
				{
					String address = data.getExtras().getString(CheckBTAvailability.EXTRA_DEVICE_ADDRESS);

					BluetoothDevice device = btAdapter.getRemoteDevice(address);
					appClientService.connect(device);
				}
			break;
			case REQUEST_ENABLE_BT:
				if ( resultCode == Activity.RESULT_OK )
				{
					setUpService();
				}
				else
				{
					Toast.makeText(this, "bluetooth not available", Toast.LENGTH_SHORT).show();
					finish();
				}
			break;
		}
	}


	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_VOLUME_UP )
		{
			appClientService.write(AppClientService.VOL_UP);
			return true;
		}
		else
			if ( keyCode == KeyEvent.KEYCODE_VOLUME_DOWN )
			{
				appClientService.write(AppClientService.VOL_DOWN);
				return true;
			}

		return super.onKeyDown(keyCode, event);

	}


	/**
	 * This method is where the update timer is set up the
	 * the timer event must only be started once and allowed
	 * to run in the background uninterrupted until the app
	 * is stopped.
	 * 
	 * The Timer object has a schedule method that takes four
	 * arguments in my case:
	 * 
	 * <pre>
	 * 	updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 100, 100);
	 *  
	 *  AcceleratorUpdater: is a TimerTask that i created.
	 *  Handler: 			is a reference to this (App) Activity, which the AcceleratorUpdater
	 *  					needs to be able to refer back to the Main UI thread.
	 *  Delay (100): 		is the startup time after the update timer is fired; milliseconds.
	 *  Period (100):		update time every 100 milliseconds the timer will pass the data back to the Main UI
	 *  					Thread via the Handler.
	 * </pre>
	 * 
	 * This method will be started as soon as the connection is established.
	 */
	public void startTheUpdateTimerTask()
	{
		updateTimer = new Timer();
		Log.d(TAG, "starting the update timer, updates every .001 of a second");
		updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 100, 100);

	}// end of startTheUpdateTimerTask() method


	protected void passStringDataToServer( String acceloData ) throws IOException
	{

		// pass the string which contains the data array to the server
		// appClient.write(acceloData);
		Log.i(TAG, "data st 1 " + acceloData);
	}// end of passStringDataToServer() method


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}// end of onCreateOptionsMenu() method

	// ================================================AcceleratorUpdater==========================================================================
	/**
	 * 
	 * @author Christopher Donovan
	 * 
	 */
	private class AcceleratorUpdater extends TimerTask implements SensorEventListener
	{

		private static final String TAG = "AcceleratorUpdater Timer";

		// Handler used to communicate with the Main UI Thread
		Handler accHandler;

		// Reference back to the Main UI Thread Activity
		App app;

		// The String that will contain the data from the
		// Accelerometer Object
		String acceloData;


		/**
		 * Constructor for the AcceleratorUpdater Class.
		 * 
		 * @param accHandler
		 *        the handler used to communicate with the Main UI Thread
		 * @param app
		 *        reference to the creator of the Handler object
		 */
		public AcceleratorUpdater( Handler accHandler, App app )
		{

			// assign the class variables
			// to the incoming parameters
			this.accHandler = accHandler;
			this.app = app;

			// register the listener for the Accelerometer Object
			Log.d(TAG, "In AcceleratorUpdater update const");
			registerListener();
		}


		/*
		 * Method that registered the Listener for the Accelerometer
		 * Object.
		 */
		private void registerListener()
		{

			// sensor manager variables
			SensorManager sm;
			Sensor s;

			// uses the app reference to register the listener
			// i do it this way here because this class those
			// not extend activity the getSystemService()
			// is a method that belongs to the Activity Class
			// so i refer the method to the App Activity which dose
			// extend Activity

			Log.d(TAG, "In AcceleratorUpdater reg listener");
			sm = (SensorManager ) getSystemService(Context.SENSOR_SERVICE);

			// check to make sure that the SensorList is not empty
			if ( sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 )
			{
				// get the Accelerometer Sensor
				s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
				// register the listener to the Sensor
				sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}


		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy )
		{

			// i do not use this method
			// but it must be implemented
		}


		/**
		 * Used to create a new String of events each time that onSensorChanged() is
		 * called.
		 * 
		 * @param event
		 *        the SensorEvent from the accelerometer sensor
		 * @return acceloData the string representation of the array events
		 */
		@Override
		public void onSensorChanged( SensorEvent event )
		{

			//
			Log.d(TAG, "In sensorchanged of of AcceleratorUpdater");
			// alpha is calculated as t / (t + dT)
			// with t, the low-pass filter's time-constant
			// and dT, the event delivery rate

			final float alpha = 0.8f;

			float[] gravity = new float[3];
			gravity[0] = alpha * gravity[0] + ( 1 - alpha ) * ( event.values[0] * 10 );
			gravity[1] = alpha * gravity[1] + ( 1 - alpha ) * ( event.values[1] * 10 );
			// i do not use the axis
			// gravity[2] = alpha * gravity[2] + ( 1 - alpha ) * event.values[2] * 10;

			float[] linear_acceleration = new float[3];
			linear_acceleration[0] = event.values[0] - gravity[0];
			linear_acceleration[1] = event.values[1] - gravity[1];
			// i do not the z axis
			// linear_acceleration[2] = event.values[2] - gravity[2];

			// once gravity has been removed from the values
			// i convert the float[] to an int[] array
			covertFloatArrayToIntegerArray(event.values);
		}


		private synchronized void covertFloatArrayToIntegerArray( float[] linear_acceleration )
		{

			// remove the ceil integer x and y values from the float array.
			int xIntAxis = (int ) Math.ceil(linear_acceleration[0]);
			int yIntAxis = (int ) Math.ceil(linear_acceleration[1]);

			// add only the positive values
			acceloData = "" + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";

			// the String value
			setAcceloData(acceloData);
			Log.d(TAG, acceloData);
		}


		@Override
		public void run()
		{

			Log.d(TAG, "In AcceleratorUpdater run");
			accHandler.post(new Runnable()
			{

				@Override
				public void run()
				{

					String acceloData = getAcceloData();

					if ( acceloData == null )
					{
						Log.d(TAG, "AcceleratorUpdater data is null");
						return;
					}
					else
					{
						try
						{
							app.passStringDataToServer(acceloData);
						}
						catch ( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
			});
		}


		// get and set methods for the String Object
		public String getAcceloData()
		{

			return acceloData;
		}


		public void setAcceloData( String acceloData )
		{

			this.acceloData = acceloData;
		}
	}
}// end of the class
