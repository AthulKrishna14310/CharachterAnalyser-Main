package com.example.characteranalyser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class TracingLayout extends AppCompatImageView {
    private static final String TAG ="TRACING_LAYOUT";
    private static final int DIVIDER =24;
    private static final int HARD_CODED_HEIGHT=576;
    private static final int HARD_CODED_WIDHT=640;
    private static final int CIRCLE_WIDTH = 6;
    private static final int CIRCLE_RADIUS = 42;
    private static final String INNER_REGION_1 ="inner_region_1";
    private static final String INNER_REGION_2 ="inner_region_2";
    private static final String INNER_REGION_3 ="inner_region_3";
    private static final String OUTER_REGION_1 ="outer_region_1";
    private static final String OUTER_REGION_2 ="outer_region_2";
    private static final String ON_LINE_1 ="on_line______1";
    private static final String ON_LINE_2 ="on_line______2";
    private static final String ON_LINE_3 ="on_line______3";
    private static final String ON_LINE_4 ="on_line______4";
    private static final String ON_TRACE = "on_trace______";
    private static final String INTEGER_FORMAT="%02d";
    public static final int TRACE = 21;
    public static final int DECODE=22;
    private ViewGroup.LayoutParams params;
    private Path path=new Path();
    private Paint brush=new Paint();
    private int traceDPX;
    private int traceDPY;
    private float tracePixelX;
    private float tracePixelY;
    private Bitmap backgroundBitmap;
    private int startDpX;
    private int startDpY;
    private int endDpX;
    private int endDpY;
    private int  pixelBitmap;
    private int redValue;
    private int blueValue;
    private int greenValue;
    private String region="";
    private float downPixelX;
    private float downPixelY;
    private String direction="";
    private ArrayList<String> tracedRegions;
    private ArrayList<String> tracedDirections;
    private ArrayList<String> tracedSquares;
    private String selectedLetter="P";
    private String tempTracedRegion="demo";
    private String tempTracedDirection="demo";
    private String tempTracedSquare="demo";
    private long timeTaken=0;
    private int traceSpeed=0;
    private long startTime=0;
    private long endTime=0;
    private int timeTakenSeconds=0;
    private ArrayList<DynamicPixel>tracedDPs=new ArrayList<>();
    private String timeStamp;
    private String directionResult="";
    private String regionResult="";
    private String squareResult="";
    private ArrayList<String>downloadedRegions=new ArrayList<>();
    private ArrayList<String> downloadedSquares =new ArrayList<>();
    private ArrayList<String>downloadedDirections=new ArrayList<>();
    private String downloadedStartDpX="";
    private String downloadedStartDpY="";
    private DatabaseReference letterdataDatabaseReference;
    private int xDifference=0;
    private ArrayList<String> scaledSquares =new ArrayList<>();
    private ArrayList<String> spottedSquare=new ArrayList<>();
    private ArrayList<DynamicPixel> spottedDps =new ArrayList<>();
    private ArrayList<Pixel>spottedPixels=new ArrayList<>();
    private ArrayList<Bitmap> bitmapCopies=new ArrayList<>();
    private ArrayList<String> errorImageStoredPaths =new ArrayList<>();
    private ArrayList<Bitmap> resultBitmaps=new ArrayList<>();
    private float currentX;
    private float currentY;
    private ArrayList<Pixel>tracedPixels=new ArrayList<>();
    private int TOTAL_SCORE=50;
    private int score=0;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    public static String getTAG() {
        return TAG;
    }
    private ArrayList<String> uploadedImages=new ArrayList<>();
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String>realtimeRegions=new ArrayList<>();
    private int MODE;
    private boolean realtimeRegionIndex=true;
    private TextView realtimeProgress;

    public TracingLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        brush.setAntiAlias(true);
        brush.setColor(Color.BLUE);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(24f);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache(true);
        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        startDpX=0;
        startDpY=0;
        tracedRegions=new ArrayList<>();
        tracedDirections=new ArrayList<>();
        tracedSquares=new ArrayList<>();
        letterdataDatabaseReference= FirebaseDatabase
                .getInstance().getReference().child("LetterData");
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference().child("Attempts");
        firebaseFirestore=FirebaseFirestore.getInstance();
        MODE=TRACE;
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        tracePixelX=event.getX();
        tracePixelY=event.getY();
        traceDPX=pxTodp((int)tracePixelX);
        traceDPY=pxTodp((int)tracePixelY);
        backgroundBitmap=this.getDrawingCache();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                 downPixelX=event.getX();
                 downPixelY=event.getY();
                 path.moveTo(tracePixelX,tracePixelY);
                 startTime=System.currentTimeMillis();



                return true;
             case MotionEvent.ACTION_MOVE:
                 if((int)tracePixelX>0&&(int)tracePixelY>0&&(int)tracePixelX<backgroundBitmap.getHeight()
                         &&(int)event.getX()<backgroundBitmap.getWidth()) {
                            if(startDpX ==0&& startDpY ==0){
                                startDpX =traceDPX;
                                startDpY =traceDPY;
                             }
                     endDpX=traceDPX;
                     endDpY=traceDPY;
                     tracedDPs.add(new DynamicPixel(traceDPX,traceDPY));
                     tracedPixels.add(new Pixel((int)tracePixelX,(int)tracePixelY));
                     currentX = event.getX();
                     currentY = event.getY();

                     if (Math.abs(downPixelX - currentX) > Math.abs(downPixelY - currentY)) {
                         if (downPixelX < currentX) {
                             direction = "right";
                         }
                         if (downPixelX > currentX) {
                             direction = "left";
                         }

                     } else {
                         if (downPixelY < currentY) {
                             direction = "down";
                         }
                         if (downPixelY > currentY) {
                             direction = "up";
                         }
                     }

                     pixelBitmap=backgroundBitmap.getPixel((int)tracePixelX,(int)tracePixelY);
                     redValue = Color.red(pixelBitmap);
                     blueValue = Color.blue(pixelBitmap);
                     greenValue = Color.green(pixelBitmap);
                     if(redValue==241&&blueValue==241&&greenValue==241){
                         region=INNER_REGION_1;
                     }else if(redValue==242&&blueValue==242&&greenValue==242){
                         region=INNER_REGION_2;
                     }else if(redValue==243&&blueValue==243&&greenValue==243){
                         region=INNER_REGION_3;
                     }else if(redValue==255&&blueValue==255&&greenValue==255){
                         region=OUTER_REGION_1;
                     }else if(redValue==250&&blueValue==250&&greenValue==250){
                         region=OUTER_REGION_2;
                     }else if(redValue==00&&blueValue==00&&greenValue==244){
                         region=ON_LINE_1;
                     }else if(redValue==00&&blueValue==00&&greenValue==248){
                         region=ON_LINE_2;
                     }else if(redValue==00&&blueValue==00&&greenValue==252){
                         region=ON_LINE_3;
                     }else if(redValue==00&&blueValue==00&&greenValue==255){
                         region=ON_LINE_4;
                     }
                     else if(redValue==00&&blueValue==255&&greenValue==00){
                         region=ON_TRACE;
                     }


                     if(tempTracedDirection.contentEquals(direction)){
                        //Do Nothing
                     }else{
                         tracedDirections.add(direction);
                         tempTracedDirection=direction;
                     }


                     if(tempTracedSquare.contentEquals(getSquareNumber())){
                        //Do Nothing
                     }else{
                         tracedSquares.add(getSquareNumber());
                         tempTracedSquare=getSquareNumber();
                     }
                     if(MODE==DECODE) {
                         if (tracedRegions.contains(region)) {
                             //Do Nothing
                         } else {
                             tracedRegions.add(region);
                             tempTracedRegion = region;
                         }
                     }else if(MODE==TRACE){
                         if (tracedRegions.contains(region)) {
                             //Do Nothing
                         } else {
                             tracedRegions.add(region);
                             tempTracedRegion = region;
                             if(realtimeRegions.contains(region)){
                                 realtimeRegionIndex=true;
                                 showLog("TRACED IN OF REGION ");
                                 realtimeProgress.setTextColor(Color.WHITE);
                             }else{
                                 realtimeRegionIndex=false;
                                 showLog("TRACED OUT OF REGION ");
                                 realtimeProgress.setTextColor(Color.RED);
                                 score=score-1;
                                 realtimeProgress.setText(getContext().getString(R.string.traced_out_of_region)+score);
                                 tracedRegions.remove(region);
                             }
                         }
                     }


                     path.lineTo(tracePixelX,tracePixelY);

                 }
                 break;
             case MotionEvent.ACTION_UP:
                 endTime=System.currentTimeMillis();
                 timeTaken=timeTaken+(endTime-startTime);
                 timeTakenSeconds=(int)timeTaken/1000;



                 break;

            default:
                return super.onTouchEvent(event);

        }
        postInvalidate();
        return super.onTouchEvent(event);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path,brush);
    }
    private int pxTodp(int px)
    {
        return (int) (px / this.getResources().getDisplayMetrics().density);
    }
    private int dpToPixel(int dp){
        return (int) (dp * this.getResources().getDisplayMetrics().density);

    }
    private void showLog(String s) {
        Log.d(TAG,s);
    }

    private String getSquareNumber(){
        Square square=new Square(traceDPX/DIVIDER,traceDPY/DIVIDER);
        return square.squareName();
    }
    public void getSecondaryAttributes(){
        for(int i=0;i<tracedRegions.size();i++){
            regionResult=regionResult+","+tracedRegions.get(i);
        }
        for(int i=0;i<tracedDirections.size();i++){
            directionResult=directionResult+","+tracedDirections.get(i);
        }
        for(int i=0;i<tracedSquares.size();i++){
            squareResult=squareResult+","+tracedSquares.get(i);
        }

    }

    public String storeImage(Bitmap bitmapImage) {
        File directory = getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File myPath = new File(directory, System.currentTimeMillis() + "__.jpeg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return myPath.getAbsolutePath();
    }
    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void downloadLetterDataAndEvaluate(
            ProgressBar errorProgress,
            TextView errorText,
            ImageView errorCheck,
            ProgressBar uploadProgress,
            TextView uploadText,
            ImageView uploadCheck)
    {
        downloadedDirections.clear();
        downloadedSquares.clear();
        downloadedRegions.clear();
        downloadedStartDpX="";
        downloadedStartDpY="";

        errorProgress.setVisibility(VISIBLE);
        errorText.setVisibility(VISIBLE);
        errorText.setText(R.string.spotting);

        letterdataDatabaseReference
                .child(selectedLetter)
                .child("direction")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    String s=dataSnapshot.getValue().toString();
                    String[] arrOfStr = s.split(",");
                    downloadedDirections.addAll(Arrays.asList(arrOfStr));
                    letterdataDatabaseReference
                            .child(selectedLetter)
                            .child("squares")
                            .get()
                            .addOnSuccessListener(dataSnapshot1 -> {
                                String s1=dataSnapshot1.getValue().toString();
                                String[] arrOfStr1 = s1.split(",");
                                downloadedSquares.addAll(Arrays.asList(arrOfStr1));
                                letterdataDatabaseReference
                                        .child(selectedLetter)
                                        .child("region")
                                        .get()
                                        .addOnSuccessListener(dataSnapshot2 -> {
                                            String s2=dataSnapshot2.getValue().toString();
                                            String[] arrOfStr2 = s2.split(",");
                                            downloadedRegions.addAll(Arrays.asList(arrOfStr2));
                                            letterdataDatabaseReference
                                                    .child(selectedLetter)
                                                    .child("startDpX")
                                                    .get()
                                                    .addOnSuccessListener(dataSnapshot3 -> {
                                                        downloadedStartDpX=dataSnapshot3.getValue().toString();
                                                        letterdataDatabaseReference
                                                                .child(selectedLetter)
                                                                .child("startDpY")
                                                                .get()
                                                                .addOnSuccessListener(dataSnapshot4 -> {
                                                                    downloadedStartDpY=dataSnapshot4.getValue().toString();
                                                                    downloadedRegions.remove(0);
                                                                    downloadedSquares.remove(0);
                                                                    downloadedDirections.remove(0);
                                                                    evaluate(
                                                                            errorProgress,
                                                                            errorText,
                                                                            errorCheck,
                                                                            uploadProgress,
                                                                            uploadText,
                                                                            uploadCheck
                                                                    );
                                                                });


                                                    });
                                            });
                                        });
                });
    }

    public void evaluate(
            ProgressBar errorProgress,
            TextView errorText,
            ImageView errorCheck,
            ProgressBar uploadProgress,
            TextView uploadText,
            ImageView uploadCheck
    ){
        xDifference=Integer.parseInt(String.valueOf(tracedSquares.get(0).subSequence(0,2)))-
                Integer.parseInt(String.valueOf(downloadedSquares.get(0).subSequence(0,2)));
        for (int i =0;i<tracedSquares.size();i++){
            Square square=getScaledSquare(tracedSquares.get(i),xDifference);
            scaledSquares.add(square.squareName());
        }
            for(int i=0;i<scaledSquares.size();i++){
                if(downloadedSquares.contains(scaledSquares.get(i))){

                }else{
                    spottedSquare.add(tracedSquares.get(i));
                }
            }

        score=score+TOTAL_SCORE-(spottedSquare.size());
        if(score<0){
            score=0;
        }
        for(int i=0;i<spottedSquare.size();i++){
            spottedDps.add(new DynamicPixel(
                    (Integer.parseInt(String.valueOf(spottedSquare.get(i).subSequence(0,2))))*DIVIDER,
                    (Integer.parseInt(String.valueOf(spottedSquare.get(i).subSequence(2,4))))*DIVIDER));
        }
        for(int i=0;i<spottedDps.size();i++){
            spottedPixels.add(new Pixel(dpToPixel(spottedDps.get(i).dPX),dpToPixel(spottedDps.get(i).dPY)));
        }

        for(int i=0;i<spottedPixels.size();i++){
            bitmapCopies.add(getBitmap(storeImage(getDrawingCache())));
        }
        for(int i=0;i<bitmapCopies.size();i++){
            errorImageStoredPaths.add(
                    drawCircleOnBitmapAndSave(
                            spottedPixels.get(i).getX(),
                            spottedPixels.get(i).getY(),
                            bitmapCopies.get(i))
            );
         }
        timeStamp= getTimeStamp();
        traceSpeed=(int)tracedDPs.size()/timeTakenSeconds;
        showLog("Attempted at = "+timeStamp);
        showLog("Letter = "+selectedLetter);
        showLog("Time Taken = "+timeTakenSeconds+" sec");
        showLog("Tracing Speed = "+traceSpeed+" dp/s");
        showLog("Total = "+tracedSquares.size());
        showLog("Number of Errors = "+spottedSquare.size());
        showLog("Score = "+score);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                errorProgress.setVisibility(GONE);
                errorCheck.setVisibility(VISIBLE);
                errorText.setText(getContext().getString(R.string.err_spot_success));
                uploadProgress.setVisibility(VISIBLE);
                uploadText.setVisibility(VISIBLE);
                uploadText.setText(R.string.upload_report);
                if(errorImageStoredPaths.size()!=0) {
                    uploadMultipleFile(0,uploadProgress,uploadText,uploadCheck);
                }else{
                    uploadToFireStore(uploadProgress,uploadText,uploadCheck);
                }
            }
        }, 1000);
    }
    private String drawCircleOnBitmapAndSave(float x,float y, Bitmap bitmap){
        Bitmap bitmapMutable=bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas=new Canvas(bitmapMutable);
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(CIRCLE_WIDTH);
        paint1.setColor(Color.RED);
        paint1.setAntiAlias(true);
        canvas.drawCircle(
                x,
                y,
                CIRCLE_RADIUS,
                paint1);
        resultBitmaps.add(bitmapMutable);
        return storeImage(bitmapMutable);
    }
    private Square getScaledSquare(String data,int difference){
        int x=Integer.parseInt(String.valueOf(data.subSequence(0,2)));
        int y=Integer.parseInt(String.valueOf(data.subSequence(2,4)));
        x-=difference;
        return new Square(x,y);
    }
    public void uploadLetterData() {
        showLog("Uploading data ...");
        getSecondaryAttributes();
        letterdataDatabaseReference
                .child(selectedLetter)
                .child("region")
                .setValue(regionResult)
                .addOnSuccessListener(aVoid -> letterdataDatabaseReference
                        .child(selectedLetter)
                        .child("direction")
                        .setValue(directionResult)
                        .addOnSuccessListener(aVoid1 -> {
                            letterdataDatabaseReference
                                    .child(selectedLetter)
                                    .child("squares")
                                    .setValue(squareResult)
                                    .addOnSuccessListener(aVoid11 -> {
                                        letterdataDatabaseReference
                                                .child(selectedLetter)
                                                .child("startDpX")
                                                .setValue(startDpX+"")
                                                .addOnSuccessListener(aVoid2 ->
                                                        letterdataDatabaseReference
                                                                .child(selectedLetter)
                                                                .child("startDpY")
                                                                .setValue(startDpY+"").
                                                                addOnSuccessListener(aVoid3 ->
                                                                        Toast.makeText(getContext(),
                                                                                "Data decoded for letter "+selectedLetter,Toast.LENGTH_LONG).show()
                                                                )
                                                );
                                    });
                        }));
    }



    public void downloadRegions(ProgressBar progress,TextView status){
        progress.setVisibility(VISIBLE);
        status.setVisibility(VISIBLE);
        status.setText(R.string.configuring);
        realtimeProgress=status;
        realtimeRegions.clear();

        letterdataDatabaseReference
                .child(selectedLetter)
                .child("region")
                .get().addOnSuccessListener(dataSnapshot -> {
                    String temp=dataSnapshot.getValue().toString();
                    String[] arrOfStr2 = temp.split(",");
                    realtimeRegions.addAll(Arrays.asList(arrOfStr2));
                    realtimeRegions.remove(0);
                    progress.setVisibility(GONE);
                    status.setText(getContext().getString(R.string.start_tracing)+" "+selectedLetter);
                });

    }


    public void refresh(){
        clearData();
        Paint brush1=new Paint();
        Path path1=new Path();
        brush1.setAntiAlias(true);
        brush1.setColor(Color.WHITE);
        brush1.setStyle(Paint.Style.STROKE);
        brush1.setStrokeJoin(Paint.Join.ROUND);
        brush1.setStrokeWidth(24f);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.bg, options);
        Canvas canvas = new Canvas(mBackground);
        for (int i=0;i<tracedPixels.size();i++){
            path1.moveTo(tracedPixels.get(i).x,tracedPixels.get(i).y);
            canvas.drawPath(path1,brush1);
        }

        setBackgroundBitmap(mBackground);
        setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bg));
    }

    private void clearData() {
        brush=new Paint();
        path=new Path();
        tracedRegions.clear();
        tracedDirections.clear();
        tracedSquares.clear();
        tracedDPs.clear();
        downloadedRegions.clear();
        downloadedSquares.clear();
        downloadedDirections.clear();
        scaledSquares.clear();
        spottedSquare.clear();
        spottedDps.clear();
        spottedPixels.clear();
        bitmapCopies.clear();
        errorImageStoredPaths.clear();
        resultBitmaps.clear();
        tracedPixels.clear();
        uploadedImages.clear();
        realtimeRegions.clear();
        directionResult="";
        squareResult="";
        tempTracedRegion="temp";
        tempTracedDirection="demo";
        tempTracedSquare="demo";
        downloadedStartDpX="";
        downloadedStartDpY="";
        region="";
        direction="";
        regionResult="";

        xDifference=0;
        timeTaken=0;
        traceSpeed=0;
        startTime=0;
        endTime=0;
        timeTakenSeconds=0;
        score=0;
        brush.setAntiAlias(true);
        brush.setColor(Color.BLUE);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(24f);
    }

    public class Pixel{
        private int x;
        private int y;

        public Pixel(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    private class Square{
        int xName;
        int yName;

        public Square(int xName, int yName) {
            this.xName = xName;
            this.yName = yName;
        }

        public int getxName() {
            return xName;
        }

        public void setxName(int xName) {
            this.xName = xName;
        }

        public int getyName() {
            return yName;
        }

        public void setyName(int yName) {
            this.yName = yName;
        }

        private String squareName(){
            String x=String.format(INTEGER_FORMAT, xName);
            String y=String.format(INTEGER_FORMAT, yName);
            return x+y;
        }


    }
    private class DynamicPixel{
        private int dPX;
        private int dPY;

        public DynamicPixel(int dPX, int dPY) {
            this.dPX = dPX;
            this.dPY = dPY;
        }

        public int getdPX() {
            return dPX;
        }

        public void setdPX(int dPX) {
            this.dPX = dPX;
        }

        public int getdPY() {
            return dPY;
        }

        public void setdPY(int dPY) {
            this.dPY = dPY;
        }
    }

    public class Error {
        private DynamicPixel dynamicPixel;
        private String message;
        private Bitmap data;
        private String savedPath;

        public Error(DynamicPixel dynamicPixel, String message, Bitmap data, String savedPath) {
            this.dynamicPixel = dynamicPixel;
            this.message = message;
            this.data = data;
            this.savedPath = savedPath;
        }

        public DynamicPixel getDynamicPixel() {
            return dynamicPixel;
        }

        public void setDynamicPixel(DynamicPixel dynamicPixel) {
            this.dynamicPixel = dynamicPixel;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Bitmap getData() {
            return data;
        }

        public void setData(Bitmap data) {
            this.data = data;
        }

        public String getSavedPath() {
            return savedPath;
        }

        public void setSavedPath(String savedPath) {
            this.savedPath = savedPath;
        }
    }
    private String getTimeStamp(){
         Calendar calendar=Calendar.getInstance();
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss" );
         String date = simpleDateFormat.format(calendar.getTime());
         return date;
    }

    private void uploadMultipleFile(final int  index,
                                    ProgressBar uploadProgress,
                                    TextView uploadText,
                                    ImageView uploadCheck){
        Uri resultUri=Uri.fromFile(new File(errorImageStoredPaths.get(index)));
        StorageReference riversRef = storageReference.child("images/"+resultUri.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(resultUri);
        uploadTask.addOnFailureListener(exception -> {
        }).addOnProgressListener(snapshot -> showLog("Uploading...screens "+index))
                .addOnSuccessListener(taskSnapshot -> {
            showLog("Upload screen success");
            riversRef.getDownloadUrl().addOnSuccessListener(uri -> {
                uploadedImages.add(uri.toString());
                if((errorImageStoredPaths.size() - 1) != index){
                    uploadMultipleFile(index+1,uploadProgress,uploadText,uploadCheck);
                }else {
                    showLog("All upload over");
                    uploadToFireStore(uploadProgress,uploadText,uploadCheck);
                }

            });

        }).addOnFailureListener(e -> {
            showLog("Upload failed");
            return;
        });
    }

    private void uploadToFireStore(ProgressBar uploadProgress,
                                   TextView uploadText,
                                   ImageView uploadCheck) {

        String currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("Letter", selectedLetter);
        data.put("Speed", String.format(INTEGER_FORMAT,traceSpeed)+" dp/s");
        data.put("Total", tracedSquares.size()+"");
        data.put("Ne", spottedSquare.size()+"");
        data.put("Score", score+"");
        data.put("Time",String.format(INTEGER_FORMAT,timeTakenSeconds)+" sec");
        data.put("AttemptedAt",timeStamp);
        data.put("Screens",uploadedImages);
        data.put("user",currentUser);
         firebaseFirestore.collection("Attempts")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    uploadProgress.setVisibility(GONE);
                    uploadCheck.setVisibility(VISIBLE);
                    uploadText.setText(getContext().getString(R.string.uploadsuccess));
                    getContext().startActivity(new Intent(getContext(),SuccessScreen.class)
                    .putExtra("ID",documentReference.getId()));
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    public static int getDIVIDER() {
        return DIVIDER;
    }

    public static int getHardCodedHeight() {
        return HARD_CODED_HEIGHT;
    }

    public static int getHardCodedWidht() {
        return HARD_CODED_WIDHT;
    }

    public static int getCircleWidth() {
        return CIRCLE_WIDTH;
    }

    public static int getCircleRadius() {
        return CIRCLE_RADIUS;
    }

    public static String getInnerRegion1() {
        return INNER_REGION_1;
    }

    public static String getInnerRegion2() {
        return INNER_REGION_2;
    }

    public static String getInnerRegion3() {
        return INNER_REGION_3;
    }

    public static String getOuterRegion1() {
        return OUTER_REGION_1;
    }

    public static String getOuterRegion2() {
        return OUTER_REGION_2;
    }

    public static String getOnLine1() {
        return ON_LINE_1;
    }

    public static String getOnLine2() {
        return ON_LINE_2;
    }

    public static String getOnLine3() {
        return ON_LINE_3;
    }

    public static String getOnLine4() {
        return ON_LINE_4;
    }

    public static String getOnTrace() {
        return ON_TRACE;
    }

    public static String getIntegerFormat() {
        return INTEGER_FORMAT;
    }

    public ViewGroup.LayoutParams getParams() {
        return params;
    }

    public void setParams(ViewGroup.LayoutParams params) {
        this.params = params;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Paint getBrush() {
        return brush;
    }

    public void setBrush(Paint brush) {
        this.brush = brush;
    }

    public int getTraceDPX() {
        return traceDPX;
    }

    public void setTraceDPX(int traceDPX) {
        this.traceDPX = traceDPX;
    }

    public int getTraceDPY() {
        return traceDPY;
    }

    public void setTraceDPY(int traceDPY) {
        this.traceDPY = traceDPY;
    }

    public float getTracePixelX() {
        return tracePixelX;
    }

    public void setTracePixelX(float tracePixelX) {
        this.tracePixelX = tracePixelX;
    }

    public float getTracePixelY() {
        return tracePixelY;
    }

    public void setTracePixelY(float tracePixelY) {
        this.tracePixelY = tracePixelY;
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.backgroundBitmap = backgroundBitmap;
    }

    public int getStartDpX() {
        return startDpX;
    }

    public void setStartDpX(int startDpX) {
        this.startDpX = startDpX;
    }

    public int getStartDpY() {
        return startDpY;
    }

    public void setStartDpY(int startDpY) {
        this.startDpY = startDpY;
    }

    public int getEndDpX() {
        return endDpX;
    }

    public void setEndDpX(int endDpX) {
        this.endDpX = endDpX;
    }

    public int getEndDpY() {
        return endDpY;
    }

    public void setEndDpY(int endDpY) {
        this.endDpY = endDpY;
    }

    public int getPixelBitmap() {
        return pixelBitmap;
    }

    public void setPixelBitmap(int pixelBitmap) {
        this.pixelBitmap = pixelBitmap;
    }

    public int getRedValue() {
        return redValue;
    }

    public void setRedValue(int redValue) {
        this.redValue = redValue;
    }

    public int getBlueValue() {
        return blueValue;
    }

    public void setBlueValue(int blueValue) {
        this.blueValue = blueValue;
    }

    public int getGreenValue() {
        return greenValue;
    }

    public void setGreenValue(int greenValue) {
        this.greenValue = greenValue;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public float getDownPixelX() {
        return downPixelX;
    }

    public void setDownPixelX(float downPixelX) {
        this.downPixelX = downPixelX;
    }

    public float getDownPixelY() {
        return downPixelY;
    }

    public void setDownPixelY(float downPixelY) {
        this.downPixelY = downPixelY;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public ArrayList<String> getTracedRegions() {
        return tracedRegions;
    }

    public void setTracedRegions(ArrayList<String> tracedRegions) {
        this.tracedRegions = tracedRegions;
    }

    public ArrayList<String> getTracedDirections() {
        return tracedDirections;
    }

    public void setTracedDirections(ArrayList<String> tracedDirections) {
        this.tracedDirections = tracedDirections;
    }

    public ArrayList<String> getTracedSquares() {
        return tracedSquares;
    }

    public void setTracedSquares(ArrayList<String> tracedSquares) {
        this.tracedSquares = tracedSquares;
    }

    public String getSelectedLetter() {
        return selectedLetter;
    }

    public void setSelectedLetter(String selectedLetter) {
        this.selectedLetter = selectedLetter;
    }

    public String getTempTracedRegion() {
        return tempTracedRegion;
    }

    public void setTempTracedRegion(String tempTracedRegion) {
        this.tempTracedRegion = tempTracedRegion;
    }

    public String getTempTracedDirection() {
        return tempTracedDirection;
    }

    public void setTempTracedDirection(String tempTracedDirection) {
        this.tempTracedDirection = tempTracedDirection;
    }

    public String getTempTracedSquare() {
        return tempTracedSquare;
    }

    public void setTempTracedSquare(String tempTracedSquare) {
        this.tempTracedSquare = tempTracedSquare;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(long timeTaken) {
        this.timeTaken = timeTaken;
    }

    public int getTraceSpeed() {
        return traceSpeed;
    }

    public void setTraceSpeed(int traceSpeed) {
        this.traceSpeed = traceSpeed;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getTimeTakenSeconds() {
        return timeTakenSeconds;
    }

    public void setTimeTakenSeconds(int timeTakenSeconds) {
        this.timeTakenSeconds = timeTakenSeconds;
    }

    public ArrayList<DynamicPixel> getTracedDPs() {
        return tracedDPs;
    }

    public void setTracedDPs(ArrayList<DynamicPixel> tracedDPs) {
        this.tracedDPs = tracedDPs;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDirectionResult() {
        return directionResult;
    }

    public void setDirectionResult(String directionResult) {
        this.directionResult = directionResult;
    }

    public String getRegionResult() {
        return regionResult;
    }

    public void setRegionResult(String regionResult) {
        this.regionResult = regionResult;
    }

    public String getSquareResult() {
        return squareResult;
    }

    public void setSquareResult(String squareResult) {
        this.squareResult = squareResult;
    }

    public ArrayList<String> getDownloadedRegions() {
        return downloadedRegions;
    }

    public void setDownloadedRegions(ArrayList<String> downloadedRegions) {
        this.downloadedRegions = downloadedRegions;
    }

    public ArrayList<String> getDownloadedSquares() {
        return downloadedSquares;
    }

    public void setDownloadedSquares(ArrayList<String> downloadedSquares) {
        this.downloadedSquares = downloadedSquares;
    }

    public ArrayList<String> getDownloadedDirections() {
        return downloadedDirections;
    }

    public void setDownloadedDirections(ArrayList<String> downloadedDirections) {
        this.downloadedDirections = downloadedDirections;
    }

    public String getDownloadedStartDpX() {
        return downloadedStartDpX;
    }

    public void setDownloadedStartDpX(String downloadedStartDpX) {
        this.downloadedStartDpX = downloadedStartDpX;
    }

    public String getDownloadedStartDpY() {
        return downloadedStartDpY;
    }

    public void setDownloadedStartDpY(String downloadedStartDpY) {
        this.downloadedStartDpY = downloadedStartDpY;
    }

    public DatabaseReference getLetterdataDatabaseReference() {
        return letterdataDatabaseReference;
    }

    public void setLetterdataDatabaseReference(DatabaseReference letterdataDatabaseReference) {
        this.letterdataDatabaseReference = letterdataDatabaseReference;
    }

    public int getxDifference() {
        return xDifference;
    }

    public void setxDifference(int xDifference) {
        this.xDifference = xDifference;
    }

    public ArrayList<String> getScaledSquares() {
        return scaledSquares;
    }

    public void setScaledSquares(ArrayList<String> scaledSquares) {
        this.scaledSquares = scaledSquares;
    }

    public ArrayList<String> getSpottedSquare() {
        return spottedSquare;
    }

    public void setSpottedSquare(ArrayList<String> spottedSquare) {
        this.spottedSquare = spottedSquare;
    }

    public ArrayList<DynamicPixel> getSpottedDps() {
        return spottedDps;
    }

    public void setSpottedDps(ArrayList<DynamicPixel> spottedDps) {
        this.spottedDps = spottedDps;
    }

    public ArrayList<Pixel> getSpottedPixels() {
        return spottedPixels;
    }

    public void setSpottedPixels(ArrayList<Pixel> spottedPixels) {
        this.spottedPixels = spottedPixels;
    }

    public ArrayList<Bitmap> getBitmapCopies() {
        return bitmapCopies;
    }

    public void setBitmapCopies(ArrayList<Bitmap> bitmapCopies) {
        this.bitmapCopies = bitmapCopies;
    }

    public ArrayList<String> getErrorImageStoredPaths() {
        return errorImageStoredPaths;
    }

    public void setErrorImageStoredPaths(ArrayList<String> errorImageStoredPaths) {
        this.errorImageStoredPaths = errorImageStoredPaths;
    }

    public ArrayList<Bitmap> getResultBitmaps() {
        return resultBitmaps;
    }

    public void setResultBitmaps(ArrayList<Bitmap> resultBitmaps) {
        this.resultBitmaps = resultBitmaps;
    }

    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public ArrayList<Pixel> getTracedPixels() {
        return tracedPixels;
    }

    public void setTracedPixels(ArrayList<Pixel> tracedPixels) {
        this.tracedPixels = tracedPixels;
    }

    public int getTOTAL_SCORE() {
        return TOTAL_SCORE;
    }

    public void setTOTAL_SCORE(int TOTAL_SCORE) {
        this.TOTAL_SCORE = TOTAL_SCORE;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public FirebaseStorage getFirebaseStorage() {
        return firebaseStorage;
    }

    public void setFirebaseStorage(FirebaseStorage firebaseStorage) {
        this.firebaseStorage = firebaseStorage;
    }

    public StorageReference getStorageReference() {
        return storageReference;
    }

    public void setStorageReference(StorageReference storageReference) {
        this.storageReference = storageReference;
    }

    public ArrayList<String> getUploadedImages() {
        return uploadedImages;
    }

    public void setUploadedImages(ArrayList<String> uploadedImages) {
        this.uploadedImages = uploadedImages;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }

    public void setFirebaseFirestore(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    public static int getTRACE() {
        return TRACE;
    }

    public static int getDECODE() {
        return DECODE;
    }

    public ArrayList<String> getRealtimeRegions() {
        return realtimeRegions;
    }

    public void setRealtimeRegions(ArrayList<String> realtimeRegions) {
        this.realtimeRegions = realtimeRegions;
    }

    public int getMODE() {
        return MODE;
    }

    public void setMODE(int MODE) {
        this.MODE = MODE;
    }
}

