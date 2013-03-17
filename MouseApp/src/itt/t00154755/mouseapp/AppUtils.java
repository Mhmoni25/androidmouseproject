package itt.t00154755.mouseapp;

import android.util.Log;

public class AppUtils {
	
	public void error(String tag, String position, Exception e){
		Log.e(tag, position);
		e.printStackTrace();
	}
	
	public void info(String tag, String position){
		Log.i(tag, position);
	}
	
}
