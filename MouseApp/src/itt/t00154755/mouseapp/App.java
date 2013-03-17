package itt.t00154755.mouseapp;


import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class App extends Activity {
	protected static final String TAG = "Main App";
	private Button send;
	private Timer updateTimer;
	private AppUtils a;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        send = (Button) findViewById(R.id.send);
        
        send.setOnClickListener( new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// starts the connection process - server must be running
				a.info(TAG, "client connecting to server");
				AppClient appClient = new AppClient();
				appClient.connectToSever();
			} 
        });
        
        a.info(TAG, "starting the update timer, updates every .0032 of a second");
        updateTimer = new Timer();
		updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 0, 32);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app, menu);
        return true;
    }

    private class AcceleratorUpdater extends TimerTask implements SensorEventListener {

    	Handler accHandler;
    	App app;
    	public AccelerometerObject acceloObj;
    	// sensor manager variables
    	SensorManager sm;
    	Sensor s;

    	public AcceleratorUpdater(Handler accHandler, App app) {
    	    super();
    	    this.accHandler = accHandler;
    	    this.app = app;

    	    a.info(TAG, "In AcceleratorUpdater update const");
    	    registerListener();
    	}

    	private void registerListener() {
    	    a.info(TAG, "In AcceleratorUpdater reg listener");
    	    sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

    	    if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
    		s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
    		sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
    	    }
    	}

    	@Override
    	public void onAccuracyChanged(Sensor sensor, int accuracy) {
    	}

    	@Override
    	public void onSensorChanged(SensorEvent event) {
    	    //
    		 a.info(TAG, "In sensorchanged of of AcceleratorUpdater");

    	    acceloObj = createNewAccelerometerObject(event);
    	}

    	/**
    	 * Used to create a new AccelerometerObject each time that
    	 * onSensorChanged() is called.
    	 * 
    	 * @param accobj
    	 *            the empty AccelerometerObject
    	 * @param values
    	 *            the float[] of event values
    	 * @return the new AccelerometerObject with assigned values
    	 */
    	public  AccelerometerObject createNewAccelerometerObject(SensorEvent event) {
    		 a.info(TAG, "In create new object AcceleratorUpdater");
    	    AccelerometerObject accobj = new AccelerometerObject();

    	    if (event != null){
    		 accobj.setX(event.values[0]);
    		 accobj.setY(event.values[1]);
    		 accobj.setZ(event.values[2]);
    	    }
    	   return accobj;
    	}

    	@Override
    	public void run() {
    		 a.info(TAG, "In AcceleratorUpdater run"); 
    	    accHandler.post(new Runnable() {
    		@Override
    		public void run() {
    		    app.sendObjectToServer(acceloObj);
    		    updateTimer.cancel();
    		}
    	    });
    	}


        }

	protected void sendObjectToServer(AccelerometerObject acceloObj) {
		// TODO Auto-generated method stub
		
	}
    
}
