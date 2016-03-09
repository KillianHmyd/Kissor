package parisdescartes.appmob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import parisdescartes.appmob.Item.Event;
import parisdescartes.appmob.Item.Participation;
import parisdescartes.appmob.Item.User;
import parisdescartes.appmob.Retrofit.KissorService;
import parisdescartes.appmob.database.DatabaseHelper;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventActivity extends Activity {

    private DatabaseHelper db;
    private Event event;
    private User creator;
    private Bundle extras;
    private String idEvent;
    private User user;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Button button = (Button) findViewById(R.id.button);
        extras = getIntent().getExtras();
        db = ((Application)getApplication()).getDb();
        idEvent = extras.getString("idEvent");
        event = db.getEvent(idEvent);
        creator = db.getUser(event.getCreated_by());
        sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        user = db.getUser(sharedPreferences.getLong("idUser", 0));
        if(db.participe(idEvent) ||creator.getUserid() == user.getUserid() ) {
            button.setText("Annuler");
        }
        else{
            button.setText("Participer");
        }
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

    public void participe(View view){
        final KissorService kissorService = new RestAdapter.Builder().
                setEndpoint(KissorService.ENDPOINT).
                build().
                create(KissorService.class);
        if(creator.getUserid() == user.getUserid()){
            errorDialog("Pas encore implémenté, désolé mec :-/");
        }
        else if(!db.participe(idEvent)) {
            kissorService.partcipe(AccessToken.getCurrentAccessToken().getToken(), new Participation(idEvent), new Callback<Participation>() {

                @Override
                public void success(Participation participation, Response response) {
                    db = ((Application) getApplication()).getDb();
                    SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
                    db.participer(idEvent, sharedPreferences.getLong("idUser", 0));
                    Button button = (Button) findViewById(R.id.button);
                    button.setText("Annuler");
                }

                @Override
                public void failure(RetrofitError error) {
                    errorDialog("Erreur : \n" + error.toString());
                }
            });
        }
        else{
            errorDialog("Pas encore implémenté, désolé mec :-/");
        }
    }

    public void errorDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(EventActivity.this).create();
        alertDialog.setTitle("Erreur");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
