package com.example.characteranalyser;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import static android.view.View.GONE;

public class DecodeScreen extends AppCompatActivity {
    private static final String TAG = "CAnalyser";
    private ImageButton submitButton;
    private TracingLayout tracingLayout;
    private ImageButton homeButton;
    private Spinner letterSpinner;
    private String selectedLetter="A";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_screen);
        getSupportActionBar().hide();
        submitButton=findViewById(R.id.submitButton);
        tracingLayout=findViewById(R.id.traceLayout);
        homeButton=findViewById(R.id.homeButton);
        letterSpinner = findViewById(R.id.letterSpinner);
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
        tracingLayout.setMODE(TracingLayout.DECODE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshButton.setOnClickListener(v -> {
            recognitionImage.setVisibility(GONE);
            recognitionProgress.setVisibility(GONE);
            recognitionText.setVisibility(GONE);
            errorProgress.setVisibility(GONE);
            errorText.setVisibility(GONE);
            errorCheck.setVisibility(GONE);
            uploadProgress.setVisibility(GONE);
            uploadText.setVisibility(GONE);
            uploadCheck.setVisibility(GONE);

            tracingLayout.refresh();
            tracingLayout.setMODE(TracingLayout.DECODE);
        });

        submitButton.setOnClickListener(v -> tracingLayout.uploadLetterData());
        homeButton.setOnClickListener(v -> finish());

        ArrayAdapter<String> letterAdapter = new ArrayAdapter<String>(DecodeScreen.this,
                R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Letter));
        letterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        letterSpinner.setAdapter(letterAdapter);
        letterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedLetter=letterSpinner.getSelectedItem().toString();
                tracingLayout.setSelectedLetter(selectedLetter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });


    }

}