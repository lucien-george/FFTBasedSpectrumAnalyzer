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
	private final ImageView imageViewDisplaySpectrum;
	private final ImageView imageViewBicep;
	private final ImageView imageViewTriceps;
	private final ImageView imageViewForearm;
	private final Canvas canvasDisplaySpectrum;
	private final Paint paintSpectrumDisplay;
	private final Paint paintFreqLines_B;
	private final Paint paintFreqLines_T;
	private final Paint paintFreqLines_F;
	private final Paint paintMinMagnitude;
	private final Paint paintMaxMagnitude;
	private RelativeLayout bparts;
	private RealDoubleFFT transformer;
	private int blockSize = 256;
	private boolean started = false;
	private boolean CANCELLED_FLAG = false;
	private AudioRecord audioRecord;
	private int sampleRate =42000;
	private final body body;
	private drawBody drawBody;
	private ImageView imageViewdrawBody;
    private int max_col = 255;
    private int lim_min = 1;
    private int lim_max = 290;
    private int high_magnitude = 150;
    private int medium_magnitude = 75;

    public final static String IO_FILENAME=
			"KISDataREC";
	public static FileOutputStream fOut;
	public static FileInputStream fIn;
	public static File file;
	public static InputStreamReader myInReader;
	public static OutputStreamWriter myOutWriter;
	public static boolean isRecording=false;
	public static boolean wasRecording=false;
	public static boolean wasRepaying= false;
	public static boolean isReplaying=false;
	BufferedReader reader = null;
	boolean wasRec = false;
	int length=0;


	

	private int muscles_counterThreshold=1;


	final int THRESHOLD = 10;
	int counter = 0;
	boolean BwasActive = false;
	boolean TwasActive = false;
	boolean FwasActive = false;
	float forearmDegreeIncrease = 0;
	float new_forearmDegreeIncrease=0;
	double maxMag=20.0;
	double[] averageDist = {0,0,0,0,0,0,0,0,0,0};
	int j=0;

	// changed from "CHANNEL_CONFIGURATION_MONO" to "CHANNEL_IN_MONO", newest version
	int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;

	//defines the audio encoding format used throughout the recording task
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private double[] re; //real part
    private double[] im; // imaginary part
    private double[] magnitude; // magnitude
    private double[] frequency; // frequency

	//constructor initializing RecordTask
	public RecordTask(Canvas canvasDisplaySpectrum, Paint paintSpectrumDisplay, ImageView imageViewDisplaySectrum, RelativeLayout bparts,
					  ImageView imageViewBicep, ImageView imageViewTriceps, ImageView imageViewForearm,
					  int width, body body, drawBody drawBody, ImageView imageViewdrawBody,boolean isRecording,boolean isReplaying) {
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

		paintFreqLines_B = new Paint();
		paintFreqLines_B.setColor(body.Bicep_textColor);
		paintFreqLines_B.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFreqLines_B.setStrokeWidth(3);

		paintFreqLines_T = new Paint();
		paintFreqLines_T.setColor(body.Triceps_textColor);
		paintFreqLines_T.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFreqLines_T.setStrokeWidth(3);

		paintFreqLines_F = new Paint();
		paintFreqLines_F.setColor(body.Forearm_textColor);
		paintFreqLines_F.setStyle(Paint.Style.FILL_AND_STROKE);
		paintFreqLines_F.setStrokeWidth(3);

		paintMinMagnitude = new Paint();
		paintMinMagnitude.setColor(Color.GREEN);
		paintMinMagnitude.setStyle(Paint.Style.STROKE);
		paintMinMagnitude.setStrokeWidth(3);

        paintMaxMagnitude = new Paint();
        paintMaxMagnitude.setColor(Color.RED);
        paintMaxMagnitude.setStyle(Paint.Style.STROKE);
        paintMaxMagnitude.setStrokeWidth(3);


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


		file= init_writeFile();

		try {
			if(!file.exists())
				file.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

				fOut = new FileOutputStream(file);
				myOutWriter = new OutputStreamWriter(fOut);

				fIn = new FileInputStream(file);
				myInReader = new InputStreamReader(fIn);

		} catch (IOException e) {
			e.printStackTrace();
		}



		int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfiguration, audioEncoding, bufferSize);
		int read=0;
		long total=0;
		boolean run=true;
		int counter=0;
		short[] buffer = new short[blockSize];
		byte[] buff = new byte[2*blockSize];
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


					if(!isReplaying) {


					/* Reads the data from the microphone. it takes in data
					 * to the size of the window "blockSize". The data is then
					 * given in to audioRecord. The int returned is the number
					 * of bytes that were read*/



						read = audioRecord.read(buffer, 0, blockSize);

						Log.v("Read ", Integer.toString(read));

						if (isRecording) {


							ByteBuffer.wrap(buff).asShortBuffer().put(buffer);

							wasRecording = true;
							try {

								if (total + read > 4294967295L) {
									// Write as many bytes as we can before hitting the max size
									for (int i = 0; i < read && total <= 4294967295L; i++, total++) {
										fOut.write(buff[i]);
									}
									isRecording = false;
									Log.v("File ", "hit file limit");
								} else {
									// Write out the entire read buffer
									fOut.write(buff, 0, read);
								}
								total += read;

							} catch (IOException ex) {
								//return new Object[]{ex};
							} finally {
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
							//if short record
							if (total <= buff.length) {

								fIn.read(buff, 0, (int) total);

								read = (int) total;
								isReplaying = false;
								//SystemClock.sleep(2000);
								//if long record
							} else {
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
						toTransform[i] = /*1 +*/ (buffer[i] / 32768.0); // signed 16 bit
	//					if (toTransform[i] < 0) Log.w("NEGATIVE", Double.toString(buffer[i]));
					}

					//Forwards Fourier Transforms toTransform[]
					transformer.ft(toTransform);
					//to publish results on the IU thread
					publishProgress(freqMagnitude(toTransform));
					System.out.println("Max Magnitude = " + getMaxMagnitude(freqMagnitude(toTransform)));
                    System.out.println("Min Magnitude = " + getMinMagnitude(freqMagnitude(toTransform)));
				}

					//writeToFile(toTransform);



		}
		return true;
	}


	public File init_writeFile(){

		// Get the directory for the user's public pictures directory.
		final File path =
				Environment.getExternalStoragePublicDirectory
						(
								Environment.DIRECTORY_DOCUMENTS + "/KIS/"
						);

		// Make sure the path directory exists.
		if(!path.exists())
		{
			// Make it, if it doesn't exit
			path.mkdirs();
		}

		File file = new File(path, RecordTask.IO_FILENAME);
		return file;

	}
	//returns the magnitude and which muscle has been activated

	double [] freqMagnitude(double [] toTransform){

		re = new double[blockSize];
		im = new double[blockSize];
		magnitude = new double[blockSize/2];
		frequency = new double[blockSize/2];


		// Calculate the Real and imaginary and Magnitude.
		for(int i = 0; i < (blockSize/2); i++){
			// real is stored in first part of array
			re[i] = toTransform[i*2];
			// imaginary is stored in the sequential part
			im[i] = toTransform[(i*2)+1];

		}

		for(int i =0; i < (blockSize/2) ; i++){

			// magnitude is calculated by the square root of (imaginary^2 + real^2)
			magnitude[i] = 0.7*(Math.sqrt((re[i] * re[i]) + (im[i]*im[i])));
			// calculated the frequency
			frequency[i] = i*(sampleRate)/(blockSize);


			//Log.d("magnitude", Double.toString(magnitude[i]));
			//Log.d("frequency", Double.toString(frequency[i]));

			//checks how many signals are above threshold
			if (magnitude[i] > THRESHOLD) {
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
				}else if(frequency[i] > (body.DIST_SENS_FRQ-200) && frequency[i] < (body.DIST_SENS_FRQ+200)){

					Log.d("distanceSensFrq", Double.toString(frequency[i]));

					//implement a counting average of last 10 values
					//if(maxMag<magnitude[i])
					//	averageDist[j] = maxMag;
					//else
						averageDist[j]=magnitude[i];

					if (j<9)
						j++;
					else
						j=0;

					Log.v("averageDistMagg: ", Double.toString(j));
					//increase counter by one
					body.setDist_Sensor_counter(1);
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


        if (body.getDist_Sensor_counter()==9) {
            body.isDist_sensorActive = true;

        }
        else{
            body.isDist_sensorActive=false;
            body.setDist_Sensor_counter(0);
            j=0;
            //maxMag=10;
        }
		return magnitude;
	}

	public double getMaxMagnitude(double[] magnitude) {
	    double max_magnitude = magnitude[0];
	    for(int i = 0 ; i < magnitude.length ; i++)
        {
            if(magnitude[i] > max_magnitude) {
                max_magnitude = magnitude[i];
            }
        }
	    return max_magnitude;
    }

    public double getMinMagnitude(double[] magnitude) {
        double min_magnitude = magnitude[0];
        for(int i = 0 ; i < magnitude.length ; i++)
        {
            if(magnitude[i] < min_magnitude) {
                min_magnitude = magnitude[i];
            }
        }
        return min_magnitude;
    }

    //find the maximum frequency received
    public double getMaxFrequency(double [] frequency) {
        double max_frequency = frequency[0];
        for(int j = 0 ; j < frequency.length ; j++) {
            if(max_frequency < frequency[j]) {
                max_frequency = frequency[j];
            }
        }
        return max_frequency;
    }

    //find the minimum frequency received
    public double getMinFrequency(double[] frequency) {
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
        float[] intervals = new float[]{20.0f, 20.0f};
        float phase = 0;
        DashPathEffect dashPathEffect = new DashPathEffect(intervals, phase);
        paintMaxMagnitude.setPathEffect(dashPathEffect);
        paintMinMagnitude.setPathEffect(dashPathEffect);

		double freqGap= (((double)sampleRate)/((double)blockSize));

		int line_position_Bicep = Math.round((float)((double)body.BICEP_FRQ/freqGap));
		int line_position_Triceps = Math.round((float)((double)body.TRICEPS_FRQ/freqGap));
		int line_position_Forearm = Math.round((float)((double)body.FOREARM_FRQ/freqGap));

		//if screen large is enough double the size
		if (width > 512) {
			for (int i = 0; i < progress[0].length; i++) {
				int x = 4 * i;
				downy = (int) (300 - (progress[0][i] * 10));
				upy = 300;
//				if (progress[0][i] < 0) Log.w("At x = " + i, Double.toString(progress[0][i]));
                canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}
			canvasDisplaySpectrum.drawLine(line_position_Bicep*4, 0, line_position_Bicep*4, upy, paintFreqLines_B);
			canvasDisplaySpectrum.drawLine(line_position_Triceps*4, 0, line_position_Triceps*4, upy, paintFreqLines_T);
			canvasDisplaySpectrum.drawLine(line_position_Forearm*4, 0, line_position_Forearm*4, upy, paintFreqLines_F);
			canvasDisplaySpectrum.drawLine(0 , lim_min , width , lim_min , paintMinMagnitude);
			canvasDisplaySpectrum.drawLine(0 , lim_max , width , lim_max , paintMaxMagnitude);
			imageViewDisplaySpectrum.invalidate();
		} else {
			for (int i = 0; i < progress[0].length; i++) {
				int x = i;
				downy = (int) (150 - (Math.abs(progress[0][i]) * 10));
				upy = 150;
//				if (progress[0][i] < 0) Log.w("At x = " + i, Double.toString(progress[0][i]));
				canvasDisplaySpectrum.drawLine(x, downy, x, upy, paintSpectrumDisplay);
			}

			canvasDisplaySpectrum.drawLine(line_position_Bicep, 0, line_position_Bicep, upy, paintFreqLines_B);
			canvasDisplaySpectrum.drawLine(line_position_Triceps, 0, line_position_Triceps, upy, paintFreqLines_T);
			canvasDisplaySpectrum.drawLine(line_position_Forearm, 0, line_position_Forearm, upy, paintFreqLines_F);
//			canvasDisplaySpectrum.drawLine(0 , 1 , width , 1 , paintMinMagnitude);
//			canvasDisplaySpectrum.drawLine(0 , 290 , width , 290 , paintMaxMagnitude);
			imageViewDisplaySpectrum.invalidate();
		}

		double av=0;
		for(int i=0; i<9;i++) {
			av = av + averageDist[i];
			//if(maxMag<averageDist[i])
				//maxMag= averageDist[i]+3;
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
		// Test Color Transition
//		drawBody.setPaintBicep();
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
