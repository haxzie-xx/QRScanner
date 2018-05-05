package me.haxzie.qrreader;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.Builder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationHandler {

  private static final String TAG = "VerificationHandler";

  static void verifyCode(final Context context, String barcode,
      final MaterialDialog progressDialog) {
    progressDialog.show();

    QRCodeVerificationService qrCodeVerificationService = RemoteApiServiceCreator
        .createService(QRCodeVerificationService.class);

    Call<String> stringCall = qrCodeVerificationService.verifyString(barcode);
    stringCall.enqueue(new Callback<String>() {
      @Override
      public void onResponse(Call<String> call, Response<String> response) {
        Log.d(TAG, "onResponse: " + call.request().url() + " " + response.body());
        progressDialog.dismiss();

        /* Create response dialog */
        final MaterialDialog.Builder status = new Builder(context)
            .title("Verification Status")
            .contentGravity(GravityEnum.CENTER);

        if (response.body().contains("already registered")) {
          status
              .content("Verified Registration!")
              .contentColor(ContextCompat.getColor(context, R.color.verified))
              .show();
        } else {
          status
              .content("Invalid User!")
              .contentColor(ContextCompat.getColor(context, R.color.error))
              .show();
        }
      }

      @Override
      public void onFailure(Call<String> call, Throwable t) {
        Log.e(TAG, "onFailure: " + call.request().url() + " " + t.getLocalizedMessage(), t);
        progressDialog.dismiss();

        new MaterialDialog.Builder(context)
            .title("Verification Status")
            .content("Some Error Occurred, Please try again!")
            .contentColor(ContextCompat.getColor(context, R.color.error))
            .show();
      }
    });
  }


  public static void register(String code) {


    Call<VerificationResponse> call = RemoteApiServiceCreator
        .createService(QRCodeVerificationService.class)
        .verifyCode(code);
    call.enqueue(new Callback<VerificationResponse>() {
      @Override
      public void onResponse(Call<VerificationResponse> call,
          Response<VerificationResponse> response) {

        // TODO: handle the response object
        Log.d(TAG, "onResponse: " + response.body().toString());


      }

      @Override
      public void onFailure(Call<VerificationResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: " + t.getLocalizedMessage(), t);


      }
    });

  }

}
