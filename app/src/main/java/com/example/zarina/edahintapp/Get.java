package com.example.zarina.edahintapp;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.SeekBar;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Get {
    SeekBar seekBar;
    EditText editText;
    OkHttpClient client = new OkHttpClient();
    MainActivity mainActivity;

    public static final MediaType JSON
            = MediaType.parse("application/json");

    public Get(EditText editText, SeekBar seekBar, MainActivity mainActivity) {
        this.editText = editText;
        this.seekBar = seekBar;
        this.mainActivity = mainActivity;
    }

    //Метод в котором происходит запрос к серверу
    public void run(final String url, final ResponseCallBack callback) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonString = generateJson(editText);
                String priceLevel = getPriceValue(seekBar);

                RequestBody formBody = RequestBody.create(JSON, "{\"products\":" + jsonString + ", \"price_level\": \"" + priceLevel + "\"}");

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    final String sResponse = decode(response.body().string());
                    callback.onResponse(sResponse);
                } catch (IOException e){
                    System.out.println(e.getMessage());
                }

            }
        }).start();
    }

    //Меняет кодировку с unicode на utf-8
    private String decode(String str) {
        str = str.replace("\\","");
        String[] arr = str.split("u");
        String text = "";
        for(int i = 1; i < arr.length; i++){
            try {
                int hexVal = Integer.parseInt(arr[i], 16);
                text += (char)hexVal;
            } catch (RuntimeException e){
                text += arr[i];
            }
        }
        return text;
    }

    //генерирует json список по списку продуктов пользователя
    private String generateJson(EditText editText){
        String text = editText.getText().toString();
        String[] listProds = text.split("\n");
        String jsonString = "[";
        int i = 0;
        for(String s : listProds){
            jsonString += "\"" + s + "\"";
            if (i != listProds.length - 1)
                jsonString += ", ";
            i++;
        }
        jsonString += "]";
        return jsonString;
    }

    private String getPriceValue(SeekBar seekBar){
        if(seekBar.getProgress() <= 50){
            return "low";
        }
        else {
            return "high";
        }
    }
}

