package store.photo.share.project.photoshare;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Matrix;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.internal.api.FirebaseNoSignedInUserException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by appu2 on 2018/03/15.
 */

public class UserActivity extends AppCompatActivity {
    private int CHOOSER_REQUEST_CODE = 100;
    private int PERMISSIONS_REQUEST_CODE = 100;
    private Uri PictureUri;
    private FirebaseAuth auth;
    private FirebaseUser current_user;
    private ImageView userImage;
    private ProgressDialog imageDialog;
    private Button ProfileButton;
    private EditText nameEdit;
    private String nameString;
    private DatabaseReference Database;
    private DatabaseReference PROFILEref;
    private ProgressDialog progressKousin;
    private Intent Loginintent;
    private Intent ThisPageIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);auth = FirebaseAuth.getInstance();
        current_user = auth.getCurrentUser();
        if(current_user == null){
            Loginintent = new Intent(this,LoginActivity.class);
            startActivity(Loginintent);
        }

        setTitle("ユーザー画面");


//        ==================================================================================================
//============================プログレスダイアログ============================================================
        progressKousin = new ProgressDialog(this);
        progressKousin.setTitle("プロフィール");
        progressKousin.setMessage("保存中…");
//        ========================================================================================================
        userImage = findViewById(R.id.userImage);
    userImage.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             if (view == userImage) {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                         showChooser();
                     } else {
                         requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                         return;
                     }
                 } else {
                     showChooser();
                 }
             }
         }
     });
        imageDialog = new ProgressDialog(this);
        imageDialog.setMessage("画像投稿…");
//   =========================名前保存=================================================================
        nameEdit = findViewById(R.id.editName);
        ProfileButton = findViewById(R.id.kousin);
        ProfileButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                nameString = nameEdit.getText().toString();

//                名前が入力されているか確認する
                if(nameString.length() ==0){
//                    キーボードをしまう
                    InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
//                    名前が入力されていなかったら
                    Snackbar.make(v,"名前を入力してください",Snackbar.LENGTH_LONG).show();
                }
//                画像が選択されていない場合
                BitmapDrawable drawable = (BitmapDrawable)userImage.getDrawable();
                if(drawable == null){
                    Snackbar.make(v,"画像を選択してください",Snackbar.LENGTH_LONG).show();
                }
//                =＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝ここから保存処理＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
                Map<String,String> data = new HashMap<>();
                data.put("name",nameString);
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
                String bitmapString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                data.put("image",bitmapString);
//                =================型名チェック===================================================
                Log.d("name型名",nameString.getClass().toString());
                Log.d("image型名",bitmapString.getClass().toString());
                Log.d("name",nameString);
                Log.d("image",bitmapString);
//                ==================================================================
//        ===================データベースに保存================================================================
                Database = FirebaseDatabase.getInstance().getReference();
                Database.child(Const.profileKey).child(current_user.getUid()).setValue(data);
                progressKousin.show();
                ThisPageIntent = new Intent(UserActivity.this,UserActivity.class);
                startActivity(ThisPageIntent);
//                ==================================================================================================
            }
        });

        Database.child(Const.profileKey).child(current_user.getUid()).addChildEventListener(childEventListener);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == CHOOSER_REQUEST_CODE){
            if(resultCode !=  RESULT_OK) {
                if (PictureUri != null) {
                    getContentResolver().delete(PictureUri, null, null);
                    PictureUri = null;
                }
                return;
            }
        }
        Uri uri = (data == null || data.getData() == null) ? PictureUri : data.getData();

        Bitmap image;
        try{
            ContentResolver contentresolver = getContentResolver();
            InputStream inputstream = contentresolver.openInputStream(uri);
            image = BitmapFactory.decodeStream(inputstream);
            inputstream.close();
        }catch(Exception e){
            return;
        }

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        float scale = Math.min((float)500/imageWidth,(float)500/imageHeight);
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        Bitmap resizeImage = Bitmap.createBitmap(image,0,0,imageWidth,imageHeight,matrix,true);
        userImage.setImageBitmap(resizeImage);
        PictureUri = null;
    }
    private void showChooser(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        String filename = System.currentTimeMillis()+ ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,filename);
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        PictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,PictureUri);

        Intent ChooserIntent = Intent.createChooser(galleryIntent,"画像を取得");
        ChooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[]{cameraIntent});
        startActivityForResult(ChooserIntent,CHOOSER_REQUEST_CODE);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            HashMap<String,String> map = new HashMap<String,String>();
            map = (HashMap) dataSnapshot.getValue();
            String name = (String) map.get("name");
            String image = (String)map.get("image");
            byte[] imagebyte;
            if(image != null){
                imagebyte = Base64.decode(image,Base64.DEFAULT);
            }else{
                imagebyte = new byte[0];
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
}
