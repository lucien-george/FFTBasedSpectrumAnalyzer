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
import ca.uol.aig.fftpack.view.ScaleImageView;
import ca.uol.aig.fftpack.body;

public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener {

	Button UpdateButton;
	Button RECButton;
	Button ReplayButton;
	RecordTask recordTask;
	body body;
	int BICEP_FRQ=7000;
	int TRICEPS_FRQ=10000;
	int FOREARM_FRQ=16000;
	int DIST_SENS_FRQ=12000;
	public int Bicep_textColor = Color.CYAN;
	public int Triceps_textColor = Color.YELLOW;
	public int Forearm_textColor = Color.MAGENTA;
	Drawable BICEP;
	Drawable TRICEPS;
	Drawable FOREARM;
	ImageView imageViewDisplaySectrum;
	ImageView imageViewBody;
	ImageView imageViewBicep;
	ImageView imageViewTriceps;
	ImageView imageViewForearm;
	EditText BicepTxt;
	String stringB;
	String stringT;
	String stringF;
	EditText TricepsTxt;
	EditText ForearmTxt;
	TextView BicepT;
	TextView TricepsT;
	TextView ForearmT;
	ScaleImageView imageViewScale;
	Bitmap bitmapDisplaySpectrum;
	Canvas canvasDisplaySpectrum;
	Canvas canvasBicep;
	Canvas canvasTriceps;
	Canvas canvasForearm;
	Paint paintSpectrumDisplay;
	Paint paintBicep;
	Paint paintTriceps;
	Paint paintForearm;
	LinearLayout main;
	RelativeLayout bparts;
	RelativeLayout txtFreq;
	RelativeLayout txt;
	int width;
	int height;
	DisplayMetrics displayM;
	ImageView imageViewdrawBody;
	drawBody drawBody;
	public static boolean isRecording=false;
	public static boolean isReplaying=false;
	public static boolean recorded =false;
	public static boolean replayed =false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Display display = getWindowManager().getDefaultDisplay();

		displayM = this.getResources().getDisplayMetrics();

		width = displayM.widthPixels;
		height = displayM.heightPixels;

		setContentView(R.layout.main);


	}

	protected boolean shouldAskPermissions() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
	}

	@SuppressLint("NewApi")
	protected void askPermissions() {
		String[] permissions = {
				"android.permission.READ_EXTERNAL_STORAGE",
				"android.permission.WRITE_EXTERNAL_STORAGE"
		};
		int requestCode = 200;
		requestPermissions(permissions, requestCode);
	}

	public void onClick(View v) {
		boolean active;
		if(v.equals(UpdateButton)) {
			 //Test color transition
			active = true;
			drawBody.setPaintBicep(active);
			recordTask.setCancel();

			stringB = BicepTxt.getText().toString();
			stringT = TricepsTxt.getText().toString();
			stringF = ForearmTxt.getText().toString();

			if (!(TextUtils.isEmpty(stringB)))
				BICEP_FRQ = Integer.parseInt(stringB);
			if (!(TextUtils.isEmpty(stringT)))
				TRICEPS_FRQ = Integer.parseInt(stringT);
			if (!(TextUtils.isEmpty(stringF)))
				FOREARM_FRQ = Integer.parseInt(stringF);
			body = new body(BICEP_FRQ, TRICEPS_FRQ, FOREARM_FRQ, DIST_SENS_FRQ, BICEP, TRICEPS, FOREARM, Bicep_textColor, Triceps_textColor, Forearm_textColor /*,imageViewBicep, imageViewTriceps, imageViewForearm,
				paintBicep,paintTriceps, paintForearm, width, height*/);

			recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay,
					imageViewDisplaySectrum, bparts,
					imageViewBicep, imageViewTriceps, imageViewForearm, width, body, drawBody, imageViewdrawBody,isRecording,isReplaying);
			recordTask.execute();

		}else if(v.equals(RECButton) ){
			active = false;
			drawBody.setPaintBicep(active);
			if (shouldAskPermissions()) {
				askPermissions();
			}

			if(recordTask.isRecording) {
				try {
					recordTask.myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isRecording = false;
				RECButton.setText("Start Recording");

			}


			if(recordTask.isReplaying){
				try {

					recordTask.myInReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			recordTask.isRecording=true;
			recordTask.isReplaying=false;
			RECButton.setText("Stop Recording");


		}else if(v.equals(ReplayButton) ) {


			if(recordTask.isRecording) {
				try {
					recordTask.myOutWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				isRecording = false;
				RECButton.setText("Start Recording");

			}


			if(recordTask.isReplaying){
				try {
					recordTask.myInReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				replayed = false;
				ReplayButton.setText("Start Replaying");
				isReplaying = false;
			}
			recordTask.isRecording = false;
			recordTask.isReplaying = true;
			ReplayButton.setText("Stop Replay");

		}



	}

	@Override
	public void onStart() {
		super.onStart();

		main = new LinearLayout(this);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		main.setOrientation(LinearLayout.VERTICAL);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		bparts= new RelativeLayout(this);
		txtFreq= new RelativeLayout(this);
		txt = new RelativeLayout(this);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		bparts.setLayoutParams(params);
		txt.setLayoutParams(params);
		txtFreq.setLayoutParams(params);
		txtFreq.setPadding(0,0,0,30);


		int imageResource;
		imageViewDisplaySectrum = new ImageView(this);
		imageViewBicep= new ImageView(this);
		imageViewTriceps= new ImageView(this);
		imageViewForearm=new ImageView(this);


		BicepT = new TextView(this);
		TricepsT = new TextView(this);
		ForearmT = new TextView(this);


		BicepTxt= new EditText(this);
		TricepsTxt= new EditText(this);
		ForearmTxt= new EditText(this);


		bitmapDisplaySpectrum = Bitmap.createBitmap(width, 300, Bitmap.Config.ARGB_8888);
		LinearLayout.LayoutParams layoutParams_imageViewScale;
		canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
		paintSpectrumDisplay = new Paint();
		paintSpectrumDisplay.setColor(Color.GREEN);



		imageViewTriceps.setImageBitmap(bitmapDisplaySpectrum);
		canvasTriceps = new Canvas(bitmapDisplaySpectrum);
		paintTriceps = new Paint();
		paintTriceps.setColor(Color.GREEN);

		imageViewBicep.setImageBitmap(bitmapDisplaySpectrum);
		canvasBicep = new Canvas(bitmapDisplaySpectrum);
		paintBicep = new Paint();
		paintBicep.setColor(Color.GREEN);

		imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
		imageViewDisplaySectrum.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layoutParams_imageViewScale = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		imageViewDisplaySectrum.setId(View.NO_ID);

		main.addView(imageViewDisplaySectrum);

		//create text box for muscle freq input
		BicepT.setLayoutParams((findViewById(R.id.BicepT)).getLayoutParams());
		TricepsT.setLayoutParams((findViewById(R.id.TricepsT)).getLayoutParams());
		ForearmT.setLayoutParams((findViewById(R.id.ForarmT)).getLayoutParams());
		BicepT.setText("Bicep");
		TricepsT.setText("Triceps");
		ForearmT.setText("Forearm");
		BicepT.setTextColor(Color.CYAN);
		TricepsT.setTextColor(Color.YELLOW);
		ForearmT.setTextColor(Color.MAGENTA);

		txt.addView(BicepT);
		txt.addView(TricepsT);
		txt.addView(ForearmT);

		//create text field input for each muscle
		BicepTxt.setLayoutParams((findViewById(R.id.BicepTxt)).getLayoutParams());
		TricepsTxt.setLayoutParams((findViewById(R.id.TricepsTxt)).getLayoutParams());
		ForearmTxt.setLayoutParams((findViewById(R.id.ForarmTxt)).getLayoutParams());
		BicepTxt.setHint("insert Frq(Hz)");
		TricepsTxt.setHint("insert Frq(Hz)");
		ForearmTxt.setHint("insert Frq(Hz)");
		BicepTxt.setHintTextColor(Color.LTGRAY);
		TricepsTxt.setHintTextColor(Color.LTGRAY);
		ForearmTxt.setHintTextColor(Color.LTGRAY);
		BicepTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
		TricepsTxt.setInputType(InputType.TYPE_CLASS_NUMBER);
		ForearmTxt.setInputType(InputType.TYPE_CLASS_NUMBER);



		txtFreq.addView(BicepTxt);
		txtFreq.addView(TricepsTxt);
		txtFreq.addView(ForearmTxt);

		bparts.addView(imageViewBicep);

		//bparts.addView(imageViewForearm);
		bparts.addView(imageViewTriceps);


		//Scale
		imageViewScale = new ScaleImageView(this);
		imageViewScale.setLayoutParams(layoutParams_imageViewScale);
		imageViewScale.setId(View.NO_ID);
//		imageViewScale.setPadding(0,0,0,20);
		main.addView(imageViewScale);

		main.addView(txt);
		main.addView(txtFreq);
		//Button
		UpdateButton = new Button(this);
		UpdateButton.setText("Update freq");
		UpdateButton.setOnClickListener(this);
		UpdateButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		RECButton = new Button(this);
		RECButton.setText("Start Recording");
		RECButton.setOnClickListener(this);
		RECButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		ReplayButton = new Button(this);
		ReplayButton.setText("Replay Records");
		ReplayButton.setOnClickListener(this);
		ReplayButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		main.addView(UpdateButton);
		main.addView(RECButton);
		main.addView(ReplayButton);

		drawBody=new drawBody(this);
		imageViewdrawBody = drawBody;
		imageViewdrawBody.setLayoutParams(layoutParams_imageViewScale);

		//converting dp in pixel
		float scale = getResources().getDisplayMetrics().density;
		int dpAsPixels = (int) (50*scale + 0.5f);

		imageViewdrawBody.setPadding(0,dpAsPixels,0,0);
		imageViewdrawBody.setId(View.NO_ID);
		main.addView(imageViewdrawBody);


		bparts.removeView(imageViewBicep);

		bparts.removeView(imageViewForearm);
		bparts.removeView(imageViewTriceps);



		setContentView(main);


		body = new body(BICEP_FRQ,TRICEPS_FRQ,FOREARM_FRQ, DIST_SENS_FRQ, BICEP,TRICEPS,FOREARM , Bicep_textColor, Triceps_textColor, Forearm_textColor /*,imageViewBicep, imageViewTriceps, imageViewForearm,
				paintBicep,paintTriceps, paintForearm, width, height*/);
		recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay,
				imageViewDisplaySectrum, bparts, imageViewBicep, imageViewTriceps, imageViewForearm,
				width, body, drawBody, imageViewdrawBody,isRecording,isReplaying);

		recordTask.execute();
	}

	@Override
	public void onBackPressed() {
		try {
			if (recordTask.isStarted()) {
				recordTask.setCancel();
				UpdateButton.setText("Start");
			} else {
				super.onBackPressed();
			}
		} catch (IllegalStateException e) {
			Log.e("Stop failed", e.toString());

		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	@Override
	public void onStop() {
		super.onStop();
		if (recordTask != null) {
			recordTask.cancel(true);
		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (recordTask != null) {
			recordTask.cancel(true);
		}
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
