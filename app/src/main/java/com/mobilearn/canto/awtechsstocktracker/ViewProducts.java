package com.mobilearn.canto.awtechsstocktracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewProducts extends AppCompatActivity {
    ProgressDialog dialog;
    ListView listView;
    int page;
    TextView t;
    Drawable end;
    Button cancel, submit;
    EditText ttitle, description, myFilter;
    ProgressDialog mProgressDialog;
    ProgressBar progressbar;
    HttpURLConnection con;
    android.support.v7.app.AlertDialog alertDialog;
    CheckBox status;
    Typeface typeface;
    Spinner members,types;
    TextView wait;
    Product mems;
    TextView title,verse;
    EditText amount,desc,mpesa;

    ImageView share, back;
    String checkstatus, server, base_url,res;
    @NonNull
    Boolean flag_loading = false;
    JSONObject json_data;
    String query, resulta;
    EditText search;
    ViewProductAdapter adapter;
    ArrayList<Product> apps, prior;
    ProgressBar progressBar;
    ArrayList<String> memberlist,typelist;
    ArrayAdapter<String> memberadapter,typeadapter;
    SharedPreferences settings;

    RelativeLayout swipeRefreshLayout;
    private int offSet = 0;
    FontChangeCrawler fontChangeCrawler;
    HashMap<String, String> membermap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupActionBar();
        SpannableString s = new SpannableString("Products");
        s.setSpan(new TypefaceSpan(this,"robotcon.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //getSupportActionBar().setTitle(s);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        setContentView(R.layout.activity_allmembers);
        membermap = new HashMap<String, String>();

        fontChangeCrawler=new FontChangeCrawler(this);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/robotcon.ttf");
        search = findViewById(R.id.search);
        progressbar=findViewById(R.id.progressbar);
        server = getResources().getString(R.string.server);
        settings = getSharedPreferences("chamasettings", Context.MODE_PRIVATE);
        swipeRefreshLayout = findViewById(R.id.linear);
        fontChangeCrawler.replaceFonts(swipeRefreshLayout);
        apps = new ArrayList<>();
        prior = new ArrayList<>();
        listView = findViewById(R.id.clist);
        listView.setTextFilterEnabled(true);
        listView.setEmptyView(findViewById(R.id.empty));
        adapter = new ViewProductAdapter(this, R.layout.listitem, apps);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });
        listView.setEmptyView(findViewById(R.id.empty));
fetchMovies();
        search.setTypeface(typeface);
        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");

                adapter.getFilter().filter(s.toString());


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                //t.setText("Members (" + listView.getCount() + ")");

            }

            @Override
            public void afterTextChanged(Editable s) {
                //t.setText("Members (" + listView.getCount() + ")");
            }
        });
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(end);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private void setupActionBar() {
ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
 public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.sync) {
            apps.clear();


            if(new ConnectionDetector(ViewProducts.this).isConnectingToInternet())
                fetchMovies();
            else{
                Snackbar.make(swipeRefreshLayout, "No Internet Connection", Snackbar.LENGTH_LONG)

                        .show();
            }
            adapter.notifyDataSetChanged();

        }
        if (id == R.id.add) {
            //Intent list=new Intent(ViewProducts.this,AllProducts.class);
            //startActivity(list);
            //finish();
            Snackbar.make(swipeRefreshLayout, "This Option Is Disabled", Snackbar.LENGTH_LONG);
        }
        return super.onOptionsItemSelected(item);
    }
    public void fetchMovies() {
        base_url = server+"/allproducts.php?";
        // showing refresh animation before making http call

progressbar.setVisibility(View.VISIBLE);
        // appending offset to url
        String url = base_url;

        // Volley's json array request object

        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(@NonNull JSONArray response) {

                        Log.d("res",response.toString());
                        if (response.length() > 0) {

                            // looping through json and adding to movies list
                            for (int i = 0; i < response.length(); i++) {
                                try {


                                    JSONObject json = response.getJSONObject(i);
                                    mems = new Product();
                                    mems.setId(json.optString("product_id"));
                                    mems.setName(json.optString("product_name"));
                                    mems.setDescription(json.optString("product_desc"));
                                    mems.setImage(json.optString("image"));
                                    mems.setStatus(json.optString("enabled"));
                                    mems.setTown_stock(json.optString("town_stock"));
                                    mems.setWarehouse_stock(json.optString("hardware_stock"));
                                    mems.setUpdated_by(json.optString("updated_by"));
                                    mems.setUpdated_on(json.optString("updated_on"));
                                    apps.add(mems);


                                } catch (JSONException e) {
                                    Log.d("error",""+e.getLocalizedMessage());

                                }

                            }
                           // apps.add(mems);
                            adapter.notifyDataSetChanged();

                        }
progressbar.setVisibility(View.GONE);
                        // stopping swipe refresh
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(@NonNull VolleyError error) {


                Toast.makeText(getApplicationContext(), "Connection error.Could not load data" + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressbar.setVisibility(View.GONE);
                // stopping swipe refresh
                adapter.notifyDataSetChanged();

            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }








    public static class MySpinnerAdapter extends ArrayAdapter<String> {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/robotcon.ttf");
        public MySpinnerAdapter(Context context, int resource, List<String> items) {
            super(context, resource, items);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getDropDownView(position, convertView, parent);
            view.setTypeface(font);
            return view;
        }
    }


}


