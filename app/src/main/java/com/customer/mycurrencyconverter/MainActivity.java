package com.customer.mycurrencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    EditText fromInput, toOutput;
    Button convertButton;
    Spinner fromDropdown, toDropdown;
    String fromCurrency, toCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromInput = findViewById(R.id.fromCurrency);
        toOutput = findViewById(R.id.toCurrency);
        convertButton = findViewById(R.id.convertButton);
        fromDropdown = findViewById(R.id.fromDropdown);
        toDropdown = findViewById(R.id.toDropdown);

        toOutput.setEnabled(false);

        fromDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                fromCurrency =parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        toDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                toCurrency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInput()){

                    JSONFetch jsonFetch = new JSONFetch();
                    jsonFetch.execute(fromInput.getText().toString());
                }
            }
        });
    }

    public boolean checkInput(){

        if (fromInput.getText().toString().isEmpty()){
            Toast.makeText(this, "Please enter a value to convert ", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (fromCurrency.equalsIgnoreCase(toCurrency)){
            Toast.makeText(MainActivity.this, "from and to values are same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    class JSONFetch extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        String error = "", apiResponse = "";

        @Override
        protected Void doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://frankfurter.app/latest?amount=" + params[0] + "&from=" + fromCurrency + "&to=" + toCurrency).build();
            try {
                Response response = client.newCall(request).execute();
                apiResponse = response.body().string();
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Converting...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void v) {
            dialog.dismiss();
            if (!error.isEmpty()) {
                Toast.makeText(MainActivity.this, "Something went wrong "+ error, Toast.LENGTH_SHORT).show();
            }
            try {
                toOutput.setText(new JSONObject(apiResponse).getJSONObject("rates").getString(toCurrency));
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Something went wrong " + e.toString(), Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(v);
        }
    }
}