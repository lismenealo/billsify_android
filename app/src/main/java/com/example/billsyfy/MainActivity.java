package com.example.billsyfy;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.billsyfy.entities.AppDatabase;
import com.example.billsyfy.entities.Bill;
import com.example.billsyfy.entities.BillDao;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import android.view.Menu;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * In this activity is handle the DB and the creation of the navigation controls
 * <p>Regarding db handling:
 * <ul>
 *  <li>Inset into the DB  a {@link Bill}
 *  <li>And retrieve a list of {@link Bill} to create the {@link PieChartView}
 *  <li>All the DB operations are performed async using {@link AsyncTask}
 * </ul>
 */
public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    public static AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_bills_brief, R.id.nav_bills_gallery, R.id.nav_capture_bill,
                R.id.nav_new_bill)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Instantiate database on start mainActivity
        this.db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "billsify").build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //Create asyncrounous job separate from main thread to perform insertion
    public static class BillsInsertAsync extends AsyncTask<Void, Void, Integer> {

        //Prevent leak
        private WeakReference<Activity> weakActivity;
        private Bill bill;

        public BillsInsertAsync(Activity activity, Bill bill) {
            weakActivity = new WeakReference<>(activity);
            this.bill = bill;
        }

        //Put the code to be executed on background thread
        @Override
        protected Integer doInBackground(Void... params) {
            BillDao billDao = db.billDao();
            billDao.insertAll(bill);
            return 0;
        }

        //After execution perform notifications
        @Override
        protected void onPostExecute(Integer agentsCount) {
            Activity activity = weakActivity.get();

            if (activity == null) {
                return;
            }

            if (agentsCount > 0) {
                //2: If it already exists then prompt user
                Toast.makeText(activity, "Bill already exists!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Bill created :)", Toast.LENGTH_LONG).show();
            }
        }
    }


    //Create job to be execute away from main thread
    public static class GetBillsByAsync extends AsyncTask<Void, Void, Integer> {

        //Prevent leak
        private WeakReference<PieChartView> pieChartView;
        private Date date;
        public List<Bill> bills;

        public GetBillsByAsync(PieChartView activity, Date date) {
            pieChartView = new WeakReference<>(activity);
            this.date = date;
        }

        //Get all bills
        @Override
        protected Integer doInBackground(Void... params) {
            BillDao billDao = db.billDao();
            this.bills = billDao.getAll();
            return 0;
        }

        //After execution pieChartView as intended from call
        @Override
        protected void onPostExecute(Integer result) {
            PieChartView activity = pieChartView.get();

            if (activity == null) {
                return;
            }

            if (result > 0) {

            } else {
                //From bills group by categories and retrieve sum of amount to build balance
                HashMap<String, Integer> categories = new HashMap<>();
                int totalAmount = 0;
                for (Bill bill: bills) {
                    totalAmount += bill.amount;
                    String cleanCategory = bill.category.trim().toUpperCase();
                    if(!categories.containsKey(cleanCategory))
                         categories.put(cleanCategory, bill.amount);
                    else {
                        int amount = categories.get(cleanCategory) + bill.amount;
                        categories.remove(cleanCategory);
                        categories.put(cleanCategory, amount);
                    }
                }

                //Set up PieChartData
                List<SliceValue> pieData = new ArrayList<>();
                for (String category: categories.keySet()) {

                    //Create random colors for categories
                    Random rand = new Random();
                    int r = rand.nextInt(255);
                    int g = rand.nextInt(255);
                    int b = rand.nextInt(255);
                    pieData.add(new SliceValue((categories.get(category)*100)/totalAmount, Color.rgb(r,g,b)).setLabel(category + ": " + categories.get(category)));
                }

                PieChartData pieChartData = new PieChartData(pieData);

                //Customize pieChart
                pieChartData.setHasLabels(true);
                pieChartData.setHasLabels(true).setValueLabelTextSize(14);
                pieChartData.setHasCenterCircle(true).setCenterText1("Total: " + totalAmount).setCenterText1FontSize(40).setCenterText1Color(Color.parseColor("#0097A7"));

                activity.setPieChartData(pieChartData);
            }
        }
    }
}