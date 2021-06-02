package ivegsd.johnwkh.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.plattysoft.leonids.ParticleSystem;

public class login extends AppCompatActivity
{
    private TextView fbook,acc,sin,sup,sinnp;
    private EditText mal,pswd;
    private String TAG = "Debug.Log";
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        sup = (TextView)findViewById(R.id.sup);
        sin = (TextView)findViewById(R.id.sin);
        acc = (TextView)findViewById(R.id.act);
        mal = (EditText)findViewById(R.id.mal);
        pswd = (EditText)findViewById(R.id.pswd);
        sinnp = findViewById(R.id.sinnp);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);


        sup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(login.this, signup.class);
                //it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }
        });

        acc.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(login.this, signup.class);
                //it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
            }
        });

        sinnp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validUser();
            }
        });


    }

    private void validUser(){
        mAuth.signInWithEmailAndPassword(mal.getText().toString(), pswd.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(login.this , "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        this.currUser = user;
        if(user != null){
            Log.d(TAG, "User Name: "+ user.getDisplayName());
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        }

    }

}
