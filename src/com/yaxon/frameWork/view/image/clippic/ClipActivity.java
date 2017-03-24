package com.yaxon.frameWork.view.image.clippic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.yaxon.frameWork.R;

import java.io.ByteArrayOutputStream;

/**
 * @author guojiaping
 * @version 2016/12/29
 */
public class ClipActivity extends Activity {
    private ClipImageLayout mClipImageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clip_image);

        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_clip:
                Bitmap bitmap = mClipImageLayout.clip();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();

                Intent intent = new Intent(this, ShowImageActivity.class);
                intent.putExtra("bitmap", datas);
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
