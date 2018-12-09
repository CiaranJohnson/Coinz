package com.example.tech.coinz;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static com.android.volley.VolleyLog.TAG;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class JsonTask extends AsyncTask<Void, Void, Void> {

    ArrayList<HashMap<String, String>> coinList;
    private static final String TAG = "JsonTask";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(getApplicationContext(),"Json Data is downloading",Toast.LENGTH_LONG).show();

    }

    @Override
    protected Void doInBackground(Void... voids) {

        String url = "http://homepages.inf.ed.ac.uk/stg/coinz/2019/12/31/coinzmap.geojson";
        HttpHandler sh = new HttpHandler();
        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url);

        Log.e(TAG, "Response from url: " + jsonStr);
        if (jsonStr != null) {
            Log.d(TAG, "A O.K.");
//                JSONObject jsonObj = new JSONObject(jsonStr);
//
//                // Getting JSON Array node
//                JSONArray contacts = jsonObj.getJSONArray("features");
//
//                // looping through All Contacts
//                for (int i = 0; i < contacts.length(); i++) {
//                    JSONObject c = contacts.getJSONObject(i);
//                    JSONObject properties = c.getJSONObject("properties");
//                    String id = properties.getString("id");
//                    String value = properties.getString("value");
//                    String currency = properties.getString("currency");
//                    String markerSymbol = properties.getString("marker-symbol");
//                    String markerColour = properties.getString("marker-colour");
//
//                    // Geometry node is JSON Object
//                    JSONObject geometry = c.getJSONObject("geometry");
//                    ArrayList<String> latlng = new ArrayList<>();
//                    latlng.add(geometry.getString("coordinates"));
//
//                    // tmp hash map for single contact
//                    HashMap<String, String> coin = new HashMap<>();
//
//                    // adding each child node to HashMap key => value
//                    coin.put("id", id);
//                    coin.put("value", value);
//                    coin.put("currency", currency);
//                    coin.put("marker-symbol", markerSymbol);
//                    coin.put("marker-colour", markerColour);
//
//                    // adding contact to contact list
//                    coinList.add(coin);
//            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(),
//                            "Couldn't get json from server. Check LogCat for possible errors!",
//                            Toast.LENGTH_LONG).show();
//                }
//            });
        }

        return null;
    }

}




//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            ListAdapter adapter = new SimpleAdapter(LoginActivity.this, contactList,
//                    R.layout.list_item, new String[]{ "email","mobile"},
//                    new int[]{R.id.email, R.id.mobile});
//            lv.setAdapter(adapter);
//        }
//    }
//}
