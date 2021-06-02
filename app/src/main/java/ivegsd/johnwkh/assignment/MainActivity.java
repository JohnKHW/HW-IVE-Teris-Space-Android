package ivegsd.johnwkh.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.plattysoft.leonids.ParticleSystem;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnStart, btnRank,btnQuit;
    private final int REQUEST_CODE = 4701;
    private final String TAG = "Debug.Log";
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private FirebaseFirestore db;
    private FirebaseFirestoreSettings settings;

    private MediaPlayer bgSound;
    private final float bgSoundVolume = 1f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        updateUI(mAuth.getCurrentUser());

        bgSound = MediaPlayer.create(this, R.raw.theme);
        bgSound.setLooping(true);
        bgSound.setVolume(bgSoundVolume, bgSoundVolume);
        bgSound.start();
        setContentView(R.layout.activity_main);
        btnStart = findViewById(R.id.btnStart);
        btnRank = findViewById(R.id.btnRank);
        btnQuit = findViewById(R.id.btnQuit);

        btnStart.setOnClickListener(this);
        btnRank.setOnClickListener(this);
        btnQuit.setOnClickListener(this);
        btnStart.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    particleEffect(v);
                }
                return false;
            }
        });
        btnRank.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    particleEffect(v);
                }
                return false;
            }
        });
        btnQuit.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    particleEffect(v);
                }
                return false;
            }
        });
    }
    private void particleEffect(View view){
        new ParticleSystem(this, 80, R.drawable.particles,  10000)
                .setSpeedRange(0.1f, 0.7f)
                .setAcceleration(0.00005f, 90)
                .oneShot(view, 80);
    }

    @Override
    public void onClick(View v) {
        if(v == btnStart) {
            bgSound.stop();
            Intent intent = new Intent(this, GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, REQUEST_CODE);
        } else if(v == btnRank) {
            Intent intent = new Intent(this, RankActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if(v == btnQuit) {
            bgSound.stop();
            mAuth.signOut();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == REQUEST_CODE)
        {
            //get score and save it
            try{
                int score = intent.getIntExtra("Score", 0);
                Log.d(TAG, "onActivityResult: " + score);
                dataHandle(score);
            }
            catch (Exception e){
                Log.w(TAG, e );
            }

        }
    }
    //update user
    private void updateUI(FirebaseUser user ){
        currUser = user;
        Log.d(TAG, "User Displace Name: "+ user.getDisplayName());
    }
    //handle the data whether update or create
    private void dataHandle(final int score){
        db.collection("RankData").document(currUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            checkUpdate(documentSnapshot.getData(), score);

                            Log.d(TAG, "*********" + "success Score");
                        }
                        else{
                            updateData(score);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, e );
            }
        });
    }
    //check the latest score whether the best score to update the score to firebase
    private void checkUpdate(Map document, int score){
        if(Integer.parseInt(document.get("score").toString()) < score){
            updateData(score);
        }
    }
    //update the data into firebase
    private void updateData(int score){
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", currUser.getDisplayName());
        userData.put("score", score);

        db.collection("RankData").document(currUser.getUid())
                .set(userData, SetOptions.merge());

    }

}
