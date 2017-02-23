package com.somitsolutions.android.spectrumanalyzer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

import ca.uol.aig.fftpack.RecordTask;
import ca.uol.aig.fftpack.view.ScaleImageView;
import ca.uol.aig.fftpack.body;

public class SoundRecordAndAnalysisActivity extends Activity implements OnClickListener {

	Button startStopButton;
	RecordTask recordTask;
	body body;
	int BICEP_FRQ=14000;
	int TRICEPS_FRQ=10000;
	int FOREARM_FRQ=20000;
	Drawable BICEP;
	Drawable TRICEPS;
	Drawable FOREARM;
	ImageView imageViewDisplaySectrum;
	ImageView imageViewBicep;
	ImageView imageViewTriceps;
	ImageView imageViewForearm;
	ScaleImageView imageViewScale;
	Bitmap bitmapDisplaySpectrum;
	Canvas canvasDisplaySpectrum;
	Paint paintSpectrumDisplay;
	LinearLayout main;
	RelativeLayout bparts;
	int width;
	int height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Display display = getWindowManager().getDefaultDisplay();

		DisplayMetrics displayM = this.getResources().getDisplayMetrics();

		width = displayM.widthPixels;
		height = displayM.heightPixels;

		setContentView(R.layout.main);


	}

	public void onClick(View v) {
		if (recordTask.isStarted()) {
			startStopButton.setText("Start");
			recordTask.setCancel();
			//canvasDisplaySpectrum.drawColor(Color.BLACK);
		} else {
			startStopButton.setText("Stop");
			body = new body(BICEP_FRQ,TRICEPS_FRQ,FOREARM_FRQ,BICEP,TRICEPS,FOREARM);
			recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay,
					imageViewDisplaySectrum, bparts, imageViewBicep, imageViewTriceps, imageViewForearm, width, body);
			recordTask.execute();

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

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		bparts.setLayoutParams(params);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int imageResource;
		imageViewDisplaySectrum = new ImageView(this);
		imageViewBicep= new ImageView(this);
		imageViewTriceps= new ImageView(this);
		imageViewForearm=new ImageView(this);

		bitmapDisplaySpectrum = Bitmap.createBitmap(width, 300, Bitmap.Config.ARGB_8888);
		LinearLayout.LayoutParams layoutParams_imageViewScale;
		canvasDisplaySpectrum = new Canvas(bitmapDisplaySpectrum);
		paintSpectrumDisplay = new Paint();
		paintSpectrumDisplay.setColor(Color.GREEN);
		imageViewDisplaySectrum.setImageBitmap(bitmapDisplaySpectrum);
		imageViewDisplaySectrum.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		layoutParams_imageViewScale = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		imageViewDisplaySectrum.setId(View.NO_ID);

		main.addView(imageViewDisplaySectrum);


		//create ImageView for 3 muscles


		String uri = "@drawable/bicep";
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		BICEP = ContextCompat.getDrawable(this, imageResource);
		imageViewBicep.setImageDrawable(BICEP);
		imageViewBicep.setLayoutParams((findViewById(R.id.imageView1)).getLayoutParams());

		//imageViewTriceps= (ImageView)main.findViewById(R.id.imageView2);



		uri = "@drawable/triceps";
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		TRICEPS = ContextCompat.getDrawable(this, imageResource);
		imageViewTriceps.setImageDrawable(TRICEPS);
		imageViewTriceps.setLayoutParams((findViewById(R.id.imageView2)).getLayoutParams());


		//imageViewForearm= (ImageView)main.findViewById(R.id.imageView3);


		uri = "@drawable/forearm";
		imageResource = getResources().getIdentifier(uri, null, getPackageName());
		FOREARM = ContextCompat.getDrawable(this, imageResource);
		imageViewForearm.setImageDrawable(FOREARM);
		imageViewForearm.setLayoutParams((findViewById(R.id.imageView3)).getLayoutParams());

		/*bparts.addView(imageViewBicep);
		bparts.addView(imageViewTriceps);
		bparts.addView(imageViewForearm);*/



		//Scale
		imageViewScale = new ScaleImageView(this);
		imageViewScale.setLayoutParams(layoutParams_imageViewScale);
		imageViewScale.setId(View.NO_ID);
//		imageViewScale.setPadding(0,0,0,20);
		main.addView(imageViewScale);

		//Button
		startStopButton = new Button(this);
		startStopButton.setText("Start");
		startStopButton.setOnClickListener(this);
		startStopButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		main.addView(startStopButton);

		main.addView(bparts);

		setContentView(main);




		recordTask = new RecordTask(canvasDisplaySpectrum, paintSpectrumDisplay,
				imageViewDisplaySectrum, bparts, imageViewBicep, imageViewTriceps, imageViewForearm, width, body);
	}

	@Override
	public void onBackPressed() {
		try {
			if (recordTask.isStarted()) {
				recordTask.setCancel();
				startStopButton.setText("Start");
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
