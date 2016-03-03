package parisdescartes.appmob;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import parisdescartes.appmob.Item.Event;
import parisdescartes.appmob.Item.User;
import parisdescartes.appmob.database.DatabaseHelper;

public class EventActivity extends Activity {

    DatabaseHelper db;
    Event event;
    User creator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        db = ((Application)getApplication()).getDb();
        Bundle extras = getIntent().getExtras();
        String idEvent = extras.getString("idEvent");
        event = db.getEvent(idEvent);
        System.out.println("Created y : " + event.getCreated_by());
        creator = db.getUser(event.getCreated_by());
        TextView textIdEvent = (TextView) findViewById(R.id.creatorName);
        textIdEvent.setText(R.string.event_organiser + creator.getFirst_name() + " " + creator.getLast_name());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_party, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
