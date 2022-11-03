package chat.atc.tges.tgeschat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import chat.atc.tges.tgeschat.R;

public class FragmentInHouse extends androidx.fragment.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ly_dialog_visor_in_house, container, false);
        return v;
    }
}
