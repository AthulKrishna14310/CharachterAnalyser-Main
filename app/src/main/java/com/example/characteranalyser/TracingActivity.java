package com.example.characteranalyser;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import static android.view.View.GONE;

public class TracingActivity extends AppCompatActivity {
    private static final String TAG = "CAnalyser";
    private ImageButton submitButton;
    private TracingLayout tracingLayout;
    private ImageButton homeButton;
    private Spinner letterSpinner;
    private Spinner letterSpinnera;
    private Spinner letterSpinner1;

    private String selectedLetter="A";
    private TextRecognizer recognizer;
    private TextView recognitionText;
    private ImageView recognitionImage;
    private ProgressBar recognitionProgress;
    private ProgressBar errorProgress;
    private TextView errorText;
    private ImageView errorCheck;
    private ProgressBar uploadProgress;
    private TextView uploadText;
    private ImageView uploadCheck;
    private ImageView refreshButton;

    private String currentUser;
    private GraphView graph;
    private GridLabelRenderer gridLabel;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Attempt> attempts=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_screen);
        getSupportActionBar().hide();

        currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        submitButton=findViewById(R.id.submitButton);
        tracingLayout=findViewById(R.id.traceLayout);
        homeButton=findViewById(R.id.homeButton);
        letterSpinner = findViewById(R.id.letterSpinnerA);
        letterSpinnera = findViewById(R.id.letterSpinnera);
        letterSpinner1 = findViewById(R.id.letterSpinner1);

        recognizer = TextRecognition.getClient();
        recognitionImage=findViewById(R.id.recognitionCheck);
        recognitionProgress=findViewById(R.id.recognitionProgress);
        recognitionText=findViewById(R.id.recognitionText);
        errorProgress=findViewById(R.id.errorProgress);
        errorText=findViewById(R.id.errorText);
        errorCheck=findViewById(R.id.errorCheck);
        uploadProgress=findViewById(R.id.uploadProgress);
        uploadText=findViewById(R.id.uploadText);
        uploadCheck=findViewById(R.id.uploadCheck);
        refreshButton=findViewById(R.id.refresh);
        recognitionImage.setVisibility(GONE);
        recognitionProgress.setVisibility(GONE);
        recognitionText.setVisibility(GONE);
        errorProgress.setVisibility(GONE);
        errorText.setVisibility(GONE);
        errorCheck.setVisibility(GONE);
        uploadProgress.setVisibility(GONE);
        uploadText.setVisibility(GONE);
        uploadCheck.setVisibility(GONE);
        tracingLayout.downloadRegions(uploadProgress,uploadText);
        tracingLayout.setMODE(TracingLayout.TRACE);
        graph=findViewById(R.id.graph);
        graph.setTitle("Previous perfomance of "+selectedLetter);
        graph.setTitleTextSize(32);
        graph.setCursorMode(true);
        GridLabelRenderer gridLabel=graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Attempts");
        gridLabel.setHorizontalAxisTitleTextSize(24);
        gridLabel.setVerticalAxisTitle("Score");
        gridLabel.setVerticalAxisTitleTextSize(24);
        gridLabel.setVerticalAxisTitleColor(Color.WHITE);
        gridLabel.setHorizontalAxisTitleColor(Color.WHITE);
        gridLabel.setHorizontalLabelsVisible(true);
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshButton.setOnClickListener(v -> {

            refreshScreen();

        });

        submitButton.setOnClickListener(v -> startEvaluation());
        homeButton.setOnClickListener(v -> finish());

        ArrayAdapter<String> letterAdapter = new ArrayAdapter<String>(TracingActivity.this,
                R.layout.dropdownlistitem,
                getResources().getStringArray(R.array.Letter));
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner.setAdapter(letterAdapter);
        letterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                selectedLetter=letterSpinner.getSelectedItem().toString();
                tracingLayout.setSelectedLetter(selectedLetter);
                tracingLayout.setMODE(TracingLayout.TRACE);
                tracingLayout.downloadRegions(uploadProgress,uploadText);
                refreshScreen();
                showGraph(selectedLetter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        ArrayAdapter<String> letterAdaptera = new ArrayAdapter<String>(TracingActivity.this,
                R.layout.dropdownlistitem,
                getResources().getStringArray(R.array.Lettera));
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinnera.setAdapter(letterAdaptera);
        letterSpinnera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                selectedLetter=letterSpinnera.getSelectedItem().toString();
                tracingLayout.setSelectedLetter(selectedLetter);
                tracingLayout.setMODE(TracingLayout.TRACE);
                tracingLayout.downloadRegions(uploadProgress,uploadText);
                refreshScreen();
                showGraph(selectedLetter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        ArrayAdapter<String> letterAdapter1 = new ArrayAdapter<String>(TracingActivity.this,
                R.layout.dropdownlistitem,
                getResources().getStringArray(R.array.number));
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner1.setAdapter(letterAdapter1);
        letterSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView,
                                       int position,
                                       long id) {
                selectedLetter=letterSpinner1.getSelectedItem().toString();
                tracingLayout.setSelectedLetter(selectedLetter);
                tracingLayout.setMODE(TracingLayout.TRACE);
                tracingLayout.downloadRegions(uploadProgress,uploadText);
                refreshScreen();
                showGraph(selectedLetter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


        showGraph(selectedLetter);
    }

    private void refreshScreen() {
        recognitionImage.setVisibility(GONE);
        recognitionProgress.setVisibility(GONE);
        recognitionText.setVisibility(GONE);
        errorProgress.setVisibility(GONE);
        errorText.setVisibility(GONE);
        errorCheck.setVisibility(GONE);
        uploadProgress.setVisibility(GONE);
        uploadText.setVisibility(GONE);
        uploadCheck.setVisibility(GONE);
        recognitionText.setText("");
        errorText.setText("");
        uploadText.setTextColor(Color.WHITE);
        uploadText.setText("");
        tracingLayout.refresh();
        tracingLayout.downloadRegions(uploadProgress,uploadText);
        tracingLayout.setMODE(TracingLayout.TRACE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recognitionImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_24));
        }
    }

    private void startEvaluation() {
        recognitionProgress.setVisibility(View.VISIBLE);
        recognitionText.setVisibility(View.VISIBLE);
        recognitionText.setText(R.string.recognize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tracingLayout.setBackground(getDrawable(R.drawable.white_bg));
        }
        InputImage image = InputImage.fromBitmap(tracingLayout.getDrawingCache(),0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tracingLayout.setBackground(getDrawable(R.drawable.bg));
        }

        recognizer.process(image)
                        .addOnSuccessListener(visionText -> {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                public void run() {
                                    if(selectedLetter.contentEquals(visionText.getText().toString().trim())){
                                        recognitionProgress.setVisibility(GONE);
                                        recognitionText.setText(getString(R.string.reco_success));
                                        recognitionImage.setVisibility(View.VISIBLE);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            tracingLayout.setBackground(getDrawable(R.drawable.bg));
                                        }
                                        InputImage temp = InputImage.fromBitmap(tracingLayout.getDrawingCache(),0);
                                        tracingLayout.downloadLetterDataAndEvaluate(
                                                    findViewById(R.id.errorProgress),
                                                    findViewById(R.id.errorText),
                                                    findViewById(R.id.errorCheck),
                                                    findViewById(R.id.uploadProgress),
                                                    findViewById(R.id.uploadText),
                                                    findViewById(R.id.uploadCheck)
                                                );

                                    }else{
                                        recognitionProgress.setVisibility(GONE);
                                        recognitionText.setText(getString(R.string.reco_failed));
                                        recognitionImage.setVisibility(View.VISIBLE);
                                        recognitionImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_cancel_24));
                                        Log.d(TAG," Failed "+selectedLetter);

                                    }
                                }
                            }, 1000);
                        })
                        .addOnFailureListener(
                                e -> {
                                    Log.d(TAG,e.getMessage());
                                    recognitionProgress.setVisibility(GONE);
                                    recognitionText.setText(getString(R.string.reco_info));
                                    recognitionImage.setVisibility(View.VISIBLE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        recognitionImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_info_24));
                                    }
                                });


    }

    private void  showGraph(String sl){
        graph.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        series.setDataPointsRadius(4);
        series.setThickness(8);
        series.setAnimated(true);
        series.setDrawAsPath(true);
        series.setDrawDataPoints(true);
        graph.setTitle("Previous perfomance of "+selectedLetter);

        firebaseFirestore.collection("Attempts")
                .orderBy("AttemptedAt", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        attempts.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                                Attempt attempt = document.toObject(Attempt.class);
                                if (attempt.getUser().contentEquals(currentUser)) {
                                    if (sl.contentEquals("All")) {
                                    attempts.add(attempt);
                                } else if (sl.contentEquals(attempt.getLetter())) {
                                    attempts.add(attempt);
                                    }

                                }
                            }
                            for (int i = 0; i < attempts.size(); i++) {
                                series.appendData(
                                        new DataPoint(i + 1,
                                                Integer.parseInt(attempts.get(i).getScore())),
                                        true,
                                        attempts.size());
                            }
                            graph.addSeries(series);
                            //graph.getViewport().setScrollable(true);
                            graph.getViewport().setScalable(true);
                            graph.getViewport().setMinX(1);
                            graph.getViewport().setMinY(0);


                    } else {
                        showLog("Error getting documents: "+ task.getException());
                    }
                });


    }
    private void showLog(String data){
        Log.d(TAG, data);
    }

}