package com.mobilearn.canto.awtechsstocktracker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AllProductAdapter extends ArrayAdapter<Product> implements Filterable {
    String server;
    Product member;
    JSONObject json_data;
    String query, resulta;
    Button cancel_loan;
    ProgressDialog mProgressDialog;
    ProgressBar mProgressBar;
    int pos;
    HttpURLConnection con;
    android.support.v7.app.AlertDialog alertDialog;
    Typeface typeface;
    SharedPreferences settings, preference;
    FontChangeCrawler fontChangeCrawler;
    Context context;
    DecimalFormat formatter = new DecimalFormat("#,###");
    private ArrayList<Product> original;
    TextView amount,ttype,foruser,status,sendtonumber,code,createdby,createdon,desc,approvedon,approvedby;
    private List<Product> fitems;
    private Filter filter;
    public AllProductAdapter(@NonNull Context a, int textViewResourceId, @NonNull ArrayList<Product> items) {
        super(a, textViewResourceId, items);
        settings = a.getSharedPreferences("chamasettings", Context.MODE_PRIVATE);
        this.fitems = items;
        this.original = items;

        filter = new NameFilter();
        context = a;
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/robotcon.ttf");
        preference = PreferenceManager.getDefaultSharedPreferences(context);
        server = this.context.getResources().getString(R.string.server);
        fontChangeCrawler=new FontChangeCrawler(context);

    }
    @NonNull
    @Override
    public Filter getFilter() {

        return filter;
    }

    @Override
    public int getCount() {
        return fitems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @SuppressWarnings("null")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        final ViewHolder holder;
        pos=position;
        member = fitems.get(position);
        if (v == null) {

            LayoutInflater li = LayoutInflater.from(getContext());
            v = li.inflate(R.layout.memberlist_row, null);
            holder = new ViewHolder();
            holder.name = v.findViewById(R.id.name);
            holder.name.setTypeface(typeface);
            holder.type = v.findViewById(R.id.type);
            holder.move_town_btn=v.findViewById(R.id.move_town_btn);
            holder.set_store_btn=v.findViewById(R.id.set_store_btn);
            holder.set_town_btn=v.findViewById(R.id.set_town_btn);
            holder.town_num=v.findViewById(R.id.town_num);
            holder.store_num=v.findViewById(R.id.store_num);
            holder.move_num=v.findViewById(R.id.move_num);

            holder.type.setTypeface(typeface);
            holder.user_image = v.findViewById(R.id.list_image);
            holder.amount = v.findViewById(R.id.amount);
            holder.date = v.findViewById(R.id.date);
            holder.date.setTypeface(typeface);
            v.setTag(holder);
        } else
            holder = (ViewHolder) v.getTag();



        holder.name.setText(fitems.get(position).getName());
        holder.type.setText(fitems.get(position).getDescription());
        try{
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Date date = dt.parse(fitems.get(position).getUpdated_on());
        SimpleDateFormat dt1 = new SimpleDateFormat("E, dd-MMM-yy", Locale.getDefault());

        //holder.date.setText(dt1.format(date));
            //holder.date.setText("TOWN ");
        }catch (Exception d){}


        holder.date.setText("TOWN: "+formatter.format(Integer.parseInt(member.getTown_stock())));
        holder.amount.setText("STORE: "+formatter.format(Integer.parseInt(member.getWarehouse_stock())));
        Picasso.with(getContext()).load("https://www.awtechs.co.ke/stock_app/productpics/" + fitems.get(position).getImage().toString()).into(holder.user_image);
        holder.name.setFocusable(false);
        holder.type.setFocusable(false);
        holder.user_image.setFocusable(false);
        holder.amount.setFocusable(false);
        holder.date.setFocusable(false);
        holder.move_town_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final class Des extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected void onPreExecute() {


                        super.onPreExecute();

                        mProgressDialog = new ProgressDialog(context);
                        mProgressDialog.setMessage("Moving to Town..A moment please");
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mProgressDialog.setCancelable(true);
                        mProgressDialog.show();

                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("product_id", fitems.get(position).getId())
                                .appendQueryParameter("num_moved", holder.move_num.getText().toString());
                        query = builder.build().getEncodedQuery();

                    }

                    @Override
                    protected Void doInBackground(Void... params) {

                        InputStream is = null;
                        try {

                            String url = server+"/move_town.php";
                            URL obj = new URL(url);
                            con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
                            con.setRequestProperty("Accept-Language", "UTF-8");
                            con.setDoOutput(true);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
                            outputStreamWriter.write(query);
                            outputStreamWriter.flush();
                            Log.e("pass 1", "connection success ");
                        } catch (Exception e) {
                            Log.e("Fail 1", e.toString());

                        }


                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String line;
                            StringBuffer sb = new StringBuffer();

                            while ((line = in.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            //   is.close();
                            resulta = sb.toString();
                            Log.e("pass 2", "connection success ");
                        } catch (Exception e) {
                            Log.e("Fail 2", e.toString());
                        }
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Void result) {


                        try {
                            json_data = new JSONObject(resulta);
                            int code = (json_data.getInt("code"));
                            if (code == 1) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Success");
                                alert.setMessage(holder.move_num.getText().toString()+" "+fitems.get(position).getName()+" Moved From Store To Town");
                                alert.setIcon(R.drawable.suc);
                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        holder.move_num.setText("");
                                        //new AllProducts().fetchMovies();
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                            }
                            else if (code == 2) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Error");
                                alert.setMessage("You Tried Moving more than you have in Store");
                                alert.setIcon(R.drawable.error);
                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                            }
                            else {
                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Submission failed!");
                                alert.setMessage("Retry later");
                                alert.setIcon(R.drawable.error);

                                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(@NonNull DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }


                                });
                                alert.show();

                            }
                        } catch (Exception e) {
                            Log.e("Fail 3", e.toString());

                        }

                        mProgressDialog.dismiss();


                    }
                }
                new Des().execute();
            }
        });
        holder.set_town_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final class SetTown extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected void onPreExecute() {


                        super.onPreExecute();

                        mProgressDialog = new ProgressDialog(context);
                        mProgressDialog.setMessage("Setting Town Stock..A moment please");
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mProgressDialog.setCancelable(true);
                        mProgressDialog.show();

                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("product_id", fitems.get(position).getId())
                                .appendQueryParameter("town_stock", holder.town_num.getText().toString());
                        query = builder.build().getEncodedQuery();

                    }

                    @Override
                    protected Void doInBackground(Void... params) {

                        InputStream is = null;
                        try {

                            String url = server+"/set_town.php";
                            URL obj = new URL(url);
                            con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
                            con.setRequestProperty("Accept-Language", "UTF-8");
                            con.setDoOutput(true);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
                            outputStreamWriter.write(query);
                            outputStreamWriter.flush();
                            Log.e("pass 1", "connection success ");
                        } catch (Exception e) {
                            Log.e("Fail 1", e.toString());

                        }


                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String line;
                            StringBuffer sb = new StringBuffer();

                            while ((line = in.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            //   is.close();
                            resulta = sb.toString();
                            Log.e("pass 2", "connection success ");
                        } catch (Exception e) {
                            Log.e("Fail 2", e.toString());
                        }
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Void result) {


                        try {
                            json_data = new JSONObject(resulta);
                            int code = (json_data.getInt("code"));
                            if (code == 1) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Success");
                                alert.setMessage("Town Stock For "+fitems.get(position).getName()+" Set To "+holder.town_num.getText().toString());
                                alert.setIcon(R.drawable.suc);
                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        holder.town_num.setText("");
                                        //new AllProducts().fetchMovies();
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                            }
                            else if (code == 2) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Error");
                                alert.setMessage("You Tried Moving more than you have in Store");
                                alert.setIcon(R.drawable.error);
                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                            }
                            else {
                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Submission failed!");
                                alert.setMessage("Retry later");
                                alert.setIcon(R.drawable.error);

                                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(@NonNull DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }


                                });
                                alert.show();

                            }
                        } catch (Exception e) {
                            Log.e("Fail 3", e.toString());

                        }

                        mProgressDialog.dismiss();


                    }
                }
                new SetTown().execute();
            }
        });
        holder.set_store_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final class SetHardware extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected void onPreExecute() {


                        super.onPreExecute();

                        mProgressDialog = new ProgressDialog(context);
                        mProgressDialog.setMessage("Setting Store Stock..A moment please");
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        mProgressDialog.setCancelable(true);
                        mProgressDialog.show();

                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("product_id", fitems.get(position).getId())
                                .appendQueryParameter("hardware_stock", holder.store_num.getText().toString());
                        query = builder.build().getEncodedQuery();

                    }

                    @Override
                    protected Void doInBackground(Void... params) {

                        InputStream is = null;
                        try {

                            String url = server+"/set_hardware.php";
                            URL obj = new URL(url);
                            con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
                            con.setRequestProperty("Accept-Language", "UTF-8");
                            con.setDoOutput(true);
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(con.getOutputStream());
                            outputStreamWriter.write(query);
                            outputStreamWriter.flush();
                            Log.e("pass 1", "connection success ");
                        } catch (Exception e) {
                            Log.e("Fail 1", e.toString());

                        }


                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String line;
                            StringBuffer sb = new StringBuffer();

                            while ((line = in.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            //   is.close();
                            resulta = sb.toString();
                            Log.e("pass 2", "connection success ");
                        } catch (Exception e) {
                            Log.e("Fail 2", e.toString());
                        }
                        return null;
                    }


                    @Override
                    protected void onPostExecute(Void result) {


                        try {
                            json_data = new JSONObject(resulta);
                            int code = (json_data.getInt("code"));
                            if (code == 1) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Success");
                                alert.setMessage("Store Stock For "+fitems.get(position).getName()+" Set To "+holder.store_num.getText().toString());
                                alert.setIcon(R.drawable.suc);
                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        holder.store_num.setText("");
                                        //new AllProducts().fetchMovies();
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                            }
                            else if (code == 2) {

                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Error");
                                alert.setMessage("You Tried Moving more than you have in Store");
                                alert.setIcon(R.drawable.error);
                                alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }
                                });
                                alert.show();
                            }
                            else {
                                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                LinearLayout lila1 = new LinearLayout(context);
                                lila1.setOrientation(LinearLayout.VERTICAL);
                                alert.setView(lila1);
                                alert.setTitle("Submission failed!");
                                alert.setMessage("Retry later");
                                alert.setIcon(R.drawable.error);

                                alert.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(@NonNull DialogInterface dialog, int whichButton) {
                                        dialog.cancel();
                                    }


                                });
                                alert.show();

                            }
                        } catch (Exception e) {
                            Log.e("Fail 3", e.toString());

                        }

                        mProgressDialog.dismiss();


                    }
                }
                new SetHardware().execute();
            }
        });
//        RelativeLayout itemlist = (RelativeLayout) convertView.findViewById(R.id.main);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;

    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {
        public TextView name, type,amount,date;
        public ImageView user_image;
        Button move_town_btn,set_town_btn,set_store_btn;
        EditText move_num,town_num,store_num;



    }

    private class NameFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Product> list = original;

            int count = list.size();
            final ArrayList<Product> nlist = new ArrayList<Product>(count);

            Product filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.getName().toLowerCase().contains(filterString)||filterableString.getDescription().toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fitems = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }

    }


}







    
    	
    

