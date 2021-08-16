package co.garmax.materialflashlight.service

import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import co.garmax.materialflashlight.R
import co.garmax.materialflashlight.features.LightManager
import co.garmax.materialflashlight.utils.PostExecutionThread
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

/**
 * Service for android notification panel icon
 */
@RequiresApi(api = Build.VERSION_CODES.N)
class LightTileService : TileService() {

    private val lightManager: LightManager by inject()

    private val postExecutionThread: PostExecutionThread by inject()

    private var disposableToggleState: Disposable? = null

    override fun onClick() {
        super.onClick()

        when (qsTile.state) {
            Tile.STATE_ACTIVE -> {
                setCurrentState(Tile.STATE_INACTIVE)
                lightManager.turnOff()
            }
            Tile.STATE_INACTIVE -> {
                setCurrentState(Tile.STATE_ACTIVE)
                lightManager.turnOn()
            }
            Tile.STATE_UNAVAILABLE -> {
            }
        }
    }

    private fun setCurrentState(state: Int) {
        val tile = qsTile
        qsTile.state = state

        when (state) {
            Tile.STATE_ACTIVE -> tile.icon = Icon.createWithResource(
                applicationContext,
                R.drawable.ic_quick_settings_active
            )
            Tile.STATE_INACTIVE -> tile.icon = Icon.createWithResource(
                applicationContext,
                R.drawable.ic_quick_settings_inactive
            )
            Tile.STATE_UNAVAILABLE -> tile.icon = Icon.createWithResource(
                applicationContext,
                R.drawable.ic_quick_settings_unavailable
            )
        }
        tile.updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        disposableToggleState = lightManager.toggleStateStream
            .observeOn(postExecutionThread.scheduler)
            .subscribe { isTurnedOn: Boolean -> setCurrentState(if (isTurnedOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE) }

        if (!lightManager.isSupported) setCurrentState(Tile.STATE_UNAVAILABLE)
    }

    override fun onStopListening() {
        super.onStopListening()

        disposableToggleState?.dispose()
    }
}