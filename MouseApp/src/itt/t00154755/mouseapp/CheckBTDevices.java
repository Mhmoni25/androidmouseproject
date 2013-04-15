package itt.t00154755.mouseapp;

import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class CheckBTDevices extends Activity
{

	// Debugging
	private static final String TAG = "CheckBTDevices";
	private static final boolean D = true;

	// Return Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	// Member fields
	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> connectedDevicesArrayAdapter;
	private ArrayAdapter<String> availableDevicesArrayAdapter;


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);

		// Setup the window
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.checkbt);

		// Set result CANCELED incase the user backs out
		setResult(Activity.RESULT_CANCELED);

		// Initialize the button to perform device discovery
		final Button searchButton = (Button ) findViewById(R.id.bSearch);
		searchButton.setOnClickListener(new OnClickListener()
		{

			public void onClick( View v )
			{
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});

		// Initialize array adapters. One for already paired devices and
		// one for newly discovered devices
		connectedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		availableDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		// Find and set up the ListView for paired devices
		ListView connectedListView = (ListView ) findViewById(R.id.lvConnected);
		connectedListView.setAdapter(connectedDevicesArrayAdapter);
		connectedListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> arg0, View v, int arg2, long arg3 )
			{
				// Cancel discovery because it's costly and we're about to connect
				btAdapter.cancelDiscovery();

				// Get the device MAC address, which is the last 17 chars in the View
				String info = ( (TextView ) v ).getText().toString();
				String address = info.substring(info.length() - 13);

				// Create the result Intent and include the MAC address
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

				// Set result and finish this Activity
				setResult(Activity.RESULT_OK, intent);
			}
		});

		// Find and set up the ListView for newly discovered devices
		ListView availableListView = (ListView ) findViewById(R.id.lvAvailable);
		availableListView.setAdapter(availableDevicesArrayAdapter);
		availableListView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> arg0, View v, int arg2, long arg3 )
			{
				// Cancel discovery because it's costly and we're about to connect
				btAdapter.cancelDiscovery();

				// Get the device MAC address, which is the last 17 chars in the View
				String info = ( (TextView ) v ).getText().toString();
				String address = info.substring(info.length() - 13);

				// Create the result Intent and include the MAC address
				Intent intent = new Intent();
				intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

				// Set result and finish this Activity
				setResult(Activity.RESULT_OK, intent);	
			}
		});

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(btReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(btReceiver, filter);

		// Get the local Bluetooth adapter
		btAdapter = BluetoothAdapter.getDefaultAdapter();

		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if ( pairedDevices.size() > 0 )
		{
			findViewById(R.id.bt_connectedDevices).setVisibility(View.VISIBLE);
			for ( BluetoothDevice device : pairedDevices )
			{
				connectedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			}
		}
		else
		{
			String noDevices = getResources().getText(R.string.bt_not_found).toString();
			connectedDevicesArrayAdapter.add(noDevices);
		}
	}


	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if ( btAdapter != null )
		{
			btAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(btReceiver);
	}


	/**
	 * Start device discover with the BluetoothAdapter
	 */
	private void doDiscovery()
	{
		if ( D )
			Log.d(TAG, "doDiscovery()");

		// Indicate scanning in the title
		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.bt_searching);

		// Turn on sub-title for new devices
		findViewById(R.id.bt_available).setVisibility(View.VISIBLE);

		// If we're already discovering, stop it
		if ( btAdapter.isDiscovering() )
		{
			btAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		btAdapter.startDiscovery();
	}

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver btReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive( Context context, Intent intent )
		{
			String action = intent.getAction();

			// When discovery finds a device
			if ( BluetoothDevice.ACTION_FOUND.equals(action) )
			{
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed already
				if ( device.getBondState() != BluetoothDevice.BOND_BONDED )
				{
					availableDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
				// When discovery is finished, change the Activity title
			}
			else
				if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) )
				{
					setProgressBarIndeterminateVisibility(false);
					setTitle(R.string.bt_choose);
					if ( availableDevicesArrayAdapter.getCount() == 0 )
					{
						String noDevices = getResources().getText(R.string.bt_not_found).toString();
						availableDevicesArrayAdapter.add(noDevices);
					}
				}
		}
	};

}