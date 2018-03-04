package dev.S.ink.hotspot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Siraprapha on 2/26/2018.
 */

public class AboutFragment extends Fragment {

    ImageView img;
    TextView txtview;
    public static Fragment newInstance() {
        AboutFragment m = new AboutFragment();
        return m;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.about_us, container, false);
        img = rootview.findViewById(R.id.img);
        txtview = rootview.findViewById(R.id.txtview);
        return rootview;
    }

}
