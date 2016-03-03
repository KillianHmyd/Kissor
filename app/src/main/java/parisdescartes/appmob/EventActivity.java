package parisdescartes.appmob;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
        creator = db.getUser(event.getCreated_by());
        TextView textIdEvent = (TextView) findViewById(R.id.creatorName);
        textIdEvent.setText(creator.getFirst_name() + " " + creator.getLast_name());
        ImageView avatar = (ImageView) findViewById(R.id.picturePorfil);
        Picasso.with(this).load(creator.getPhoto_url()).into(avatar);
        Geocoder geo = new Geocoder(EventActivity.this.getApplicationContext(), Locale.getDefault());
        TextView addresse = (TextView) findViewById(R.id.adresse);
        try {
            List<Address> addresses = geo.getFromLocation(event.getLatitude(), event.getLongitude(), 1);
            if (addresses.isEmpty()) {
                addresse.setText(R.string.erreur);
            }
            else{
                addresse.setText(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getPostalCode() + ", " + addresses.get(0).getCountryName());
            }
        } catch (IOException e) {
            addresse.setText(e.toString());
        }

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
