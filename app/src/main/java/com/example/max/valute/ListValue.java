package com.example.max.valute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.max.valute.Valute;
import com.example.max.valute.Db;
import java.util.List;

/**
 * Created by 1 on 18.05.2015.
 */
public class ListValue extends ActionBarActivity {
    String strValute;
    String LOG_TAG = "ListValue Program";
    String[] arrayValute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_value);
        Intent intent = getIntent();
        strValute = intent.getStringExtra("valute");
        Db db = new Db(this);
        List<Valute> valutes = db.getAllRows(DbHelper.VALUTE, strValute);
        int index = 0;
        arrayValute = new String[valutes.size()];
        for (Valute valute : valutes ) {
                arrayValute[index] = "Id " + valute.getId() + ", Дата: " + valute.getData() + ", Валюта: " + valute.getValute() + ", Значение: " + valute.getValue();
                Log.d(LOG_TAG, arrayValute[index]);
                index = index + 1;
        }
        ListView lvValute = (ListView) findViewById(R.id.lvValute);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, arrayValute);
        lvValute.setAdapter(adapter);

    }
    public void bBackClick(View view) {
        finish();
    }
}
