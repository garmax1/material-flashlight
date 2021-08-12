package co.garmax.materialflashlight.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import co.garmax.materialflashlight.features.LightManager
import org.koin.android.ext.android.inject

/**
 * Activity to call runtime permission from any place like service, widget or activity
 */
class PermissionsActivity : AppCompatActivity() {

    private val lightManager: LightManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getIntent()?.getStringArrayExtra(EXTRA_PERMISSIONS_ARRAY)?.let {
            ActivityCompat.requestPermissions(this, it, RC_CHECK_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_CHECK_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                lightManager.turnOn()
            }
        }

        finish()
    }

    companion object {
        private const val RC_CHECK_PERMISSION = 0

        private const val EXTRA_PERMISSIONS_ARRAY = "extra_permissions_array"

        fun startActivity(context: Context, permissions: Array<String>) {
            Intent(context, PermissionsActivity::class.java).apply {
                putExtra(EXTRA_PERMISSIONS_ARRAY, permissions)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.let {
                context.startActivity(it)
            }
        }
    }
}