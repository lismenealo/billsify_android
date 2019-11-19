package com.example.billsyfy.ui.BillsGallery;

import android.app.Activity;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.example.billsyfy.MainActivity;
import com.example.billsyfy.R;
import com.example.billsyfy.entities.Bill;
import com.example.billsyfy.entities.BillDao;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

//Class and view that handle the a single category album
public class AlbumActivity extends AppCompatActivity {

    GridView galleryGridView;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //Set title view from intent information
        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        setTitle(album_name);

        galleryGridView = (GridView) findViewById(R.id.galleryGridView);


        //Get display details to tuned he visualization in gridView
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Helper.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        //LoadAlbum information from bill table
        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }


    //Again create asyn job to load images on background
    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageList.clear();
        }

        //Retrieve bills information and build category collection
        protected String doInBackground(String... args) {
            String xml = "";

            BillDao billDao = MainActivity.db.billDao();
            List<Bill> bills = billDao.findByCategory(album_name);

            for (Bill bill: bills) {
                imageList.add(Helper.mappingInbox(album_name, bill.imageFilePath, bill.date.toString(), bill.description, "€" + bill.amount));
            }
            Collections.sort(imageList, new MapComparator(Helper.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            //Create and CategoryAdapter
            SingleAlbumAdapter adapter = new SingleAlbumAdapter(AlbumActivity.this, imageList);
            galleryGridView.setAdapter(adapter);
            //Subscribe for click event and navigate to zoom preview of bill passing all bills details with it
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(AlbumActivity.this, GalleryPreviewActivity.class);
                    intent.putExtra("path", imageList.get(+position).get(Helper.KEY_PATH));
                    intent.putExtra("category", imageList.get(+position).get(Helper.KEY_CATEGORY));
                    intent.putExtra("description", imageList.get(+position).get(Helper.KEY_DESC));
                    intent.putExtra("amount", imageList.get(+position).get(Helper.KEY_AMOUNT));
                    intent.putExtra("timestamp", imageList.get(+position).get(Helper.KEY_TIMESTAMP));
                    startActivity(intent);
                }
            });
        }
    }
}

//Album Adapter to handle collection in view
class SingleAlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public SingleAlbumAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
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

    //Set data on album of bill preview
    public View getView(int position, View convertView, ViewGroup parent) {
        SingleAlbumViewHolder holder = null;
        if (convertView == null) {

            //Retrieve view object to set fields values
            holder = new SingleAlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
            holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

            convertView.setTag(holder);
        } else {
            holder = (SingleAlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            holder.gallery_title.setText(song.get(Helper.KEY_DESC));
            holder.gallery_count.setText(song.get(Helper.KEY_AMOUNT));

            //load sample image for bill
            Glide.with(activity)
                    .load(new File(song.get(Helper.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);


        } catch (Exception e) {}
        return convertView;
    }
}

//Helper object to store bill detials on view album
class SingleAlbumViewHolder {
    ImageView galleryImage;
    TextView gallery_count, gallery_title;
}
