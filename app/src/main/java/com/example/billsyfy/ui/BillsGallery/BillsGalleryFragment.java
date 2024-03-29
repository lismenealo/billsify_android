package com.example.billsyfy.ui.BillsGallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.billsyfy.MainActivity;
import com.example.billsyfy.R;
import com.example.billsyfy.entities.Bill;
import com.example.billsyfy.entities.BillDao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//Class and view that handle the a all category albums
public class BillsGalleryFragment extends Fragment {

    static final int REQUEST_PERMISSION_KEY = 1;
    LoadAlbum loadAlbumTask;
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_bills_gallery, container, false);

        galleryGridView = root.findViewById(R.id.galleryGridView);

        //Get display details to tuned he visualization in gridView
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Helper.convertDpToPixel(dp, getContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        //Request persmission if no granted yet
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Helper.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
        }
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Set request permissions and LoadAlbum if granted
        switch (requestCode)
        {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    loadAlbumTask = new LoadAlbum();
                    loadAlbumTask.execute();
                } else
                {
                    Toast.makeText(getActivity(), "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();

        //Set request permissions and LoadAlbum
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Helper.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, REQUEST_PERMISSION_KEY);
        }else{
            loadAlbumTask = new LoadAlbum();
            loadAlbumTask.execute();
        }

    }


    //Again create asyn job to load images on background
    class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        //Retrieve bills information and build album collection
        protected String doInBackground(String... args) {
            String xml = "";
            BillDao billDao = MainActivity.db.billDao();
            List<Bill> bills = billDao.getAll();

            HashMap<String, Integer> categories = new HashMap<>();
            HashMap<String, String> categories_imgpath = new HashMap<>();
            HashMap<String, Date> categories_timeStamp = new HashMap<>();
            for (Bill bill: bills) {
                String cleanCategory = bill.category.trim().toUpperCase();
                if(!categories.containsKey(cleanCategory)) {
                    categories.put(cleanCategory, bill.amount);
                    categories_imgpath.put(cleanCategory, bill.imageFilePath);
                    categories_timeStamp.put(cleanCategory, bill.date);
                } else {
                    int amount = categories.get(cleanCategory) + bill.amount;
                    categories.remove(cleanCategory);
                    categories.put(cleanCategory, amount);
                }
            }

            for (String category: categories.keySet()) {
                albumList.add(Helper.mappingInbox(category,
                        categories_imgpath.get(category),
                        categories_timeStamp.get(category).toString(),
                        categories_timeStamp.get(category).toString(),
                        "€" + categories.get(category).toString()));
            }

            Collections.sort(albumList, new MapComparator(Helper.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            //Create a set album adapter to handle collection
            AlbumAdapter adapter = new AlbumAdapter(getActivity(), albumList);
            galleryGridView.setAdapter(adapter);
            //subscribe to click event to retrieve image on call
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(getActivity(), AlbumActivity.class);
                    intent.putExtra("name", albumList.get(+position).get(Helper.KEY_CATEGORY));
                    startActivity(intent);
                }
            });
        }
    }
}

//Album Adapter to handle collection in view
class AlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public AlbumAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
        activity = a;
        data = d;
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    //Set data on album of bill category preview
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;
        //Retrieve view object to set fields values
        if (convertView == null) {
            holder = new AlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
            holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

            convertView.setTag(holder);
        } else {
            holder = (AlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);
        holder.gallery_count.setId(position);
        holder.gallery_title.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            holder.gallery_title.setText(song.get(Helper.KEY_CATEGORY));
            holder.gallery_count.setText(song.get(Helper.KEY_AMOUNT));

            //load sample image for category
            Glide.with(activity)
                    .load(new File(song.get(Helper.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);


        } catch (Exception e) {}
        return convertView;
    }
}

//Helper object to store album category data
class AlbumViewHolder {
    ImageView galleryImage;
    TextView gallery_count, gallery_title;
}