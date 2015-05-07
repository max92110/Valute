package com.example.max.valute;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;


public class MainActivity extends ActionBarActivity {
    public static final String EUR = "R01239";
    public static final String USD = "R01235";
    public static final String TAG = "Network Connect";
    String strtoday, strvalute;
    String arrayValute[] = {USD, EUR};
    TextView tvEuro, tvUSD;
    Button bRefresh;
    Integer indexValute = 0;
    DBHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar today = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        strtoday = df.format(today.getTime());

        tvEuro = (TextView) findViewById(R.id.tvEuro);
        tvUSD = (TextView) findViewById(R.id.tvUSD);
        bRefresh = (Button) findViewById(R.id.bRefresh);
        dbhelper = new DBHelper(this);
        ParseValute(0);
    }

    public void bRefreshClick(View view) {
        ParseValute(0);
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(urls[0]);
            } catch (IOException e) {
                return getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPreExecute() {
            bRefresh.setVisibility(View.INVISIBLE);
        }


        @Override
        protected void onPostExecute(String result) {
            bRefresh.setVisibility(View.VISIBLE);
            Log.i(TAG, result);
            //Work with BD
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = dbhelper.getWritableDatabase();
            cv.put("date", strtoday);
            cv.put("Val", strvalute);
            cv.put("Value", result);
            db.insert("valuteTable", null, cv);
            dbhelper.close();

            switch (strvalute){
                case EUR:
                    tvEuro.setText(result);
                    break;
                case USD:
                    tvUSD.setText(result);
                    break;
            }
            indexValute = indexValute + 1;
            ParseValute(indexValute);

        }


        private String loadFromNetwork(String urlString) throws IOException {
            tvEuro = (TextView) findViewById(R.id.tvEuro);
            InputStream stream = null;
            String str = "";

            try {
                stream = downloadUrl(urlString);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(stream);
                doc.getDocumentElement().normalize();

//Считывание первого тега
                NodeList nList = doc.getElementsByTagName("Record");
                for (int i = 0; i < nList.getLength(); i++) {
                    Node nNode = nList.item(i);
                    org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
//Запись в строку текста у тега Value
                    String Eur = eElement.getElementsByTagName("Value").item(0).getTextContent();
//Считывание второго тега
                    str = str + " " + Eur;

                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
            return str;
        }

        private InputStream downloadUrl(String urlString) throws IOException {
            // BEGIN_INCLUDE(get_inputstream)
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Start the query
            conn.connect();
            InputStream stream = conn.getInputStream();
            return stream;
            // END_INCLUDE(get_inputstream)
        }

    }

    //Проходит по массиву с типами валют и запрашивает для каждой курс
    protected void ParseValute(Integer indexValute){
        if (indexValute < arrayValute.length) {
            strvalute = arrayValute[indexValute];
            new DownloadTask().execute("http://cbr.ru/scripts/XML_dynamic.asp?date_req1=" + strtoday + "&date_req2=" + strtoday + "&VAL_NM_RQ=" + strvalute);
        }
    }
    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            Log.d("DB","--onCreate database");
            db.execSQL("create table valuteTable ("
                    + "id integer primary key autoincrement,"
                    + "date,"
                    + "Val," + "Value" + ");");
        }
        @Override
        public void onUpgrade (SQLiteDatabase db, int oldVesion, int newVersion){

        }
    }

}