package ivegsd.johnwkh.assignment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RankActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currUser;
    private FirebaseFirestore db;
    private FirebaseFirestoreSettings settings;
    private Map<String, Integer> rankData = new HashMap<>();
    private final String TAG = "Debug.Log";
    private ViewGroup list_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        list_item = findViewById(R.id.list_item);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        currUser = mAuth.getCurrentUser();
        Log.d(TAG, "Test1");

        db.collection("RankData")
                .orderBy("score", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 1;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        View v = getLayoutInflater().inflate(R.layout.rank_layout, null, false);
                        v.setId(count);
                        TextView rank = v.findViewById(R.id.tvRank);
                        TextView name = v.findViewById(R.id.tvName);
                        TextView score = v.findViewById(R.id.tvScore);
                        rank.setText(""+ count);
                        name.setText(document.get("name").toString());
                        score.setText(document.get("score").toString());
                        /*
                        if(count%2 != 0)
                            v.setBackgroundColor(getResources().getColor(R.color.rankOddColor));
                        else
                            v.setBackgroundColor(getResources().getColor(R.color.rankEvenColor));


                         */

                        list_item.addView(v);
                        if(document.getId().equals(currUser.getUid())){
                            TextView myrank = findViewById(R.id.myRank);
                            TextView myname = findViewById(R.id.myName);
                            TextView myscore = findViewById(R.id.myScore);
                            myrank.setText(""+ count);
                            myname.setText(document.get("name").toString());
                            myscore.setText(document.get("score").toString());
                        }
                        count++;
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        Log.d(TAG, "Test2");

        //db.collection("RankData").orderBy("score").toString();
    }
}
