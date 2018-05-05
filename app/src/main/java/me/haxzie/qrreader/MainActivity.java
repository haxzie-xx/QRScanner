package me.haxzie.qrreader;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
    OnQRCodeReadListener, OnDismissListener, View.OnClickListener {

  private static final String TAG = "MainActivity";
  private QRCodeReaderView qrCodeReaderView;
  private EditText qrCodeEditText;
  private FloatingActionButton fab;
  private ImageButton clearTextButton;

  private MaterialDialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    qrCodeReaderView = findViewById(R.id.qrdecoderview);
    qrCodeEditText = findViewById(R.id.qr_code);
    fab = findViewById(R.id.fab);
    clearTextButton = findViewById(R.id.btn_clear_text);

    /* Build Progress Dialog */
    MaterialDialog.Builder builder = new Builder(this)
        .title("Verifying...")
        .progress(true, 0);
    progressDialog = builder.build();

    clearTextButton.setOnClickListener(this);
    fab.setOnClickListener(this);

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
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.btn_clear_text:
        qrCodeEditText.setText("");
        break;
      case R.id.fab:
        validate();

        break;
    }
  }

  private void validate() {
    if (!TextUtils.isEmpty(qrCodeEditText.getText())) {
      String code = qrCodeEditText.getText().toString().toUpperCase();
      if (code.length() < 6) {
        Toast.makeText(this, "Invalid Code!", Toast.LENGTH_SHORT).show();
      } else {
        // TODO: use this method after JSON model change
        // register(code);

        VerificationHandler.verifyCode(MainActivity.this, code, progressDialog);
      }
    }
  }

  @Override
  public void onQRCodeRead(String text, PointF[] points) {
    qrCodeEditText.setText(text);
    if (!fab.isShown()) {
      fab.show();
    }
  }


  @Override
  public void onBackPressed() {
    finishAffinity();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (qrCodeReaderView != null) {
      qrCodeReaderView.startCamera();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (qrCodeReaderView != null) {
      qrCodeReaderView.stopCamera();
    }
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    if (qrCodeReaderView != null) {
      qrCodeReaderView.startCamera();
    }
  }

}
