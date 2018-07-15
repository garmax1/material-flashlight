package co.garmax.materialflashlight.features.modes;

/**
 * Just steady light
 */
public class TorchMode extends ModeBase {

    @Override
    public void start() {
        setBrightness(MAX_LIGHT_VOLUME);
    }

    @Override
    public void stop() {
        setBrightness(MIN_LIGHT_VOLUME);
    }

    @Override
    public boolean checkPermissions() {
        return true;
    }
}
