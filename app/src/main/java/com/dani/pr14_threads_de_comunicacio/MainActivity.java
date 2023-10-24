package com.dani.pr14_threads_de_comunicacio;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String error = "";
        Button searchButton = (Button) findViewById(R.id.search);
        TextView mostrar = (TextView) findViewById(R.id.text);
        ImageView  randomFoto = (ImageView) findViewById(R.id.foto);
        Random r = new Random();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("miapp", "hooooooooooooooooooooooooo");
                ExecutorService executor = Executors.newSingleThreadExecutor();

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        int w = r.nextInt(400)+100;
                        int h = r.nextInt(400)+100;
                        String ip = getDataFromUrl("https://animechan.xyz/api/random", error);
                        Log.i("MiApp",ip);
                        String f = "https://placekitten.com/"+Integer.toString(w)+"/"+Integer.toString(h);
                        Bitmap bm = null;

                        try {
                            InputStream in = new java.net.URL(f).openStream();
                            bm = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                            e.printStackTrace();
                        }

                        // Tasques en background (xarxa)

                        Handler handler = new Handler(Looper.getMainLooper());
                        Bitmap finalBm = bm;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                randomFoto.setImageIcon(Icon.createWithBitmap(finalBm));
                                // Tasques a la interfície gràfica (GUI)
                                mostrar.setText(ip);

                            }
                        });
                    }
                });
            }
        });
    }

    private String getDataFromUrl(String dataUrl, String error) {

        String result = null;
        int resCode;
        InputStream in;
        try {
            URL url = new URL(dataUrl);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();
            resCode = httpsConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpsConn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        in, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                in.close();
                result = sb.toString();
            } else {
                error += resCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}