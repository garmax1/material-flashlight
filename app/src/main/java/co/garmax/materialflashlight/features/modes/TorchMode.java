package co.garmax.materialflashlight.features.modes;

/**
 * Just steady light
 */
public class TorchMode extends ModeBase {

    @Override
    public void start() {
        setLightState(true);
    }

    @Override
    public void stop() {
        setLightState(false);
    }

    @Override
    public boolean checkPermissions() {
        return true;
    }
}
