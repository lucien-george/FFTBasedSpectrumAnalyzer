package ca.uol.aig.fftpack;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


//Async is used to avoid dealing with threads concurrences and therefore handlers.
public class RecordTask extends AsyncTask<Void, double[], Boolean> {


	private final int width;
	private final ImageView imageViewDisplaySpectrum;
	private final ImageView imageViewBicep;
	private final ImageView imageViewTriceps;
	private final ImageView imageViewForearm;
	private final Canvas canvasDisplaySpectrum;
	private final Paint paintSpectrumDisplay;
	private RelativeLayout bparts;
	private RealDoubleFFT transformer;
	private int blockSize = 256;
	private boolean started = false;
	private boolean CANCELLED_FLAG = false;
	private AudioRecord audioRecord;
	private int sampleRate =42000;
	private final body body;


	

	private int muscles_counterThreshold=1;


	final int THRESHOLD = 10;
	int counter = 0;
	boolean BwasActive = false;
	boolean TwasActive = false;
	boolean FwasActive = false;

	// changed from "CHANNEL_CONFIGURATION_MONO" to "CHANNEL_IN_MONO", newest version
	int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;

	//defines the audio encoding format used throughout the recording task
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	//constructor initializing RecordTask
	public RecordTask(Canvas canvasDisplaySpectrum, Paint paintSpectrumDisplay, ImageView imageViewDisplaySectrum, RelativeLayout bparts,
					  ImageView imageViewBicep, ImageView imageViewTriceps, ImageView imageViewForearm, int width, body body) {
		this.width = width;
		blockSize = width / 2;
		this.imageViewDisplaySpectrum = imageViewDisplaySectrum;
		this.bparts = bparts;
		this.imageViewBicep= imageViewBicep;
		this.imageViewTriceps= imageViewTriceps;
		this.imageViewForearm=imageViewForearm;
		this.canvasDisplaySpectrum = canvasDisplaySpectrum;
		this.paintSpectrumDisplay = paintSpectrumDisplay;
		this.body=body;
	}

	//invoked on the UI thread before the task is executed <-- "AsyncTast" extension
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		transformer = new RealDoubleFFT(blockSize);
	}

	//involves background operation that can take time <-- "AsyncTast" extension
	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d("Recording doBackground", params.toString());

		double[] re = new double[blockSize];
		double[] im = new double[blockSize];
		double[] magnitude = new double[blockSize/2];
		double [] frequency = new double[blockSize];
		int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfiguration, audioEncoding, bufferSize);
		int bufferReadResult;
		short[] buffer = new short[blockSize];
		double[] toTransform = new double[blockSize];
		try {
			audioRecord.startRecording();
			started = true;
		} catch (IllegalStateException e) {
			Log.e("Recording failed", e.toString());
		}
		while (started) {
			if (isCancelled() || (CANCELLED_FLAG)) {
				started = false;
				// publishProgress(cancelledResult);
				Log.d("doInBackground", "Cancelling the RecordTask");
				break;
			} else {

				/* Reads the data from the microphone. it takes in data
                 * to the size of the window "blockSize". The data is then
                 * given in to audioRecord. The int returned is the number
                 * of bytes that were read*/
				bufferReadResult = audioRecord.read(buffer, 0, blockSize);

				// Read in the data from the mic to the array
				for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
					//Since the range of toTransform[i] is [-1,1), re-scale it
					//such that its base is 0 instead of -1
					toTransform[i] = /*1 +*/ (buffer[i] / 32768.0); // signed 16 bit
//					if (toTransform[i] < 0) Log.w("NEGATIVE", Double.toString(buffer[i]));
				}

				//Forwards Fourier Transforms toTransform[]
				transformer.ft(toTransform);
				//to publish results on the IU thread
			}

			publishProgress(freqMagnitude(toTransform));

		}
		return true;
	}


	//returns the magnitude and which muscle has been activated

	double [] freqMagnitude(double [] toTransform){

		double[] re = new double[blockSize];
		double[] im = new double[blockSize];
		double[] magnitude = new double[blockSize/2];
		double frequency[] = new double[blockSize/2];

		// Calculate the Real and imaginary and Magnitude.
		for(int i = 0; i < (blockSize/2); i++){
			// real is stored in first part of array
			re[i] = toTransform[i*2];
			// imaginary is stored in the sequential part
			im[i] = toTransform[(i*2)+1];

		}


		for(int i =0; i < (blockSize/2) ; i++){

			// magnitude is calculated by the square root of (imaginary^2 + real^2)
			magnitude[i] = Math.sqrt((re[i] * re[i]) + (im[i]*im[i]));
			// calculated the frequency
			frequency[i] = i*(sampleRate)/(blockSize);

			//Log.d("magnitude", Double.toString(magnitude[i]));
			//Log.d("frequency", Double.toString(frequency[i]));

			//checks how many signals are above threshold
			if (magnitude[i]>THRESHOLD) {
				//checks if the signal belongs to one of the targeted muscle frequency window
				//then increment counter for each muscle frequency window
				if (frequency[i] > (body.BICEP_FRQ-200) && frequency[i] < (body.BICEP_FRQ+200)) {

					Log.d("bicepfrq", Double.toString(frequency[i]));
					//increase counter by one
					body.setBicep_counter(1);
				}else if(frequency[i] > (body.TRICEPS_FRQ-200) && frequency[i] < (body.TRICEPS_FRQ+200)){

					Log.d("tricepsfrq", Double.toString(frequency[i]));
					//increase counter by one
					body.setTriceps_counter(1);
				}else if(frequency[i] > (body.FOREARM_FRQ-200) && frequency[i] < (body.FOREARM_FRQ+200)){

					Log.d("forearmfrq", Double.toString(frequency[i]));
					//increase counter by one
					body.setForearm_counter(1);
				}
			}

		}

		if (body.getBicep_counter()>muscles_counterThreshold) {
			body.isBicepActive = true;
			body.setBicep_counter(0);
		}
		else
			body.isBicepActive=false;

		if (body.getTriceps_counter()>muscles_counterThreshold) {
			body.isTricepsActive = true;
			body.setTriceps_counter(0);
		}
		else
			body.isTricepsActive=false;

		if (body.getForearm_counter()>muscles_counterThreshold) {
			body.isForearmActive = true;
			body.setForearm_counter(0);
		}
		else
			body.isForearmActive=false;

		Log.d("doInBackground", "we made it");

		return magnitude;
	}

	//UPDATE SCREEN by invoked on the UI thread after a call to publishProgress()
	// from doInBackground() <-- "AsyncTast" extension
	@Override
	protected void onProgressUpdate(double[]... progress) {
		Log.v("onProgressUpdate:", Integer.toString(progress[0].length));
		canvasDisplaySpectrum.drawColor(Color.GRAY);



		//if screen large is enough double the size
		if (width > 512) {
			for (int i = 0; i < progress[0].length; i++) {
				int x = 4 * i;
				int downy = (int) (200 - (progress[0][i] * 10));
				int upy = 200;
//				if (progress[0][i] < 0) Log.w("At x = " + i, Double.toString(progress[0][i]));
                canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}
			imageViewDisplaySpectrum.invalidate();
		} else {
			for (int i = 0; i < progress[0].length; i++) {
				int x = i;
				int downy = (int) (150 - (Math.abs(progress[0][i]) * 10));
				int upy = 150;
//				if (progress[0][i] < 0) Log.w("At x = " + i, Double.toString(progress[0][i]));
				canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}



			imageViewDisplaySpectrum.invalidate();
		}


		while (counter <0) {


			if (body.isBicepActive && !BwasActive) {
				bparts.addView(imageViewBicep);
				BwasActive=true;
				body.isBicepActive=false;
			}
			else if (!body.isBicepActive && BwasActive){
				bparts.removeView(imageViewBicep);
				BwasActive=false;
			}

			if (body.isTricepsActive && !TwasActive){
				bparts.addView(imageViewTriceps);
				TwasActive=true;
				body.isTricepsActive=false;
			}
			else if(!body.isTricepsActive && TwasActive){
				bparts.removeView(imageViewTriceps);
				TwasActive=false;
			}

			if (body.isForearmActive && !FwasActive) {
				bparts.addView(imageViewForearm);
				FwasActive=true;
				body.isForearmActive=false;
			}
			else if (!body.isForearmActive && FwasActive){
				bparts.removeView(imageViewForearm);
				FwasActive=false;
			}

			imageViewBicep.invalidate();
			imageViewTriceps.invalidate();
			imageViewForearm.invalidate();

			counter=15;
			//counterB=false;
		}
		counter--;


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

		//canvasDisplaySpectrum.drawColor(Color.BLACK);
		//imageViewDisplaySectrum.invalidate();
	}

	public boolean isStarted() {
		return started;
	}

	public void setCancel() {
		CANCELLED_FLAG = true;
	}
}
