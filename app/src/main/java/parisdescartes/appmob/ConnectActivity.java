package parisdescartes.appmob;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import parisdescartes.appmob.Retrofit.KissorService;
import parisdescartes.appmob.Item.User;
import parisdescartes.appmob.database.DatabaseHelper;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Version modifiée le 28/02/2016 à 1h50 par Yassin HASSAN
 * 1 - Ajout de la variable myDb
 * 2 - Initialisation de la variable myDb dans la méthode onCreate()
 * 3 - Dans la callback de la methode onCompleted , insertion du user créé dans la base de donnée myDb
 *
 * NB : les id des users sont des int dans la base de données et des long dans le ConnectActivity
 */
public class ConnectActivity extends Activity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProgressDialog progress;
    private SharedPreferences sharedpreferences;

    DatabaseHelper myDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        myDb = ((Application)getApplication()).getDb();
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        sharedpreferences  = getSharedPreferences("USER", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_connect);
        if (AccessToken.getCurrentAccessToken() != null) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
        }
        fbConnect();

    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
       /*if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
        }*/
    }

    public Context getContext(){
        return this;
    }

    public void fbConnect() {
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progress = ProgressDialog.show(getContext(), "Connexion en cours",
                        "Veuillez patienter....", true);
                progress.setCancelable(true);
                progress.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            LoginManager.getInstance().logOut();
                            progress.dismiss();
                            finish();
                            startActivity(getIntent());
                            return true;
                        }
                        return true;
                    }
                });
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(final JSONObject jsonObject, GraphResponse graphResponse) {
                        KissorService kissorService = new RestAdapter.Builder().
                                setEndpoint(KissorService.ENDPOINT).
                                build().
                                create(KissorService.class);

                        kissorService.getUser(AccessToken.getCurrentAccessToken().getToken(), new Callback<User>() {
                            @Override
                            public void success(User user, Response response) {
                                progress.dismiss();

                                //TODO : Mettre dans la base de données locale
                                myDb.insertUser((int)user.getUserid(), user.getFirst_name(), user.getLast_name(), user.getPhoto_url());

                                sharedpreferences.edit().putLong("idUser", user.getUserid()).commit();
                                Intent intent = new Intent(getContext(), MapsActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                progress.dismiss();
                                LoginManager.getInstance().logOut();
                                errorDialog("Erreur : \n" + error.toString());
                            }
                        });

                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,last_name,email,gender, birthday,first_name,picture.height(500).width(500).type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();
                errorDialog("Connexion annulée.");
            }

            @Override
            public void onError(FacebookException e) {
                LoginManager.getInstance().logOut();
                errorDialog("Connexion impossible. Veuillez ressayer.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect, menu);
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

    public void errorDialog(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(ConnectActivity.this).create();
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
