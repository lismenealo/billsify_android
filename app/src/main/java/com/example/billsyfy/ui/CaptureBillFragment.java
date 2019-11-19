package com.example.billsyfy.ui;

import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.billsyfy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class CaptureBillFragment extends Fragment {

    private static final String TAG = "CaptureBillFragment";
    private CameraCaptureController cameraController;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_capture_bill, container, false);

        //Create object camera controller
        cameraController = new CameraCaptureController(getActivity(), getContext());

        //Set texture in cameraController to handel the preview
        cameraController.textureView = root.findViewById(R.id.textureView);
        assert cameraController.textureView != null;
        cameraController.textureView.setSurfaceTextureListener(cameraController.textureListener);

        //Set listener for on click action on takePicture button
        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call take picture in camera controller
                cameraController.takePicture();
                Snackbar.make(view, "Bill Scanned", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                    Navigation.findNavController(view).navigate(R.id.nav_new_bill);
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Verify permission properly set
        if (requestCode == cameraController.REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(getContext(), "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        //Start Camera when returning to fragment
        cameraController.startBackgroundThread();

        //Set preview on textureView
        if (cameraController.textureView.isAvailable()) {
            cameraController.openCamera();
        } else {
            cameraController.textureView.setSurfaceTextureListener(cameraController.textureListener);
        }
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");

        //Close camera on leaving the fragment, release resource
        cameraController.closeCamera();
        cameraController.stopBackgroundThread();
        super.onPause();
    }
}