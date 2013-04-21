package itt.t00154755.mouseapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class WaitToConnectDialog extends Activity
{

	public void onCreate( Bundle savedInstanceState )
	{

		super.onCreate(savedInstanceState);
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(null);

		// set title
		alertDialogBuilder.setTitle("Waiting to connect to device...");

		// set dialog message
		alertDialogBuilder.setMessage("Click yes to exit!").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{

			public void onClick( DialogInterface dialog, int id )
			{
				// if this button is clicked, close
				// current activity
				WaitToConnectDialog.this.finish();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener()
		{

			public void onClick( DialogInterface dialog, int id )
			{
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}