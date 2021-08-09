package com.example.characteranalyser;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.INotificationSideChannel;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tracingBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView studentName;
    private TextView studentClass;
    private String currentUser;
    private ListView listView;
    private Spinner letterSpinner;
    private Spinner categorySpinner;
    private TextView attemptsTitle;
    private TextView subtitle;
    private ProgressBar loadProgress;
    private TextView loadText;
    private GraphView graph;
    private GridLabelRenderer gridLabel;
    private FirebaseFirestore firebaseFirestore;
    private String TAG="Main";
    private ArrayList<Attempt> attempts;
    private String selectedLetter ="All";
    private String selectedCategory ="Score";
    private LineGraphSeries<DataPoint> series;
    private static final int SCORE_TOTAL=100;
    private  ArrayList<String> arrayList=new ArrayList<>();
    private ArrayAdapter attemptsAdapter;
    private MaterialButton logoutButton;
    private TextView school;
    private MaterialButton decodeButton;
    private ArrayList<String>idLists=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        getSupportActionBar().hide();
        decodeButton=findViewById(R.id.decode_data);
        studentName = findViewById(R.id.student_name);
        studentClass = findViewById(R.id.student_class);
        school=findViewById(R.id.student_school);
        tracingBtn = findViewById(R.id.tracing_btn);
        logoutButton=findViewById(R.id.logout_btn);
        loadProgress =findViewById(R.id.loadProgress);
        loadText=findViewById(R.id.loadingText);
        attemptsTitle=findViewById(R.id.attemptsTitle);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        graph=findViewById(R.id.graph);
        subtitle=findViewById(R.id.subtitle);
        letterSpinner = findViewById(R.id.letterSpinnerA);
        categorySpinner = findViewById(R.id.categorySpinner);
        firebaseFirestore = FirebaseFirestore.getInstance();
        attempts=new ArrayList<>();
        listView=findViewById(R.id.listview);
        if (mAuth.getCurrentUser() == null) {
            sendToLogin();
        }else{
            currentUser = mAuth.getCurrentUser().getUid();
            DocumentReference documentReference = db.collection("user").document(currentUser);
            documentReference.addSnapshotListener(this, (value, error) -> {
                studentName.setText(value.getString("name"));
                studentClass.setText(value.getString("class"));
                school.setText(value.getString("school"));
            });

        }
        tracingBtn.setOnClickListener(v -> {
            Intent tracingIntent = new Intent(MainActivity.this, TracingActivity.class);
            startActivity(tracingIntent);
        });
        decodeButton.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this,DecodeScreen.class)));

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            recreate();
        });

        attemptsAdapter = new ArrayAdapter(getApplicationContext(),
                R.layout.simple_list_item_1,
                arrayList);
        listView.setAdapter(attemptsAdapter);
        graph.setTitle(selectedCategory+" Graph");
        graph.setTitleTextSize(32);
        graph.setCursorMode(true);
        GridLabelRenderer gridLabel=graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Attempts");
        gridLabel.setHorizontalAxisTitleTextSize(24);
        gridLabel.setVerticalAxisTitle(selectedCategory);
        gridLabel.setVerticalAxisTitleTextSize(24);
        gridLabel.setVerticalAxisTitleColor(Color.WHITE);
        gridLabel.setHorizontalAxisTitleColor(Color.WHITE);
        gridLabel.setHorizontalLabelsVisible(true);

        ArrayAdapter<String> letterAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Letter2));
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner.setAdapter(letterAdapter);
        letterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedLetter=letterSpinner.getSelectedItem().toString();
                graph.setTitle(selectedCategory+" Graph of "+selectedLetter);
                onStart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Type));
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedCategory=categorySpinner.getSelectedItem().toString();
                gridLabel.setVerticalAxisTitle(selectedCategory);
                onStart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });
          }

    @Override
    protected void onStart() {
        super.onStart();
        graph.setVisibility(View.INVISIBLE);
        letterSpinner.setVisibility(View.INVISIBLE);
        categorySpinner.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.INVISIBLE);
        attemptsTitle.setVisibility(View.INVISIBLE);
        loadText.setVisibility(View.VISIBLE);
        loadProgress.setVisibility(View.VISIBLE);
        subtitle.setVisibility(View.INVISIBLE);
        graph.removeAllSeries();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            loadText.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline5);
        }
        loadText.setText("Loading your data...");
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        if(selectedCategory.contentEquals("Score")){
            series.setColor(Color.parseColor(getString(R.string.graph_line)));
        } else if(selectedCategory.contentEquals("No of Errors")){
            series.setColor(Color.RED);
        }else{
            series.setColor(Color.BLUE);
        }
        series.setTitle(selectedCategory);
        series.setDataPointsRadius(4);
        series.setThickness(8);
        series.setAnimated(true);
        series.setDrawAsPath(true);
        series.setDrawDataPoints(true);

        firebaseFirestore.collection("Attempts")
                .orderBy("AttemptedAt", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        arrayList.clear();
                        attempts.clear();
                        idLists.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Attempt attempt = document.toObject(Attempt.class);
                            if (attempt.getUser().contentEquals(currentUser)) {
                                if (selectedLetter.contentEquals("All")) {
                                    attempts.add(attempt);
                                    arrayList.add(attempts.size() + " - " + attempt.getAttemptedAt());
                                    idLists.add(document.getId());
                                } else if (selectedLetter.contentEquals(attempt.getLetter())) {
                                    attempts.add(attempt);
                                    arrayList.add(attempts.size() + " - " + attempt.getAttemptedAt());
                                    idLists.add(document.getId());

                                }

                            }
                            ;
                        }
                        if(attempts.size()>0) {
                            graph.setVisibility(View.VISIBLE);
                            letterSpinner.setVisibility(View.VISIBLE);
                            categorySpinner.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            attemptsTitle.setVisibility(View.VISIBLE);
                            loadProgress.setVisibility(View.INVISIBLE);
                            loadText.setVisibility(View.INVISIBLE);
                            showLog("attempt size = " + attempts.size());
                            if (selectedCategory.contentEquals("Score")) {
                                for (int i = 0; i < attempts.size(); i++) {
                                    series.appendData(
                                            new DataPoint(i + 1,
                                                    Integer.parseInt(attempts.get(i).getScore())),
                                            true,
                                            attempts.size());
                                }
                            } else if (selectedCategory.contentEquals("No of Errors")) {
                                for (int i = 0; i < attempts.size(); i++) {
                                    series.appendData(
                                            new DataPoint(i + 1,
                                                    Integer.parseInt(attempts.get(i).getNe())),
                                            true,
                                            attempts.size());
                                }
                            } else if (selectedCategory.contentEquals("Time Taken")) {
                                for (int i = 0; i < attempts.size(); i++) {
                                    series.appendData(
                                            new DataPoint(i + 1,
                                                    Integer.parseInt(String.valueOf(attempts.get(i).getTime().subSequence(0, 2)))),
                                            true,
                                            attempts.size());
                                }
                            } else if (selectedCategory.contentEquals("Speed")) {
                                for (int i = 0; i < attempts.size(); i++) {
                                    series.appendData(
                                            new DataPoint(i + 1,
                                                    Integer.parseInt(String.valueOf(attempts.get(i).getSpeed().subSequence(0, 2)))),
                                            true,
                                            attempts.size());
                                }
                            }

                            attemptsAdapter.notifyDataSetChanged();
                            graph.addSeries(series);
                            //graph.getViewport().setScrollable(true);
                            graph.getViewport().setScalable(true);
                            graph.getViewport().setMinX(1);
                            graph.getViewport().setMinY(0);

                        }else{
                            loadProgress.setVisibility(View.INVISIBLE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                loadText.setTextAppearance(R.style.TextAppearance_MaterialComponents_Headline4);
                            }
                            loadText.setText(R.string.welcome);
                            ObjectAnimator animation = ObjectAnimator.ofFloat(loadText, "translationX", -320f);
                            animation.setDuration(300);
                            animation.start();
                            ObjectAnimator animation2 = ObjectAnimator.ofFloat(tracingBtn, "translationY", +240f);
                            animation2.setDuration(300);
                            animation2.start();
                            animation2.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    subtitle.setVisibility(View.VISIBLE);
                                    tracingBtn.setOnClickListener(v -> {
                                        loadText.setVisibility(View.INVISIBLE);
                                        subtitle.setVisibility(View.INVISIBLE);
                                        loadProgress.setVisibility(View.INVISIBLE);
                                        ObjectAnimator animationX = ObjectAnimator.ofFloat(loadText, "translationX", 0);
                                        animationX.setDuration(300);
                                        animationX.start();
                                        animationX.addListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                super.onAnimationEnd(animation);
                                                ObjectAnimator animation2 = ObjectAnimator.ofFloat(tracingBtn, "translationY", 0);
                                                animation2.setDuration(300);
                                                animation2.start();
                                                animation2.addListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);
                                                        Intent tracingIntent = new Intent(MainActivity.this, TracingActivity.class);
                                                        startActivity(tracingIntent);
                                                    }
                                                });

                                            }
                                        });

                                    });

                                }
                            });
                        }
                    } else {
                        showLog("Error getting documents: "+ task.getException());
                    }
                });

        listView.setOnItemClickListener((parent, view, position, id) ->
                startActivity(new Intent(MainActivity.this,SuccessScreen.class)
                .putExtra("ID",idLists.get(position))));

    }


    private void showLog(String data){
        Log.d(TAG, data);
    }


    protected void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}