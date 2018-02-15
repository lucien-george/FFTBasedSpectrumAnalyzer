package com.somitsolutions.android.spectrumanalyzer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.lang.String;

import ca.uol.aig.fftpack.RecordTask;
import ca.uol.aig.fftpack.drawBody;
import ca.uol.aig.fftpack.view.HorizontalScaleImageView;
import ca.uol.aig.fftpack.body;

public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener {

	Button UpdateButton; //button updating frequencies for each muscle
	Button RECButton; // start recording button
	Button ReplayButton; // replay saved movement button
	RecordTask recordTask; //recordTask instantiated
	body body; // body object instantiated
	int BICEP_FRQ= 100 /*7000*/; // bicep frequency
	int TRICEPS_FRQ= 1000/*10000*/; // tricep frequency
	int FOREARM_FRQ= 2000/*16000*/; // forearm frequency
	int DIST_SENS_FRQ=12000;
	int bitmap_height = 500;
	public int Bicep_textColor = Color.CYAN , Triceps_textColor = Color.YELLOW , Forearm_textColor = Color.MAGENTA; // text color
	Drawable BICEP , TRICEPS , FOREARM;
	ImageView imageViewDisplaySpectrum , imageViewBody , imageViewBicep , imageViewTriceps , imageViewForearm , imageViewdrawBody;
	EditText BicepTxt ,	 TricepsTxt , ForearmTxt; //text fields for frequencies
	String stringB , stringT , stringF;
	TextView BicepT , TricepsT , ForearmT;
	HorizontalScaleImageView horizontalImageViewScale;
	Bitmap bitmapDisplaySpectrum;
	Canvas canvasDisplaySpectrum , canvasBicep , canvasTriceps , canvasForearm; // canvas for each muscle
	Paint paintSpectrumDisplay , paintBicep , paintTriceps , paintForearm;
	LinearLayout main; // main layout
	RelativeLayout bparts , txtFreq , txt; // relative layouts
	int width , height; // width & height of screen
	DisplayMetrics displayM; // get height and widths of screen
	drawBody drawBody; // draw body instantiated
	public static boolean isRecording=false , isReplaying=false , recorded =false , replayed =false;

	@Override
	public void onCreate(Bundle savedInstanceState) { // whenever app is running
		super.onCreate(savedInstanceState);

		displayM = this.getResources().getDisplayMetrics(); // get screen width and height
		width = displayM.widthPixels; //screen width
		height = displayM.heightPixels; //screen height
		setContentView(R.layout.main); // call layout used for activity
	}

	protected boolean shouldAskPermissions() { // check that the device runs on Lollipop_MR1 or higher
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	@SuppressLint("NewApi")
	protected void askPermissions() { // asks permissions to read and write external storage
		String[] permissions = {
				"android.permission.READ_EXTERNAL_STORAGE",
				"android.permission.WRITE_EXTERNAL_STORAGE"
		};
		int requestCode = 200;
		requestPermissions(permissions, requestCode);
	}

	public void onClick(View v) { // takes care of button clicks
		if(v.equals(UpdateButton)) { // if click on updateButton is detected
			recordTask.setCancel(); // cancel recordTask

			stringB = BicepTxt.getText().toString(); stringT = TricepsTxt.getText().toString(); stringF = ForearmTxt.getText().toString(); // gets the values entered for the frequencies

			if (!(TextUtils.isEmpty(stringB))) // if the field is not empty parse it
				BICEP_FRQ = Integer.parseInt(stringB);
			if (!(TextUtils.isEmpty(stringT)))
				TRICEPS_FRQ = Integer.parseInt(stringT); // if the field is not empty parse it
			if (!(TextUtils.isEmpty(stringF)))
				FOREARM_FRQ = Integer.parseInt(stringF); // if the field is not empty parse it
			body = new body(BICEP_FRQ, TRICEPS_FRQ, FOREARM_FRQ, DIST_SENS_FRQ, BICEP, TRICEPS, FOREARM, Bicep_textColor, Triceps_textColor, Forearm_textColor); //constructor

			recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay, imageViewDisplaySpectrum, bparts, imageViewBicep, imageViewTriceps, imageViewForearm, width, body, drawBody, imageViewdrawBody,isRecording,isReplaying); // constructor
			recordTask.execute(); // execute record task

		}else if(v.equals(RECButton) ){ //if click detected on record button
			if (shouldAskPermissions()) { // check OS version
				askPermissions(); // ask permission to read and write external storage
			}

			if(recordTask.isRecording) { // if task is recording
				try {
					recordTask.myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isRecording = false; // boolean set to false
				RECButton.setText("Start Recording"); // button text updated

			}


			if(recordTask.isReplaying){ // if task is being replayed
				try {

					recordTask.myInReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			recordTask.isRecording=true; // boolean set to true
			recordTask.isReplaying=false; // boolean set to false
			RECButton.setText("Stop Recording"); // button text updated

		}else if(v.equals(ReplayButton) ) { // if click on replay button is detected


			if(recordTask.isRecording) { // if task is recording
				try {
					recordTask.myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isRecording = false; // set boolean to false
				RECButton.setText("Start Recording"); // button text updated

			}

			if(recordTask.isReplaying){ // if task is being replayed
				try {
					recordTask.myInReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				replayed = false; // boolean set to false
				ReplayButton.setText("Start Replaying"); // button text updated
				isReplaying = false; // boolean set to false
			}
			recordTask.isRecording = false; // boolean set to false
			recordTask.isReplaying = true; // boolean set to true
			ReplayButton.setText("Stop Replay"); // button text updated
		}
	}

	@Override
	public void onStart() { // when app is launched
		super.onStart();

		main = new LinearLayout(this); // initializing linear layout
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT)); // setting parameters widths and height
		main.setOrientation(LinearLayout.VERTICAL); // setting orientation to vertical
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // set orientation to portrait everytime the app launches
		bparts= new RelativeLayout(this) ; txtFreq= new RelativeLayout(this) ; txt = new RelativeLayout(this); // initializing relative layout

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // set width and height
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE); // align to the bottom of parent layout
		bparts.setLayoutParams(params); txt.setLayoutParams(params); txtFreq.setLayoutParams(params); // set layout
		txtFreq.setPadding(0,0,0,30);

		imageViewDisplaySpectrum = new ImageView(this) ; imageViewBicep= new ImageView(this) ; imageViewTriceps= new ImageView(this) ; imageViewForearm=new ImageView(this); // ImageView initialized

		BicepT = new TextView(this) ; TricepsT = new TextView(this) ; ForearmT = new TextView(this); // TextViews initialized

		BicepTxt= new EditText(this) ; TricepsTxt= new EditText(this) ; ForearmTxt= new EditText(this); // TextViews initialized

		// Frequency spectrum is displayed
		bitmapDisplaySpectrum = Bitmap.createBitmap(width, bitmap_height, Bitmap.Config.ARGB_8888); //Creating spectrum with specific dimensions
		LinearLayout.LayoutParams layoutParams_imageViewScale;
		canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
		paintSpectrumDisplay = new Paint();
		paintSpectrumDisplay.setColor(Color.GREEN); //color of the spectrum


		imageViewTriceps.setImageBitmap(bitmapDisplaySpectrum); // sets bitmap as content of this image view
		canvasTriceps = new Canvas(bitmapDisplaySpectrum); // initializing canvas
		paintTriceps = new Paint(); // initializing paint
		paintTriceps.setColor(Color.GREEN); // set paint color to green

		imageViewBicep.setImageBitmap(bitmapDisplaySpectrum); // sets bitmap as content of this image view
		canvasBicep = new Canvas(bitmapDisplaySpectrum); // initializing canvas
		paintBicep = new Paint(); // initializing paint
		paintBicep.setColor(Color.GREEN); // set paint color to green

		imageViewDisplaySpectrum.setImageBitmap(bitmapDisplaySpectrum); // set bitmap as content of this image view
		imageViewDisplaySpectrum.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); // set width and height
		layoutParams_imageViewScale = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // create new layout parameters

		imageViewDisplaySpectrum.setId(View.NO_ID); // image view has no ID

		main.addView(imageViewDisplaySpectrum); // add the image view to the main layout

		BicepT.setLayoutParams((findViewById(R.id.BicepT)).getLayoutParams()) ; TricepsT.setLayoutParams((findViewById(R.id.TricepsT)).getLayoutParams()) ; ForearmT.setLayoutParams((findViewById(R.id.ForarmT)).getLayoutParams()); // set parameters for text views
		BicepT.setText("Bicep" ); TricepsT.setText("Triceps") ; ForearmT.setText("Forearm"); // set texts for text views
		BicepT.setTextColor(Color.CYAN) ; TricepsT.setTextColor(Color.YELLOW) ; ForearmT.setTextColor(Color.MAGENTA); // set color for text views

		txt.addView(BicepT) ; txt.addView(TricepsT) ; txt.addView(ForearmT); // add text views to layout txt

		BicepTxt.setLayoutParams((findViewById(R.id.BicepTxt)).getLayoutParams()) ; TricepsTxt.setLayoutParams((findViewById(R.id.TricepsTxt)).getLayoutParams()) ; ForearmTxt.setLayoutParams((findViewById(R.id.ForarmTxt)).getLayoutParams()); // set parameters for edit texts
		BicepTxt.setHint("insert Frq(Hz)") ; TricepsTxt.setHint("insert Frq(Hz)") ; ForearmTxt.setHint("insert Frq(Hz)"); // set hint in text views
		BicepTxt.setHintTextColor(Color.LTGRAY) ; TricepsTxt.setHintTextColor(Color.LTGRAY) ; ForearmTxt.setHintTextColor(Color.LTGRAY); // set color of hints
		BicepTxt.setInputType(InputType.TYPE_CLASS_NUMBER) ; TricepsTxt.setInputType(InputType.TYPE_CLASS_NUMBER) ; ForearmTxt.setInputType(InputType.TYPE_CLASS_NUMBER); // set input type as numbers

		txtFreq.addView(BicepTxt); txtFreq.addView(TricepsTxt); txtFreq.addView(ForearmTxt); // add edit texts to layout txtFreq

		bparts.addView(imageViewBicep); bparts.addView(imageViewTriceps); // add image view bicep and triceps to layout bparts

		horizontalImageViewScale = new HorizontalScaleImageView(this); // create new scale under spectrum
		horizontalImageViewScale.setLayoutParams(layoutParams_imageViewScale); // set parameters for scale
		horizontalImageViewScale.setId(View.NO_ID); // scale has no ID

		main.addView(horizontalImageViewScale); // add scale to main layout
		main.addView(txt) ; main.addView(txtFreq); // add the relative layouts to the main layout

		//Button
		UpdateButton = new Button(this); // create new update button
		UpdateButton.setText("Update freq"); // set text of update button
		UpdateButton.setOnClickListener(this); // set a click listener
		UpdateButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); // set width and height

		RECButton = new Button(this); // create new record button
		RECButton.setText("Start Recording"); // set text of record button
		RECButton.setOnClickListener(this); // set a click listener
		RECButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); // set width and height

		ReplayButton = new Button(this); // set new replay button
		ReplayButton.setText("Replay Records"); // set text for replay button
		ReplayButton.setOnClickListener(this); // set a click listener
		ReplayButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); // set width and height

		main.addView(UpdateButton) ; main.addView(RECButton) ; main.addView(ReplayButton); // add buttons to main layout

		drawBody=new drawBody(this); // initialize drawbody
		imageViewdrawBody = drawBody; // set image view equal to drawbody
		imageViewdrawBody.setLayoutParams(layoutParams_imageViewScale); // set width and height

		float scale = getResources().getDisplayMetrics().density; // gets density of display
		int dpAsPixels = (int) (50*scale + 0.5f); // convert dps to pixels

		imageViewdrawBody.setPadding(0,dpAsPixels,0,0); // set padding
		imageViewdrawBody.setId(View.NO_ID); // image view has no id
		main.addView(imageViewdrawBody); // add image view to main layout

		bparts.removeView(imageViewBicep) ; bparts.removeView(imageViewForearm) ; bparts.removeView(imageViewTriceps); // remvoe views from layout bparts

		setContentView(main);

		body = new body(BICEP_FRQ,TRICEPS_FRQ,FOREARM_FRQ, DIST_SENS_FRQ, BICEP,TRICEPS,FOREARM , Bicep_textColor, Triceps_textColor, Forearm_textColor);
		recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay, imageViewDisplaySpectrum, bparts, imageViewBicep, imageViewTriceps, imageViewForearm, width, body, drawBody, imageViewdrawBody,isRecording,isReplaying);
		recordTask.execute();
	}

	@Override
	public void onBackPressed() { // when back button is pressed
		try {
			if (recordTask.isStarted()) { // if recording has started
				recordTask.setCancel(); // cancel recording
				UpdateButton.setText("Start"); // change text of update button
			} else {
				super.onBackPressed();
			}
		} catch (IllegalStateException e) {
			Log.e("Stop failed", e.toString());

		}
		Intent intent = new Intent(Intent.ACTION_MAIN); // initialize intent
		intent.addCategory(Intent.CATEGORY_HOME); // first activity that is displayed when app boots
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this activity will become the start of a new task on this history stack
		startActivity(intent); // start new activity
	}

	@Override
	public void onStop() { // when app stops
		super.onStop();
		if (recordTask != null) { // if recordTask is not null
			recordTask.cancel(true); // cancel recording
		}
		Intent intent = new Intent(Intent.ACTION_MAIN); // initialize intent
		intent.addCategory(Intent.CATEGORY_HOME); // first activity that is displayed when app boots
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this activity will become the start of a new task on this history stack
		startActivity(intent); // start new activity
	}

	@Override
	protected void onDestroy() { // when app crashes?
		super.onDestroy();
		if (recordTask != null) { // if recordTask is not null
			recordTask.cancel(true); // cancel recording
		}
		Intent intent = new Intent(Intent.ACTION_MAIN); // initialize intent
		intent.addCategory(Intent.CATEGORY_HOME); // first activity that is displayed when app boots
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this activity will become the start of a new task on this history stack
		startActivity(intent); // start new activity
	}
}