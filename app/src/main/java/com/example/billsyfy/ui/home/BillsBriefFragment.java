package com.example.billsyfy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import lecho.lib.hellocharts.view.PieChartView;

import com.example.billsyfy.MainActivity;
import com.example.billsyfy.R;
import java.util.Date;

public class BillsBriefFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_bills_brief, container, false);

        PieChartView pieChartView = root.findViewById(R.id.chart);

        MainActivity.GetBillsByAsync getBillsByAsync = new MainActivity.GetBillsByAsync(pieChartView, new Date());
        getBillsByAsync.execute();

        Button captureBill = root.findViewById(R.id.capture_bill);
        captureBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_capture_bill);
            }
        });

        Button slideShow = root.findViewById(R.id.shoow_all);
        slideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_bills_gallery);
            }
        });

        return root;
    }
}