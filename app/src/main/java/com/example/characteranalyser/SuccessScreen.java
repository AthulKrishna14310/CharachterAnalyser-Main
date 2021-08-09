package com.example.characteranalyser;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SuccessScreen extends AppCompatActivity {
    private static final String TAG = "SuccessScreen";
    private String id;
    private TextView score;
    private TextView speed;
    private TextView time;
    private TextView ne;
    private ArrayList<String>aList=new ArrayList<>();
    private ListView errorList;
    private TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_screen);
        getSupportActionBar().hide();
        id=getIntent().getStringExtra("ID");
        score=findViewById(R.id.scoreTextView);
        speed=findViewById(R.id.speed);
        time=findViewById(R.id.timeTaken);
        ne=findViewById(R.id.numberOfErrors);
        result=findViewById(R.id.textViewEx);
        errorList=findViewById(R.id.errorList);
        errorList.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("Attempts")
                .document(id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Attempt attempt=documentSnapshot.toObject(Attempt.class);
                score.setText("Score : "+attempt.getScore()+"/50");
                ne.setText("No of errors : "+attempt.getNe());
                speed.setText("Tracing speed : "+attempt.getSpeed());
                time.setText("Time Taken : "+attempt.getTime());
                aList.addAll(attempt.getScreens());
                showLog("Size= "+aList.size()+"");
                showLog(id);
                if(aList.size()!=0) {
                    CustomAdapter customAdapter = new CustomAdapter(SuccessScreen.this, aList);
                    errorList.setAdapter(customAdapter);
                    }
                if(Integer.parseInt(attempt.getScore())>40){
                        result.setText(R.string.wow);
                    }else if(Integer.parseInt(attempt.getScore())<40&&Integer.parseInt(attempt.getScore())>30){
                        result.setText(R.string.average);
                    }else if(Integer.parseInt(attempt.getScore())<30&&Integer.parseInt(attempt.getScore())>20){
                        result.setText(R.string.failed);
                        result.setTextColor(Color.RED);
                    }
                }
        });

        ne.setOnClickListener(v -> {
            showLog(ne.getX()+" "+ne.getY());
            ObjectAnimator animation = ObjectAnimator.ofFloat(ne, "translationY", -464f);
            animation.setDuration(300);
            animation.start();
            ObjectAnimator animation2 = ObjectAnimator.ofFloat(score, "translationX", +56f);
            animation2.setDuration(300);
            animation2.start();
            animation2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    showLog("Animation End");
                    displayList();
                }
            });

        });
        findViewById(R.id.homeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void displayList() {
        errorList.setVisibility(View.VISIBLE);
    }

    private void showLog(String data){
        Log.d(TAG, data);

    }
}