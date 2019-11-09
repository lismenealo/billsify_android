package com.example.billsyfy.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.billsyfy.MainActivity;
import com.example.billsyfy.R;
import com.example.billsyfy.entities.Bill;
import com.example.billsyfy.ui.CaptureBillFragment;

import java.util.Date;

public class NewBillFragment extends Fragment {

    View root;
    private EditText et1,et2;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // check Fields For Empty Values
            checkFieldsForEmptyValues();
        }
    };

    void checkFieldsForEmptyValues(){
        Button b = (Button) root.findViewById(R.id.submit);

        String s1 = et1.getText().toString();
        String s2 = et2.getText().toString();

        if(s1.equals("")|| s2.equals("")){
            b.setEnabled(false);
        } else {
            b.setEnabled(true);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.fragment_new_bill, container, false);

        final ImageView imageView = root.findViewById(R.id.bill_scanned);
        Bitmap myBitmap = BitmapFactory.decodeFile(CaptureBillFragment.filePath);
        imageView.setImageBitmap(myBitmap);

        Button re_scan = root.findViewById(R.id.cancel);
        re_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.nav_capture_bill);
            }
        });

        Button submit = root.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bill bill = new Bill();
                bill.amount =  Integer.parseInt((((EditText)root.findViewById(R.id.amount)).getText().toString()));
                bill.category = (((EditText)root.findViewById(R.id.category)).getText().toString());
                bill.date = new Date();
                bill.description = (((EditText)root.findViewById(R.id.description)).getText().toString());
                bill.imageFilePath = CaptureBillFragment.filePath;

                new MainActivity.BillsInsertAsync(getActivity(), bill).execute();
                Navigation.findNavController(view).navigate(R.id.nav_bills_brief);
            }
        });

        et1 = (EditText) root.findViewById(R.id.amount);
        et2 = (EditText) root.findViewById(R.id.category);

        // set listeners
        et1.addTextChangedListener(mTextWatcher);
        et2.addTextChangedListener(mTextWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();
        return root;
    }
}