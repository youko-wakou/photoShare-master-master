package store.photo.share.project.photoshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

/**
 * Created by appu2 on 2018/03/24.
 */

    public class LoginActivity extends AppCompatActivity {
        private static final int RC_SIGN_IN = 9001;
        private Button googleBt;
        private GoogleApiClient mGoogleApiCilent;
        private FirebaseAuth mAuth;
        private FirebaseUser currentUser;
        private Snackbar loginErrorSnack;
        private RelativeLayout login_layout;
        private Intent UserPageIntent;
        private Button account_bt;
        private OnCompleteListener<AuthResult> CreateAccountListener;
        private EditText mail_Edit;
        private EditText pass_Edit;
        private String mail_string;
        private String pass_string;
        private Snackbar EditErrorSnack;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setTitle("アカウント作成");

        mAuth = FirebaseAuth.getInstance();
//        ================入力エラーのスナックバー============================
        login_layout = findViewById(R.id.login_layout);
        EditErrorSnack = Snackbar.make(login_layout,"入力に誤りがあります",Snackbar.LENGTH_LONG);
//        ====================================================
        CreateAccountListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserPageIntent = new Intent(LoginActivity.this, UserActivity.class);
                    startActivity(UserPageIntent);
                }else{
                    Log.d("ログイン処理","ログイン処理に失敗しました");
                }
            }
        };

        account_bt = findViewById(R.id.account_button);
        mail_Edit = findViewById(R.id.email_Text);
        pass_Edit = findViewById(R.id.pass_Text);

        account_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Log.d("ボタンが押された","押された");
                mail_string = mail_Edit.getText().toString();
                pass_string = pass_Edit.getText().toString();
                if (mail_string.length() == 0 && pass_string.length() < 6) {
                    Log.d("ボタンが押された","入力エラー");
                    EditErrorSnack.show();
                } else {
                    Log.d("ボタンが押された","アカウント作成");
//                    アカウント作成
                    mAuth.createUserWithEmailAndPassword(mail_string, pass_string).addOnCompleteListener(CreateAccountListener);

                }
            }
        });


//        mAuth = FirebaseAuth.getInstance();
////        スナックバー作成
//        login_layout = findViewById(R.id.login_layout);
//        loginErrorSnack = Snackbar.make(login_layout,"ログインに失敗しました",Snackbar.LENGTH_LONG);
//
//        //                googleアプリ統合
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
//                .requestIdToken("477970037331-fdfg2an95ie3kmstf8ttvvjq27fto70i.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
////        googleボタンワンクリック
//        googleBt = findViewById(R.id.googleButton);
//        googleBt.setOnClickListener(new View.OnClickListener(){
//            public void onClick(View v){
////                ログイン
//                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiCilent);
//                startActivityForResult(signInIntent,RC_SIGN_IN);
//            }
//        });

    }

//    @Override
//    public void onActivityResult(int requestCode,int resultCode,Intent data){
//        super.onActivityResult(requestCode,resultCode,data);
//
//        if(requestCode == RC_SIGN_IN){
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//
//            if(result.isSuccess()){
////                ログイン成功
//                GoogleSignInAccount account = result.getSignInAccount();
//                firebaseAuthWithGoogle(account);
//
//            }else{
////                ログイン失敗
//                loginErrorSnack.show();
//            }
//        }
//
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
//        Log.d("ログイン処理","firebaseAuthWithGoogle:" + acct.getId());
//        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
//        mAuth.signInWithCredential(credential).addOnCompleteListener(this,new OnCompleteListener<AuthResult>(){
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task){
//                if(task.isSuccessful()){
//                    Log.d("ログイン処理","signInWithCredential:success");
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    UserPageIntent = new Intent(LoginActivity.this,UserActivity.class);
//                    startActivity(UserPageIntent);
//                }else{
//                    Log.d("ログイン処理","signInWithCredential:failure",task.getException());
//                    loginErrorSnack.show();
//                }
//            }
//        });
//    }
}
