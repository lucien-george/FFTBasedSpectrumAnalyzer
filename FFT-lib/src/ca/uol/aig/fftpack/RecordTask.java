package ca.uol.aig.fftpack;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Scanner;


//Async is used to avoid dealing with threads concurrences and therefore handlers.
public class RecordTask extends AsyncTask<Void, double[], Boolean> {


	private final int width;
	private final ImageView imageViewDisplaySpectrum , imageViewBicep , imageViewTriceps , imageViewForearm;
	private final Canvas canvasDisplaySpectrum;
	private final Paint paintSpectrumDisplay , paintFreqLines_B , paintFreqLines_T , paintFreqLines_F , paintMinMagnitude , paintMaxMagnitude;
	private RelativeLayout bparts;
	private RealDoubleFFT transformer;
	private boolean started = false , CANCELLED_FLAG = false;
	private AudioRecord audioRecord;
	private final body body;
	private drawBody drawBody;
	private ImageView imageViewdrawBody;
	private double[] re; //real part
	private double[] im; // imaginary part
	private double[] magnitude; // magnitude
	private double[] frequency; // frequency
	private int max_col = 255 , lim_min = 500 - 1 , lim_max = 500 - 290 , high_magnitude = 150 , medium_magnitude = 75 , blockSize = 256 , sampleRate =42000 , origin = 0 , muscles_counterThreshold=1;

	public final static String IO_FILENAME= "KISDataREC";
	public static FileOutputStream fOut;
	public static FileInputStream fIn;
	public static File file;
	public static InputStreamReader myInReader;
	public static OutputStreamWriter myOutWriter;
	public static boolean isRecording=false , wasRecording=false , wasRepaying= false , isReplaying=false;

	BufferedReader reader = null;
	boolean wasRec = false;

	// changed from "CHANNEL_CONFIGURATION_MONO" to "CHANNEL_IN_MONO", newest version
	// defines the audio encoding format used throughout the recording task
	int length=0 , j=0 , channelConfiguration = AudioFormat.CHANNEL_IN_MONO , audioEncoding = AudioFormat.ENCODING_PCM_16BIT , counter = 0;

	final int THRESHOLD = 10;
	boolean BwasActive = false , TwasActive = false , FwasActive = false;
	float forearmDegreeIncrease = 0 , new_forearmDegreeIncrease=0;
	double maxMag=20.0;
	double[] averageDist = {0,0,0,0,0,0,0,0,0,0};

	//constructor initializing RecordTask
	public RecordTask(Canvas canvasDisplaySpectrum, Paint paintSpectrumDisplay, ImageView imageViewDisplaySectrum, RelativeLayout bparts, ImageView imageViewBicep, ImageView imageViewTriceps, ImageView imageViewForearm, int width, body body, drawBody drawBody, ImageView imageViewdrawBody,boolean isRecording,boolean isReplaying) {
		this.width = width;
		blockSize = width / 2;
		this.imageViewDisplaySpectrum = imageViewDisplaySectrum;
		this.bparts = bparts;
		this.imageViewBicep= imageViewBicep;
		this.imageViewTriceps= imageViewTriceps;
		this.imageViewForearm = imageViewForearm;
		this.canvasDisplaySpectrum = canvasDisplaySpectrum;
		this.paintSpectrumDisplay = paintSpectrumDisplay;
		this.body = body;
		this.drawBody = drawBody;
		this.imageViewdrawBody=imageViewdrawBody;

		paintFreqLines_B = new Paint(); // initializing paint
		paintFreqLines_B.setColor(body.Bicep_textColor); // setting a color to the bicep frequency line
		paintFreqLines_B.setStyle(Paint.Style.FILL_AND_STROKE); // setting style
		paintFreqLines_B.setStrokeWidth(3); // setting width

		paintFreqLines_T = new Paint(); // initializing paint
		paintFreqLines_T.setColor(body.Triceps_textColor); // setting a color to the tricep frequency line
		paintFreqLines_T.setStyle(Paint.Style.FILL_AND_STROKE); // setting style
		paintFreqLines_T.setStrokeWidth(3); // setting width

		paintFreqLines_F = new Paint(); // initializing paint
		paintFreqLines_F.setColor(body.Forearm_textColor); // setting a color to the forearm frequency line
		paintFreqLines_F.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFreqLines_F.setStrokeWidth(3); // setting width

		paintMinMagnitude = new Paint(); // initializing paint
		paintMinMagnitude.setColor(Color.GREEN); // setting a color to the minimum magnitude line
		paintMinMagnitude.setStyle(Paint.Style.STROKE); // setting style
		paintMinMagnitude.setStrokeWidth(3); // setting width

		paintMaxMagnitude = new Paint(); // initializing paint
		paintMaxMagnitude.setColor(Color.RED); // setting a color to the maximum magnitude line
		paintMaxMagnitude.setStyle(Paint.Style.STROKE); // setting style
		paintMaxMagnitude.setStrokeWidth(3); // setting width


	}

	//invoked on the UI thread before the task is executed <-- "AsyncTast" extension
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		transformer = new RealDoubleFFT(blockSize); // initializing fast fourier transform
	}

	//involves background operation that can take time <-- "AsyncTast" extension
	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("Recording doBackground", params.toString());
		file= init_writeFile(); // initiation of file writing
		try {
			if(!file.exists()) // check if file doesn't exist
				file.createNewFile(); // create a new file

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut = new FileOutputStream(file); // output file
			myOutWriter = new OutputStreamWriter(fOut); // output writer?

			fIn = new FileInputStream(file); // input file
			myInReader = new InputStreamReader(fIn); // input reader?
		} catch (IOException e) {
			e.printStackTrace();
		}

		int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding); // set buffer size
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfiguration, audioEncoding, bufferSize); // initializing audioRecord
		int read = 0 , counter = 0;
		long total=0;
		boolean run = true;
		short[] buffer = new short[blockSize];
		byte[] buff = new byte[2 * blockSize];
		double[] toTransform = new double[blockSize];

		try {
			audioRecord.startRecording(); // start recording
			started = true; // record started recording
		} catch (IllegalStateException e) {
			Log.e("Recording failed", e.toString());
		}

		while (started) {
			if (isCancelled() || (CANCELLED_FLAG)) { // if record is cancelled
				started = false; // then started is false
				Log.d("doInBackground", "Cancelling the RecordTask");
				break;
			} else {
				if(!isReplaying) {
					/* Reads the data from the microphone. it takes in data
					 * to the size of the window "blockSize". The data is then
					 * given in to audioRecord. The int returned is the number
					 * of bytes that were read*/
					read = audioRecord.read(buffer, 0, blockSize);
					Log.v("Read ", Integer.toString(read));
					if (isRecording) { // if is recording
						ByteBuffer.wrap(buff).asShortBuffer().put(buffer); // write to buffer?
						wasRecording = true;
						try {

							if (total + read > 4294967295L) { // Write as many bytes as we can before hitting the max size
								for (int i = 0; i < read && total <= 4294967295L; i++, total++) {
									fOut.write(buff[i]);
								}
								isRecording = false; // is recording is false because file limit is reached
								Log.v("File ", "hit file limit");
							} else {
								fOut.write(buff, 0, read); // Write out the entire read buffer
							}
							total += read;

						} catch (IOException ex) {
						} finally { //return new Object[]{ex};
							if (!isRecording && wasRecording && fOut != null)
								try {
									fOut.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
						}
					}
				}else if(isReplaying) {
					SystemClock.sleep(75);
					try {
						fIn = new FileInputStream(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					try {
						if (total <= buff.length) { //if short record
							fIn.read(buff, 0, (int) total);
							read = (int) total;
							isReplaying = false;
						} else { //if long record
							if (counter < 10) {
								fIn.read(buff, counter * buff.length, buff.length);
								counter++;
							}else
							if (counter == blockSize-1) {
								fIn.read(buff, counter * buff.length, (int) (total - buff.length * counter));
								counter = 0;
								isReplaying = false;
							}
							read = blockSize;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						if (!isReplaying && wasRepaying && fIn != null)
							try {
								fIn.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
					}
					ByteBuffer.wrap(buff).asShortBuffer().get(buffer);
				}

				// Read in the data from the mic to the array
				for (int i = 0; i < blockSize && i < read; i++) {
					//Since the range of toTransform[i] is [-1,1), re-scale it
					//such that its base is 0 instead of -1
					toTransform[i] = (buffer[i] / 32768.0); // signed 16 bit
				}

				//Forwards Fourier Transforms toTransform[]
				transformer.ft(toTransform);
				//to publish results on the IU thread
				publishProgress(freqMagnitude(toTransform));
			}
		}
		return true;
	}

	public File init_writeFile(){
		final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/KIS/"); // Get the directory for the user's public pictures directory.
		if(!path.exists()) { // Make sure the path directory exists.
			path.mkdirs(); // Make it, if it doesn't exit
		}
		File file = new File(path, RecordTask.IO_FILENAME);
		return file;
	}

	double [] freqMagnitude(double [] toTransform){ //returns the magnitude and which muscle has been activated
		re = new double[blockSize];
		im = new double[blockSize];
		magnitude = new double[blockSize/2];
		frequency = new double[blockSize/2];

		for(int i = 0; i < (blockSize/2); i++){ // Calculate the Real and imaginary and Magnitude.
			re[i] = toTransform[i*2]; // real is stored in first part of array
			im[i] = toTransform[(i*2)+1]; // imaginary is stored in the sequential part

		}
		for(int i =0; i < (blockSize/2) ; i++){
			magnitude[i] = 0.7*(Math.sqrt((re[i] * re[i]) + (im[i]*im[i]))); // magnitude is calculated by the square root of (imaginary^2 + real^2)
			frequency[i] = i*(sampleRate)/(blockSize); // calculated the frequency
			if (magnitude[i] > THRESHOLD) { // checks how many signals are above threshold
				if (frequency[i] > (body.BICEP_FRQ-200) && frequency[i] < (body.BICEP_FRQ+200)) { // checks if the signal belongs to one of the targeted muscle frequency window then increment counter for each muscle frequency window
					Log.d("bicepfrq", Double.toString(frequency[i]));
					body.setBicep_counter(1);// increase counter by one
				}else if(frequency[i] > (body.TRICEPS_FRQ-200) && frequency[i] < (body.TRICEPS_FRQ+200)){
					Log.d("tricepsfrq", Double.toString(frequency[i]));
					body.setTriceps_counter(1); // increase counter by one
				}else if(frequency[i] > (body.FOREARM_FRQ-200) && frequency[i] < (body.FOREARM_FRQ+200)){
					Log.d("forearmfrq", Double.toString(frequency[i]));
					body.setForearm_counter(1); // increase counter by one
				}else if(frequency[i] > (body.DIST_SENS_FRQ-200) && frequency[i] < (body.DIST_SENS_FRQ+200)){
					Log.d("distanceSensFrq", Double.toString(frequency[i]));
					averageDist[j]=magnitude[i];
					if (j<9)
						j++;
					else
						j=0;
					Log.v("averageDistMagg: ", Double.toString(j));
					body.setDist_Sensor_counter(1); //increase counter by one
				}
			}
		}

		if (body.getBicep_counter()>muscles_counterThreshold) { // check if counter of bicep is greater than the muscle counter threshold
			body.isBicepActive = true;
			body.setBicep_counter(0); // decrement counter
		}
		else
			body.isBicepActive=false;

		if (body.getTriceps_counter()>muscles_counterThreshold) { // check if counter of triceps is greater than the muscle counter threshold
			body.isTricepsActive = true;
			body.setTriceps_counter(0); // decrement counter
		}
		else
			body.isTricepsActive=false;

		if (body.getForearm_counter()>muscles_counterThreshold) { // check if counter of forearm is greater than the muscle counter threshold
			body.isForearmActive = true;
			body.setForearm_counter(0); // decrement counter
		}
		else
			body.isForearmActive=false;


		if (body.getDist_Sensor_counter()==9) {
			body.isDist_sensorActive = true;

		}
		else{
			body.isDist_sensorActive=false;
			body.setDist_Sensor_counter(0);
			j=0;
		}
		return magnitude;
	}

	public double getMaxMagnitude(double[] magnitude) { // gets max magnitude
		double max_magnitude = magnitude[0];
		for(int i = 0 ; i < magnitude.length ; i++)
		{
			if(magnitude[i] > max_magnitude) {
				max_magnitude = magnitude[i];
			}
		}
		return max_magnitude;
	}

	public double getMinMagnitude(double[] magnitude) { // get min magnitude
		double min_magnitude = magnitude[0];
		for(int i = 0 ; i < magnitude.length ; i++)
		{
			if(magnitude[i] < min_magnitude) {
				min_magnitude = magnitude[i];
			}
		}
		return min_magnitude;
	}

	public double getMaxFrequency(double [] frequency) { // gets max frequency
		double max_frequency = frequency[0];
		for(int j = 0 ; j < frequency.length ; j++) {
			if(max_frequency < frequency[j]) {
				max_frequency = frequency[j];
			}
		}
		return max_frequency;
	}

	public double getMinFrequency(double[] frequency) { // gets minimum frequency
		double min_frequency = frequency[0];
		for(int k = 0 ; k < frequency.length ; k++) {
			if(min_frequency > frequency[k]) {
				min_frequency = frequency[k];
			}
		}
		return min_frequency;
	}

	//UPDATE SCREEN by invoked on the UI thread after a call to publishProgress()
	// from doInBackground() <-- "AsyncTast" extension
	@Override
	protected void onProgressUpdate(double[]... progress) {
		Log.v("onProgressUpdate:", Integer.toString(progress[0].length));
		canvasDisplaySpectrum.drawColor(Color.BLACK);
		int downy=1;
		int upy=1;
		float[] intervals = new float[]{20.0f, 20.0f}; // creates interval between dashes in dashed line
		float phase = 0;
		DashPathEffect dashPathEffect = new DashPathEffect(intervals, phase);
		paintMaxMagnitude.setPathEffect(dashPathEffect); // dashed line for max magnitude
		paintMinMagnitude.setPathEffect(dashPathEffect); // dashed line for min magnitude

		double freqGap= (((double)sampleRate)/((double)blockSize)); // frequency gap

		int line_position_Bicep = Math.round((float)((double)body.BICEP_FRQ/freqGap)); // line indicating bicep frequency
		int line_position_Triceps = Math.round((float)((double)body.TRICEPS_FRQ/freqGap)); // line indicating triceps frequency
		int line_position_Forearm = Math.round((float)((double)body.FOREARM_FRQ/freqGap)); // line indicating forearm frequency

		//TODO: if else inside for loop not the other way around
		if (width > 512) {
			for (int i = 0; i < progress[0].length; i++) {
				int x = 4 * i;
				downy = (int) (500 - (progress[0][i] * 10));
				upy = 500;
//				if (progress[0][i] < 0) Log.w("At x = " + i, Double.toString(progress[0][i]));
				canvasDisplaySpectrum.drawLine(x, downy, x, upy , paintSpectrumDisplay);
			}
			canvasDisplaySpectrum.drawLine(line_position_Bicep*4, origin, line_position_Bicep*4, upy, paintFreqLines_B); // draw bicep frequency line
			canvasDisplaySpectrum.drawLine(line_position_Triceps*4, origin, line_position_Triceps*4, upy, paintFreqLines_T); // draw triceps frequency line
			canvasDisplaySpectrum.drawLine(line_position_Forearm*4, origin, line_position_Forearm*4, upy, paintFreqLines_F); // draw forearm frequency line
			canvasDisplaySpectrum.drawLine(origin , lim_min , width , lim_min , paintMinMagnitude); // draw min magnitude dashed line
			canvasDisplaySpectrum.drawLine(origin , lim_max , width , lim_max , paintMaxMagnitude); // draw max magnitude dashed line
			imageViewDisplaySpectrum.invalidate();
		} else {
			for (int i = 0; i < progress[0].length; i++) {
				int x = i;
				downy = (int) (250 - (Math.abs(progress[0][i]) * 10));
				upy = 250;
				canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}

			canvasDisplaySpectrum.drawLine(line_position_Bicep, origin, line_position_Bicep, upy, paintFreqLines_B); // draw bicep frequency line
			canvasDisplaySpectrum.drawLine(line_position_Triceps, origin, line_position_Triceps, upy, paintFreqLines_T); // draw triceps frequency line
			canvasDisplaySpectrum.drawLine(line_position_Forearm, origin, line_position_Forearm, upy, paintFreqLines_F); // draw forearm frequency line
			canvasDisplaySpectrum.drawLine(origin , lim_min , width , lim_min , paintMinMagnitude); // draw min magnitude dashed line
			canvasDisplaySpectrum.drawLine(origin , lim_max , width , lim_max , paintMaxMagnitude); // draw max magnitude dashed line
			imageViewDisplaySpectrum.invalidate();
		}

		double av=0;
		for(int i=0; i<9;i++) {
			av = av + averageDist[i];
		}
		av = av/9.0;
		Log.v("average: ", Double.toString(av));

		while (counter <0) {
			if (body.isBicepActive && !BwasActive) {
				for(int i = 0 ; i < blockSize / 2 ; i++) {
					if(magnitude[i] >= high_magnitude) {
						drawBody.paintBicep.setColor(Color.RED);
					}
					else if (magnitude[i] < high_magnitude || magnitude[i] >= medium_magnitude) {
						drawBody.paintBicep.setColor(Color.rgb(255 , 165 , 0));
					}
//                    int max_magnitude = (int) getMaxMagnitude(magnitude);
//                    int red_value = (int) (Math.min((magnitude[i] / lim_max) , 1) * max_col);
//                    int green_value = (int) (Math.max((lim_max - magnitude[i])/ lim_max , 0)  * max_col);
//                    drawBody.paintBicep.setColor(Color.rgb(red_value , green_value , 0));
					BwasActive = true;
					body.isBicepActive = false;
				}
			}
			else if (!body.isBicepActive && BwasActive){
				drawBody.paintBicep.setColor(Color.GREEN);
				BwasActive=false;
			}

			if (body.isTricepsActive && !TwasActive){
				for(int i = 0 ; i < blockSize / 2 ; i++) {
					if(magnitude[i] >= high_magnitude) {
						drawBody.paintTriceps.setColor(Color.RED);
					}
					else if (magnitude[i] < high_magnitude || magnitude[i] >= medium_magnitude) {
						drawBody.paintTriceps.setColor(Color.rgb(255 , 165 , 0));
					}
//                    int max_magnitude = (int) getMaxMagnitude(magnitude);
//                    int red_value = (int) (Math.min((magnitude[i] / lim_max) , 1) * max_col);
//                    int green_value = (int) (Math.max((lim_max - magnitude[i])/ lim_max , 0)  * max_col);
//                    drawBody.paintTriceps.setColor(Color.rgb(red_value , green_value , 0));
					TwasActive=true;
					body.isTricepsActive=false;
				}
			}
			else if(!body.isTricepsActive && TwasActive){
				drawBody.paintTriceps.setColor(Color.GREEN);
				TwasActive=false;
			}

			if (body.isForearmActive && !FwasActive) {
				for(int i = 0 ; i < blockSize / 2 ; i++) {
					if(magnitude[i] >= high_magnitude) {
						drawBody.paintForearm.setColor(Color.RED);
					}
					else if (magnitude[i] < high_magnitude || magnitude[i] >= medium_magnitude) {
						drawBody.paintForearm.setColor(Color.rgb(255 , 165 , 0));
					}
//                    int max_magnitude = (int) getMaxMagnitude(magnitude);
//                    int red_value = (int) (Math.min((magnitude[i] / lim_max), 1) * max_col);
//                    int green_value = (int) (Math.max((lim_max - magnitude[i])/ lim_max , 0)  * max_col);
//                    drawBody.paintForearm.setColor(Color.rgb(red_value , green_value , 0));
					FwasActive = true;
					body.isForearmActive = false;
				}
			}
			else if (!body.isForearmActive && FwasActive){
				drawBody.paintForearm.setColor(Color.GREEN);
				FwasActive=false;
			}

			drawBody.invalidate();
			imageViewdrawBody.invalidate();
			counter=15;
			//counterB=false;
		}
		counter--;
	}

	public void updateDrawBody() {
		drawBody.setPaintBicep(body.isBicepActive);
		drawBody.setPaintForearm(body.isForearmActive);
		drawBody.setPaintTriceps(body.isTricepsActive);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		try {
			if (audioRecord != null) {
				audioRecord.stop();
			}
		} catch (IllegalStateException e) {
			Log.e("Stop failed", e.toString());
		}
	}

	public boolean isStarted() {
		return started;
	}

	public void setCancel() {
		CANCELLED_FLAG = true;
	}
}