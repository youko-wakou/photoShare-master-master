package store.photo.share.project.photoshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser current_user;
    private Intent LoginIntent;
    private Intent UserIntent;
    private Button buttonUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonUser = findViewById(R.id.user);
        buttonUser.setVisibility(View.INVISIBLE);

        auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser();
        if (current_user == null){
            Log.d("スタート画面","ログインしていません");
            LoginIntent = new Intent(this,LoginActivity.class);
            startActivity(LoginIntent);
        }else{
            buttonUser.setVisibility(View.VISIBLE);
            buttonUser.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    userintentSET();
                }
            });
            Log.d("スタート画面","ログインしています");
        }
    }
    private void userintentSET(){
        UserIntent = new Intent(this,UserActivity.class);
        startActivity(UserIntent);
    }
}
