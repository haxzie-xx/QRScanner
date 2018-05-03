package me.haxzie.qrreader;

import android.app.ProgressDialog;
import android.graphics.PointF;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener{

    private QRCodeReaderView qrCodeReaderView;
    private TextView qrcode;
    private FloatingActionButton fab;
    private String code;
    private ImageButton cancelButton;
    private String REGISTRATION_URL = "";
    private final OkHttpClient client = new OkHttpClient();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new ProgressDialog(this);

        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrcode = findViewById(R.id.qr_code);
        fab = findViewById(R.id.fab);
        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrcode.setText("");
                code = "";
                fab.hide();
            }
        });
        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                //register him
                if (code != null && !code.equals("") && code.length() > 4)
                    try {
                        //show a loading dialog
                        dialog.setTitle("Registering "+code);
                        dialog.setCancelable(true);
                        dialog.show();
                        registerThisGuy(code);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                Toast.makeText(MainActivity.this, code, Toast.LENGTH_LONG).show();
                code = "";
                qrcode.setText("");
            }
        });

        qrCodeReaderView.setOnQRCodeReadListener(this);
        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(false);

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        code = text;
        qrcode.setText(text);
        if (!fab.isShown())
            fab.show();
    }

    public void registerThisGuy(String Code) throws Exception{
        RequestBody formBody = new FormBody.Builder()
                .add("code", code)
                .build();
        Request request = new Request.Builder()
                .url(REGISTRATION_URL)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            dialog.dismiss();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            //got the response
            Log.i("QRCODE", response.body().string());
        }
    }

    @Override
    public void onBackPressed() {
        if (dialog.isShowing())
            dialog.dismiss();
        else
            super.onBackPressed();
    }
}
