package com.example.billsyfy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import lecho.lib.hellocharts.view.PieChartView;

import com.example.billsyfy.MainActivity;
import com.example.billsyfy.R;
import java.util.Date;

public class HomeFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        PieChartView pieChartView = root.findViewById(R.id.chart);

        MainActivity.GetBillsByAsync getBillsByAsync = new MainActivity.GetBillsByAsync(pieChartView, new Date());
        getBillsByAsync.execute();


        return root;
    }
}