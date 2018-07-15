package co.garmax.materialflashlight.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import dagger.android.support.AndroidSupportInjection;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View decorView = requireActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                if (isInImmersiveMode()) {
                    setFullscreen();
                } else {
                    exitFullscreen();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final View decorView = requireActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(null);
    }

    boolean isInImmersiveMode() {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    private static boolean isImmersiveAvailable() {
        return android.os.Build.VERSION.SDK_INT >= 19;
    }

    private void setFullscreen() {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;

        if (isImmersiveAvailable()) {
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        requireActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private void exitFullscreen() {
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}
