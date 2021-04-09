package co.garmax.materialflashlight.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import javax.inject.Inject;

import co.garmax.materialflashlight.features.LightManager;
import dagger.android.AndroidInjection;

/**
 * Activity to call runtime permission from any place like service, widget or activity
 */
public class PermissionsActivity extends AppCompatActivity {
    /**
     * Just ask runtime permissions
     */
    public static final int RC_CHECK_PERMISSION = 0;

    private static final String EXTRA_PERMISSIONS_ARRAY = "extra_permissions_array";
    private static final String EXTRA_REQUEST_CODE = "extra_request_code";

    @Inject
    LightManager lightManager;

    public static void startActivity(Context context, String[] permissions) {
        Intent intent = new Intent(context, PermissionsActivity.class);

        intent.putExtra(EXTRA_PERMISSIONS_ARRAY, permissions);
        intent.putExtra(EXTRA_REQUEST_CODE, RC_CHECK_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null) {
            int requestCode = intent.getIntExtra(EXTRA_REQUEST_CODE, RC_CHECK_PERMISSION);
            String[] permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS_ARRAY);

            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == RC_CHECK_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lightManager.turnOn();
            }
        }

        finish();
    }
}
