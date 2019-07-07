package project.cs4084.asteroids;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this line says which xml file to work from
        setContentView(R.layout.activity_register);

        // hold reference to all fields and buttons in activity_register xml file
        // final because this is the only variable its being assigned to
        // findViewById looks at activity_register and finds a view
        // casted as (EditText) so that findViewById knows what to look for
        final EditText registerName = (EditText) findViewById(R.id.registerName);
        final EditText registerID = (EditText) findViewById(R.id.registerID);
        final EditText registerPassword = (EditText) findViewById(R.id.registerPassword);

        // Button
        final Button registerButton = (Button) findViewById(R.id.registerButton);

        // finish implementation after RegisterRequest
        // when user clicks register, get the information from the fields and pass to RegisterRequest and execute request
        // so add listener to register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // when user clicks register, get all the info theyve entered
                // get the text entered into the registerName field and convert to string
                final String name = registerName.getText().toString();
                final String username = registerID.getText().toString();
                final String password = registerPassword.getText().toString();

                if(name.matches("") || username.matches("") || password.matches("")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("All fields are required.").setNegativeButton("Retry", null).create().show();
                } else if(name.length() <= 1 || username.length() <= 1 || password.length() <= 1) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(RegisterActivity.this);
                    builder1.setMessage("All fields must be at least 2 characters.").setNegativeButton("Retry", null).create().show();
                } else {
                    // create request with all the needed params, for 4th param need to create listener
                    // Callback interface for delivering parsed responses
                    Response.Listener<String> responseListener = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // anything in here happens when response has been executed.
                            // need to convert to JSON object to work with it because its encoded to a JSON file in the php files
                            // gets the String response that volley has given us and converts to JSONObject
                            // new JSONObject needs to be surrounded with try and catch because it can fail if String response is
                            // not in the form of a JSON string
                            try {
                                JSONObject jsonResponse = new JSONObject(response);

                                // we have a JSON response so we can get the response
                                // when registered the response is 'success' so need to get that
                                // its called success is register.php
                                boolean success = jsonResponse.getBoolean("success");

                                // If registration is successful want to move to login page
                                if (success) {
                                    LoginActivity.mMediaPlayer.stop();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    RegisterActivity.this.startActivity(intent);
                                } else {
                                    // create error message, if failed create retry button
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("Registration Failed").setNegativeButton("Retry", null).create().show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    RegisterRequest registerRequest = new RegisterRequest(name, username, password, responseListener);
                    // need to add this request to request queue for volley
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    // add to queue
                    queue.add(registerRequest);
                    // need to add permission to manifest to allow INTERNET
                }
            }
        });
    }
}
