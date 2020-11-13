package tech.hans.climatico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity {

    EditText temptxt;
    Button b;
    String city;
    String apiurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temptxt = (EditText) findViewById(R.id.temptxt);
        b = (Button) findViewById(R.id.btn);
        city = "";
        apiurl = "";


    }

    public void demo(View view){
        String city = temptxt.getText().toString();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(city.equals("")){

            Toast.makeText(this,"Please enter a city name above!", LENGTH_SHORT).show();
            mgr.hideSoftInputFromWindow(temptxt.getWindowToken(),0);
        }
        else {
            TempDownload td = new TempDownload();
            apiurl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&APPID=4c762db3cd7b8228ad3be9a0b94e57e7";
            td.execute(apiurl);
            mgr.hideSoftInputFromWindow(temptxt.getWindowToken(),0);
        }
    }




    public class TempDownload extends AsyncTask<String, Void, String> {




        @Override
        protected String doInBackground(String... strings) {
            String res = "";
            char ch;
            URL url;
            HttpURLConnection urlConnection = null;
            InputStream in = null;
            try {

                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int d = reader.read();
                while (d != -1) {
                    ch = (char) d;
                    res = res + ch;
                    d = reader.read();
                }
                return res;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    if (in != null) {
                        try {
                            in.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }


        }

        @Override
        protected void onPostExecute(String s) {

            if(s == null || s.equals("")){
                Toast.makeText(getApplicationContext(),"ENTER VALID CITY", LENGTH_SHORT).show();
            }

            try {
                JSONObject jsonObject = new JSONObject(s);
                String jsobj = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(jsobj);
                JSONObject jsonfinal = jsonArray.getJSONObject(0);


                String main = jsonfinal.getString("main");
                String description = jsonfinal.getString("description");

                JSONObject jsonTemp = jsonObject.getJSONObject("main");

                double tempK = jsonTemp.getDouble("temp");
                String outp = Double.toString(tempK);
                Log.i("tempk----->---->",outp);
                float tempC = (float) (tempK - 273.15);
                String temperature = Float.toString(tempC) + " Degree Celcius";
                String finalOutput = "Temperature:\n"+temperature+"\n\n\nsky:\n"+main+"\n\n\ndescription:\n"+description;
                Toast.makeText(getApplicationContext(),finalOutput, LENGTH_LONG).show();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}