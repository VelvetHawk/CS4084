package project.cs4084.asteroids;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// extends StringRequest from volley
    /*
    Allow us to make a request to the register.php file on the server and get a response
    as a string which is why we extend StringRequest. Need to specifiy the URL where our register.php file is

     */
public class RegisterRequest extends StringRequest {
    // static, final - wont change
    private static final String REGISTER_REQUEST_URL = "http://brianed.000webhostapp.com/register.php";
    // hasmpa to store values
    private Map<String, String> params;

    // create constructor, first method that runs when an instance of this class is created
    // takes in the variables needed for registration to use against the php files.
    // this constructor also needs a Response.Listener
    public RegisterRequest(String name, String username, String password, Response.Listener<String> listener) {
        // need to pass data to volley which will allow it to execute the request, use the super of the class
        // Method.POST - send some data to register.php. second param - give volley the URL, third param - give it listener..
        // ..when volley is finished with the request it will inform the Response.Listener<String> listener
        // 4th param is for error management
        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        // need to make volley pass information to request. first create our param
        params = new HashMap<>();
        // put data into hashmap then volley needs to access it
        params.put("name", name);
        params.put("username", username);
        params.put("password", password);
    }

    // get the data thats in the params
    // when the request is executed, volley will call getParams()
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
