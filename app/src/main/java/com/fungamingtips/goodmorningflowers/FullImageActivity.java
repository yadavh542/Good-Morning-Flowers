package com.fungamingtips.goodmorningflowers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.File;
import java.io.IOException;
import java.util.List;

public class FullImageActivity<adRequest> extends AppCompatActivity {

    private ImageView fullImage;
    private String url;
    private AdView adView;
    private final String TAG = "GGLADS";
    private InterstitialAd mInterstitialAd;
   // private Object BitmapDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });

        adView = findViewById(R.id.adView);

        bannerAds();
        interstitialAds();


        fullImage = findViewById(R.id.fullImage);
        Button apply = findViewById(R.id.apply);
        Button downloadImg = findViewById(R.id.download_img);

        url = getIntent().getStringExtra("image");


        Glide.with(this).load(url).into(fullImage);

        PRDownloader.initialize(getApplicationContext());

        downloadImg.setOnClickListener(v -> {
            checkPermission();
        } );
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBackground();
            }
        });
    }



    //Interstitial Ads

    private void interstitialAds() {

    AdRequest adRequest = new AdRequest.Builder().build();

      InterstitialAd.load(this,"ca-app-pub-9573006086971804/9401555836", adRequest,
            new InterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            // The mInterstitialAd reference will be null until
            // an ad is loaded.

            mInterstitialAd.show(FullImageActivity.this);

            mInterstitialAd = interstitialAd;

            Log.i(TAG, "onAdLoaded");
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            // Handle the error
            Log.i(TAG, loadAdError.getMessage());
            mInterstitialAd = null;
        }


    });



    }



//Banner Ads Function

    private void bannerAds() {
        adView.loadAd(new AdRequest.Builder().build());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened: ");

            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: ");

            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdImpression() {
                Log.d(TAG, "onAdImpression: ");
            }
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError){
                Log.d(TAG, "onAdFailedToLoad: "+loadAdError.getMessage());
            }
        });
    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                if (report.areAllPermissionsGranted()){
                    downloadImage();
                }else{
                    Toast.makeText(FullImageActivity.this, "Please allow all permission", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }


        }).check();
    }

    private void downloadImage() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Downloading...");
        pd.setCancelable(false);
        pd.show();

        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        PRDownloader.download(url, file.getPath(), URLUtil.guessFileName(url, null, null))
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long per = progress.currentBytes*100 / progress.totalBytes;
                        pd.setMessage("Downloading : "+per+" %");

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        pd.dismiss();
                        Toast.makeText(FullImageActivity.this, "Downloading Completed", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Error error) {
                        pd.dismiss();
                        Toast.makeText(FullImageActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }


                });
    }

    private void setBackground() {
        Bitmap bitmap = ((BitmapDrawable)fullImage.getDrawable()).getBitmap();
        WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
        try {
            manager.setBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}