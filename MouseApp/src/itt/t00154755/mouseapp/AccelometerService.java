package itt.t00154755.mouseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * AccelometerService
 * 
 * @author
 */
public class AccelometerService extends Service implements SensorEventListener
{

	private static final String TAG = "Accelometer Service";
	private static final boolean D = true;
	private String accelerometerData = " 1 0 0 ";
	private SensorEvent event;
	private int xIntAxis;
	private int yIntAxis;
	// sensor movement direction
	private static final int LEFTDOWN = 1;
	private static final int RIGHTUP = 2;
	private static final int LEFTUP = 3;
	private static final int RIGHTDOWN = 4;
	private Context context;
	private Handler appHandler;
	private Thread sendDataThread;

	// sensor manager variables
	private SensorManager accelerometerManager;
	private Sensor accelerometerSensor;


	public AccelometerService( Context context, Handler appHandler )
	{
		this.context = context;
		this.appHandler = appHandler;
	}


	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		// TODO: return start sticky this allows the service to run
		// until told otherwise
		return START_STICKY;
	}
	
	@Override
	public void onCreate()
	{
		// TODO initiate the service
		super.onCreate();
		initAccelometerService();
	}
	@Override
	public void onDestroy()
	{
		// TODO stop the service and the helper thread
		super.onDestroy();
		endAccelometerService();
	}


	public void initAccelometerService()
	{
		makeToastShort("AccelometerService started");
		registerListener();
	}


	public void endAccelometerService()
	{
		unregisterListener();
		// stop the service
		stopSelf();
		makeToastShort("AccelometerService stopped");
	}


	private void makeToastShort( String string )
	{
		// message back to UI
		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST_ACCELO);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, string);
		message.setData(bundle);
		appHandler.handleMessage(message);
	}


	private void sendDataToUIActivity( String string )
	{
		// message back to UI
		Message message = appHandler.obtainMessage(App.MESSAGE_DATA_ACCELO);
		Bundle bundle = new Bundle();
		bundle.putString(App.DATA, string);
		message.setData(bundle);
		appHandler.handleMessage(message);
	}


	/**
	 * @return the accelerometerManager
	 */
	public SensorManager getAccelerometerManager()
	{
		return (SensorManager ) context.getSystemService(Context.SENSOR_SERVICE);
	}


	// Method that registered the Listener for the Accelerometer
	// Object.
	private void registerListener()
	{
		if ( D )
			Log.d(TAG, "+++ REGISTER-LISTENER FOR SENSOR +++");

		accelerometerManager = getAccelerometerManager();
		// check to make sure that the SensorList is not empty
		if ( accelerometerManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 )
		{
			// get the Accelerometer Sensor
			accelerometerSensor = accelerometerManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			// register the listener to the Sensor
			accelerometerManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}


	private void unregisterListener()
	{
		if ( D )
			Log.d(TAG, "+++ UNREGISTER-LISTENER FOR SENSOR +++");
		accelerometerManager.unregisterListener(this, accelerometerSensor);

	}


	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy )
	{
		//
	}


	@Override
	public void onSensorChanged( SensorEvent event )
	{
		if ( D )
			Log.d(TAG, "+++ SENSOR CHANGE +++");
		// long timestamp = event.timestamp;
		sendData(event);
	}
	// javarevisited.blogspot.com/2011/02/how-to-implement-thread-in-java.html#ixzz2R3ICcqPJ
	private class SendDataThread implements Runnable
	{

		public void run()
		{
			sendDataToUIActivity(getAccelerometerData());
		}

	}


	private void sendData( SensorEvent event )
	{
		this.event = event;
		// remove the integer x and y values from the float array.
		xIntAxis = (int ) this.event.values[0];
		yIntAxis = (int ) this.event.values[1];

		if ( xIntAxis < 0 && yIntAxis < 0 )
		{
			// add only the positive values
			if ( D )
				Log.d(TAG, "move mouse: " + LEFTDOWN);
			setAccelerometerData(" " + LEFTDOWN + " " + Math.abs(xIntAxis) + " " + Math.abs(yIntAxis) + " ");
		}
		else
			if ( xIntAxis > 0 && yIntAxis > 0 )
			{
				// add only the positive values
				if ( D )
					Log.d(TAG, "move mouse: " + RIGHTUP);
				setAccelerometerData(" " + RIGHTUP + " " + Math.abs(xIntAxis) + " " + Math.abs(yIntAxis) + " ");
			}
			else
				if ( xIntAxis < 0 && yIntAxis > 0 )
				{
					// add only the positive values
					if ( D )
						Log.d(TAG, "move mouse: " + LEFTUP);
					setAccelerometerData(" " + LEFTUP + " " + Math.abs(xIntAxis) + " " + Math.abs(yIntAxis) + " ");
				}
				else
					if ( xIntAxis > 0 && yIntAxis < 0 )
					{
						// add only the positive values
						if ( D )
							Log.d(TAG, "move mouse: " + RIGHTDOWN);
						setAccelerometerData(" " + RIGHTDOWN + " " + Math.abs(xIntAxis) + " " + Math.abs(yIntAxis) + " ");
					}

		sendDataThread = new Thread(new SendDataThread()); // Thread created
		sendDataThread.start();
	}


	/**
	 * @return the accelerometerData
	 */
	public String getAccelerometerData()
	{
		return accelerometerData;
	}


	/**
	 * @param accelerometerData
	 *        the accelerometerData to set
	 */
	public void setAccelerometerData( String accelerometerData )
	{
		this.accelerometerData = accelerometerData;
	}


	@Override
	public IBinder onBind( Intent intent )
	{
		// TODO Auto-generated method stub
		return null;
	}

}// end class
