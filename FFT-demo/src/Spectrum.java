import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.somitsolutions.android.spectrumanalyzer.R;

import ca.uol.aig.fftpack.view.HorizontalScaleImageView;

public class Spectrum extends Fragment{

    HorizontalScaleImageView imageViewScale;
    Bitmap bitmapDisplaySpectrum;
    Canvas canvasDisplaySpectrum;
    Paint paintSpectrumDisplay;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.spectrum_display,container,false);

        return view;
    }
}
