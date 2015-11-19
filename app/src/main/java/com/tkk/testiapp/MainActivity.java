package com.tkk.testiapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import java.util.List;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONParser jParser = new JSONParser(this);
        jParser.execute();
    }


    //class for (1)establish connection, (2)read input data and (3)prase data with json
    protected class JSONParser extends AsyncTask<String, Void, String> {

        //json object
        JSONObject jObj = null;

        Context mainContext;

        //web andress
        String webAddress = "https://api.myjson.com/bins/2vlmt";

        // construstor
        public JSONParser(Context context){
            mainContext=context;
        }

        @Override
        protected String doInBackground(String... params){

            URL myUrl;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            StringBuilder stringBuilder;
            String line = "";

            //1
            try{
                myUrl = new URL(webAddress);

                urlConnection = (HttpURLConnection) myUrl
                        .openConnection();

            } catch (IOException e){
                Log.e("TestiApp", e.getMessage());
            }

            //2

            try{
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                stringBuilder = new StringBuilder();

                line = null;
                while((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line + "\n");
                }

                line = stringBuilder.toString();

            } catch (IOException e){
                Log.e("Myapp", e.getMessage());
            }

            return line;
        }

        // 3
        protected void onPostExecute(String line){
            //(1) parse the string to a JSON object, (2) go through its data, (3) and output into to the user
            try {
                // 1
                jObj = new JSONObject(line);

                //2
                JSONArray users = jObj.getJSONArray("data");
                String course = "";
                String name = "";
                String date = "";
                String today="2014-09-08 21:00";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date thisDay;
                ExpandableListAdapter listAdapter;
                ExpandableListView expListView;
                List<String> listDataHeader = new ArrayList<String>();
                HashMap<String, List<String>> listDataChild = new HashMap<String,List<String>>();





                // for (int i = 0; i < users.length(); i++){
                for (int i = 1; i < 6; i++){
                    JSONObject c = users.getJSONObject(i);
                    //course += c.getString("fullName")+"\n";

                    JSONArray assignments = new JSONArray(users.getJSONObject(i).getString("assignments"));

                    for (int j = 0; j < assignments.length(); j++) {

                        JSONObject k = assignments.getJSONObject(j);
                        //name += k.getString("name")+"\n";
                        try {
                            thisDay=sdf.parse(today);
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTime(thisDay);
                            Date dateTime = sdf.parse(k.getString("dueDate"));
                            Calendar calendar2 = Calendar.getInstance();
                            calendar2.setTime(dateTime);
                            if(calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) && calendar1.get(Calendar.MONTH) ==
                                    calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DAY_OF_MONTH)  == calendar2.get(Calendar.DAY_OF_MONTH)) {
                                //date += sdf.format(dateTime)+"\n";
                                //name += k.getString("name")+"\n";
                                //course += c.getString("fullName")+"\n";
                                listDataHeader.add(c.getString("fullName"));
                                List <String> tmpList = new ArrayList<String>();
                                tmpList.add(k.getString("name"));
                                tmpList.add(k.getString("dueDate"));
                                listDataChild.put(c.getString("fullName"),tmpList);
                                String s = listDataHeader.get(0);

                            }
                        }
                        catch (ParseException e)
                        {}
                    }

                }

                //3
                /*TextView myQueryTV = (TextView) findViewById(R.id.assignment);
                myQueryTV.setText(course);

                TextView myNameTV = (TextView) findViewById(R.id.name);
                myNameTV.setText(name);

                TextView myDateTV = (TextView) findViewById(R.id.date);
                myDateTV.setText(date);*/

                // get the listview
                expListView = (ExpandableListView) findViewById(R.id.lvExp);
                // preparing list data
                listAdapter = new ExpandableListAdapter(mainContext,listDataHeader, listDataChild);
                // setting list adapter
                expListView.setAdapter(listAdapter);





            }catch (JSONException e){
                Log.e("JSON Parser", e.getMessage());
            }
        }
    }
}
