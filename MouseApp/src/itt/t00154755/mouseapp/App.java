package itt.t00154755.mouseapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class App extends Activity {
	private Button send;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        send = (Button) findViewById(R.id.send);
        
        send.setOnClickListener( new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// starts the connection process - server must be running
				AppClient appClient = new AppClient();
			} 
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app, menu);
        return true;
    }

    
}
