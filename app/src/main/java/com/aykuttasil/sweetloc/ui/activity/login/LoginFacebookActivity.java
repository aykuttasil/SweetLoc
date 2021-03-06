package com.aykuttasil.sweetloc.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.aykuttasil.sweetloc.R;
import com.aykuttasil.sweetloc.ui.activity.base.BaseActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.logger.Logger;

public class LoginFacebookActivity extends BaseActivity {

  private FirebaseAuth.AuthStateListener mAuthListener;
  private FirebaseAuth mAuth;
  private CallbackManager mCallbackManager;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_facebook_layout);
    mCallbackManager = CallbackManager.Factory.create();
    mAuth = FirebaseAuth.getInstance();
    initAuthListener();
  }

  private void initAuthListener() {
    mAuthListener =
        firebaseAuth -> {
          FirebaseUser user = firebaseAuth.getCurrentUser();
          if (user != null) {
            Logger.d("onAuthStateChanged:signed_in:" + user.getUid());
          } else {
            Logger.d("onAuthStateChanged:signed_out");
          }
        };
  }

  public void initializeAfterViews() {
    // initToolbar();
    initFacebookLoginButton();
  }

  private void initFacebookLoginButton() {

    /*
    LoginButton.setReadPermissions("email", "public_profile", "user_friends");
    LoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
        @DebugLog
        @Override
        public void onSuccess(LoginResult loginResult) {
            Logger.d("facebook:onSuccess:" + new Gson().toJson(loginResult));
            handleFacebookAccessToken(loginResult.getAccessToken());
        }

        @DebugLog
        @Override
        public void onCancel() {
            Logger.d("facebook:onCancel");
        }

        @DebugLog
        @Override
        public void onError(FacebookException error) {
            Logger.e(error, "facebook:onError");
            SweetLocHelper.CrashlyticsError(error);
        }
    });
    */

  }

  @Override
  public void onStart() {
    super.onStart();
    mAuth.addAuthStateListener(mAuthListener);
  }

  public void handleFacebookAccessToken(AccessToken token) {
    Logger.d("handleFacebookAccessToken:" + token);
    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

    mAuth
        .signInWithCredential(credential)
        .addOnCompleteListener(
            this,
            task -> {
              Logger.i("signInWithCredential:onComplete:" + task.isSuccessful());
              // If sign in fails, display a message to the user. If sign in succeeds
              // the auth state listener will be notified and logic to handle the
              // signed in user can be handled in the listener.
              if (!task.isSuccessful()) {
                // SweetLocHelper..CrashlyticsError(task.getException());
                Logger.e(task.getException(), "HATA");
                Toast.makeText(
                        LoginFacebookActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                    .show();
              } else {
                // task.getResult().getAdditionalUserInfo().getProfile()
                // takeSweetLocToken(task.getResult().getUser());
              }
            });
  }

  //    private void takeSweetLocToken(FirebaseUser user) {
  //        View tokenView =
  // LayoutInflater.from(this).inflate(R.layout.custom_view_give_token_layout, null);
  //        MaterialDialog dialog = new MaterialDialog.Builder(this)
  //                .title("SweetLoc İzleme Anahtarı")
  //                .customView(tokenView, true)
  //                .cancelable(false)
  //                .build();
  //
  //        tokenView.findViewById(R.id.DevamEt).setOnClickListener(view -> {
  //            EditText tokentext = tokenView.findViewById(R.id.EditTextTrackerId);
  //            if (!SweetLocHelper.validateIsEmpty(tokentext)) {
  //                dialog.dismiss();
  //                /*
  //                ModelUser modelUser = new ModelUser();
  //                modelUser.setUUID(user.getUid());
  //                modelUser.setEmail(user.getEmail());
  //                modelUser.setParola(user.getProviderId());
  //                modelUser.setToken(tokentext.getText().toString());
  //                modelUser.setImageUrl(user.getPhotoUrl().toString());
  //                modelUser.save();
  //
  //                FirebaseDatabase.getInstance().getReference()
  //                        .child(ModelUser.class.getSimpleName())
  //                        .child(user.getUid())
  //                        .setValue(modelUser);
  //                        */
  //
  //                //MainActivity_.intent(this).start();
  //                finish();
  //            }
  //        });
  //
  //        dialog.show();
  //
  //    }

  @Override
  public void onBackPressed() {
    moveTaskToBack(true);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    mCallbackManager.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mAuthListener != null) {
      mAuth.removeAuthStateListener(mAuthListener);
    }
  }
}
