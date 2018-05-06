package me.haxzie.qrreader;

import android.Manifest.permission;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

public class MainActivity extends AppCompatActivity implements
    OnQRCodeReadListener, OnDismissListener, View.OnClickListener {

  public static final int PERMISSION_REQ_CODE = 44;
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

    qrCodeReaderView.setOnQRCodeReadListener(null);

    if (ContextCompat.checkSelfPermission(this, permission.CAMERA)
        == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(this, new String[]{permission.CAMERA}, PERMISSION_REQ_CODE);
    }

    setUpCamera();


    /* Build Progress Dialog */
    MaterialDialog.Builder builder = new Builder(this)
        .title("Verifying...")
        .progress(true, 0);
    progressDialog = builder.build();

    clearTextButton.setOnClickListener(this);
    fab.setOnClickListener(this);

  }

  private void setUpCamera() {
    qrCodeReaderView.setOnQRCodeReadListener(this);
    // Use this function to enable/disable decoding
    qrCodeReaderView.setQRDecodingEnabled(true);

    // Use this function to change the autofocus interval (default is 5 secs)
    qrCodeReaderView.setAutofocusInterval(2000L);

    // Use this function to enable/disable Torch
    qrCodeReaderView.setTorchEnabled(false);

    // Use this function to set back camera preview
    qrCodeReaderView.setBackCamera();

    qrCodeReaderView.startCamera();
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
        //         register(code);
        VerificationHandler.verifyCode(MainActivity.this, code, progressDialog);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == PERMISSION_REQ_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // permission granted
        setUpCamera();
      } else {
        Toast.makeText(this, "Camera Permissions are Denied!", Toast.LENGTH_SHORT).show();
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
    qrCodeReaderView.startCamera();
  }

  @Override
  protected void onPause() {
    super.onPause();
    qrCodeReaderView.stopCamera();

  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    qrCodeReaderView.setOnQRCodeReadListener(this);
    qrCodeReaderView.startCamera();
  }
}
