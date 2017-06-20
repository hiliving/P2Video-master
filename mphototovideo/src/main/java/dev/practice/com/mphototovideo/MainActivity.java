package dev.practice.com.mphototovideo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static dev.practice.com.mphototovideo.Config.REQUEST_CODE_CHOOSE;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.chose)
    Button chose;
    @Bind(R.id.render)
    Button render;
    @Bind(R.id.progress)
    ProgressBar progress;
    private List<Uri> mSelected;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.chose,R.id.render})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.chose:
                cheseFile();
                break;
            case R.id.render:
                performJcodec();
                break;
        }

    }

    private void cheseFile() {
        Matisse.from(MainActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(2000)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("AAAAAMatisse", "mSelected: " +mSelected.size());
            for (int i = 0; i < mSelected.size(); i++) {
                list.add(getPath(mSelected.get(i)));
            }
        }
    }
    public String getPath(Uri uri){

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor actualimagecursor = managedQuery(uri,proj,null,null,null);

        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        actualimagecursor.moveToFirst();

        String img_path = actualimagecursor.getString(actual_image_column_index);

        return img_path;
    }


    private void performJcodec() {
        progress.setVisibility(View.VISIBLE);
        try {

            Log.e("performJcodec: ", "执行开始");
            SequenceEncoderMp4 se   = null;
            File outFile = new File(Constants.FILE_VIDEO_FLODER);
            if (!outFile.exists()){
                outFile.mkdirs();
            }
            File out = new File(Constants.FILE_VIDEO_FLODER, "jcode.mp4");
            se = new SequenceEncoderMp4(out);

            final File[] files = new File[list.size()];
            for (int i = 0; i < list.size(); i++) {
                files[i] = new File(list.get(i));
                Log.d("AAAAAMatisse", "mSelected: " +list.get(i));
                if (!files[i].exists()) { break; }
                Bitmap frame = BitmapUtil.decodeSampledBitmapFromFile(files[i].getAbsolutePath() , 480 , 320);
                se.encodeImage(frame);
                Log.d("performJcodec: ", "执行到的图片是 " + i);
            }
            se.finish();
            Log.d("performJcodec: ", "执行完成");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"导出完成",Toast.LENGTH_LONG).show();
                }
            });
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(out)));
            progress.setVisibility(View.INVISIBLE);
        } catch (final IOException e) {
            Log.e("performJcodec: ", "执行异常 " + e.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
