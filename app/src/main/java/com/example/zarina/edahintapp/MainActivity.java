package com.example.zarina.edahintapp;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {
    Button button;
    EditText listProds;
    SeekBar seekBar;
    ProgressBar progressBar;

    String alertMessage = "";

    private void alert(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        if (alertMessage != "") {
            builder.setTitle("Наш совет!");
            builder.setMessage("С таким списком продуктов советуем пойти вам в " + alertMessage);
        }
        else {
            builder.setTitle("Поздравляем!");
            builder.setMessage("Вы превзошли нас - мы не в силах дать вам совет");
        }
        AlertDialog alert = builder.create();
        alert.show();
        alertMessage = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        listProds = (EditText) findViewById(R.id.editText);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);

        button.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Get test = new Get(listProds, seekBar, MainActivity.this);
                try {
                    test.run("http://52.174.57.15:5000/get_short_hint", new ResponseCallBack() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                alertMessage = response;
                            } catch (Exception e){
                                System.out.println(e.getMessage());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e){
                    
                }
                progressBar.setVisibility(View.INVISIBLE);
                alert();
            }
        });
    }
}