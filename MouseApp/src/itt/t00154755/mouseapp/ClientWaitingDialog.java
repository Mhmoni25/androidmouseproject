package itt.t00154755.mouseapp;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * 
 * @author Christopher Donovan
 * @author ClientWaitingDialog.java
 * @since 10/02/2015
 * @version 2.0
 *
 */
public class ClientWaitingDialog extends DialogFragment 
{

	public void onCreate( Bundle savedInstanceState )
	{
		/* The activity that creates an instance of this dialog fragment must
	     * implement this interface in order to receive event callbacks.
	     * Each method passes the DialogFragment in case the host needs to query it. 
	    public interface ClientWaitingDialog {
	        public void onDialogPositiveClick(DialogFragment dialog);
	        public void onDialogNegativeClick(DialogFragment dialog);
	    }
	    
	    // Use this instance of the interface to deliver action events
	    ClientWaitingDialog mListener;
	    
	    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (ClientWaitingDialog) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement ClientWaitingDialog");
	        }
	    }*/
	}

}
