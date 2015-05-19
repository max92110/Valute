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
import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import com.example.max.valute.DbHelper;
import com.example.max.valute.Db;


public class MainActivity extends ActionBarActivity {
    public static final String EUR = "R01239";
    public static final String USD = "R01235";
    public static final String TAG = "Network Connect";
    String strtoday, strvalute, strvalue, strDateIfNull;
    String arrayValute[] = {USD, EUR};
    TextView tvEuro, tvUSD;
    Button bRefresh;
    Integer indexValute = 0;
    Db db;
    String LOG_TAG = "MainProgValute";
    Calendar today;
    SimpleDateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        today = Calendar.getInstance();
        df = new SimpleDateFormat("dd/MM/yyyy");
        strtoday = df.format(today.getTime());

        tvEuro = (TextView) findViewById(R.id.tvEuro);
        tvUSD = (TextView) findViewById(R.id.tvUSD);
        bRefresh = (Button) findViewById(R.id.bRefresh);
        db = new Db(this);

        RefreshTv();
        ParseValute(0);
    }

    public void bRefreshClick(View view) {
        //ParseValute(0);
        //List<Valute> valutes = db.getAllRows();
        //for (Valute valute : valutes ) {
        //    Log.d(LOG_TAG, "Id " + valute.getId() + ",Дата " + valute.getData() + ", Валюта " + valute.getValute() + ", Значение " + valute.getValue());
        //}
    }

    public void bUSDClick(View view) {
        Intent intent = new Intent(this, ListValue.class);
        intent.putExtra("valute", USD);
        startActivity(intent);
    }

    public void bEURClick(View view) {
        Intent intent = new Intent(this, ListValue.class);
        intent.putExtra("valute", EUR);
        startActivity(intent);
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

            //bRefresh.setVisibility(View.INVISIBLE);
        }


        @Override
        protected void onPostExecute(String result) {
            //bRefresh.setVisibility(View.VISIBLE);
            Log.i(TAG, result);
            String value = result;
            if (value != "Connection error" && value != "") { // Если ошибка
                db.insertRow(strtoday, strvalute, value);
                }
            if (value != "") { //Если выходной, взять прошлый день.
                today.add(Calendar.DAY_OF_YEAR,-1);
                strDateIfNull = df.format(today.getTime());
                Log.d(LOG_TAG, strtoday);
                today.add(Calendar.DAY_OF_YEAR, 1);
                value = db.getValue(strDateIfNull, strvalute);
                db.insertRow(strtoday, strvalute, value);
            }
            indexValute = indexValute + 1;
            RefreshTv();
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
            if (db.getValue(strtoday, strvalute) == null) {
                new DownloadTask().execute("http://cbr.ru/scripts/XML_dynamic.asp?date_req1=" + strtoday + "&date_req2=" + strtoday + "&VAL_NM_RQ=" + strvalute);
            } else {
                indexValute = indexValute + 1;
            }
    }
    }

    protected void RefreshTv(){
        tvEuro.setText(db.getValue(strtoday,EUR));
        tvUSD.setText(db.getValue(strtoday,USD));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

}