package com.example.billsyfy.ui.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.example.billsyfy.MainActivity;
import com.example.billsyfy.R;
import com.example.billsyfy.entities.AppDatabase;
import com.example.billsyfy.entities.Bill;
import com.example.billsyfy.entities.BillDao;
import com.example.billsyfy.ui.gallery.GalleryFragment;

import java.util.Date;
import java.util.List;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        final ImageView imageView = root.findViewById(R.id.bill_scanned);
        Bitmap myBitmap = BitmapFactory.decodeFile(GalleryFragment.filePath);
        imageView.setImageBitmap(myBitmap);


        Button re_scan = (Button) root.findViewById(R.id.re_scan);
        re_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_gallery);
            }
        });

        Button submit = (Button) root.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bill bill = new Bill();
                bill.amount = 1000;
                bill.category = "GENERAK";
                bill.date = new Date();
                bill.description = "some nice description";

                new MainActivity.BillsInsertAsync(getActivity(), bill.category, bill.description, bill.amount, bill.date).execute();
                Navigation.findNavController(view).navigate(R.id.nav_home);
            }
        });
        return root;
    }
}