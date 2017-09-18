package com.example.zarina.edahintapp;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    Button button;
    EditText listProds;
    SeekBar seekBar;
    ProgressBar progressBar;
    DownloadTask downloadTask;

    private void alert(String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if (alertMessage != "") {
            builder.setTitle("Наш совет!");
            builder.setMessage("С таким списком продуктов советуем пойти вам в " + alertMessage);
        } else {
            builder.setTitle("Поздравляем!");
            builder.setMessage("Вы превзошли нас - мы не в силах дать вам совет");
        }
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        listProds = (EditText) findViewById(R.id.editText);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                downloadTask = new DownloadTask();
                downloadTask.execute("http://52.174.57.15:5000/get_short_hint");
            }
        });
    }


    class DownloadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            //Отображаем системный диалог загрузки
            MainActivity.this.progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            //В этом методе происходит загрузка картинки через
            //стандартный класс URLConnection
            String jsonString = generateJson(listProds);
            String priceLevel = getPriceValue(seekBar);
            OkHttpClient client = new OkHttpClient();

            MediaType JSON = MediaType.parse("application/json");

            RequestBody formBody = RequestBody.create(JSON, "{\"products\":" + jsonString + ", \"price_level\": \"" + priceLevel + "\"}");

            Request request = new Request.Builder()
                    .url(params[0])
                    .post(formBody)
                    .build();
            String sResponse = "";
            try {
                Response response = client.newCall(request).execute();
                sResponse = decode(response.body().string()).toUpperCase();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return sResponse;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //progressDialog.setProgress(progress[0]);
        }

        //Скроем диалог и покажем картинку
        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            alert(result);
        }

        // Меняет кодировку с unicode на utf-8
        private String decode(String rawstr) {
            String[] arrSpase = rawstr.split(" ");
            String res = "";
            for (String str : arrSpase) {
                str = str.replace("\\", "").replace("\"", "");
                String[] arr = str.split("u");
                String text = "";
                for (int i = 0; i < arr.length; i++) {
                    try {
                        int hexVal = Integer.parseInt(arr[i], 16);
                        text += (char) hexVal;
                    } catch (RuntimeException e) {
                        text += arr[i];
                    }
                }
                res += " " + text;
            }
            return res;
        }

        //генерирует json список по списку продуктов пользователя
        private String generateJson(EditText editText) {
            String text = editText.getText().toString().toLowerCase();
            String[] listProds = text.split("\n");
            String jsonString = "[";
            int i = 0;
            for (String s : listProds) {
                jsonString += "\"" + s + "\"";
                if (i != listProds.length - 1)
                    jsonString += ", ";
                i++;
            }
            jsonString += "]";
            return jsonString;
        }

        private String getPriceValue(SeekBar seekBar) {
            if (seekBar.getProgress() <= 50) {
                return "low";
            } else {
                return "high";
            }
        }
    }
}