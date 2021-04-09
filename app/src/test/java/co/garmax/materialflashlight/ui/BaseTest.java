package co.garmax.materialflashlight.ui;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.junit.BeforeClass;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class BaseTest {

    @BeforeClass
    public static void init() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable
                -> Schedulers.trampoline());

        Timber.plant(new UnitTestTree());
    }

    public static class UnitTestTree extends Timber.Tree {

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (TextUtils.isEmpty(tag)) {
                tag = "TEST";
            }

            String logMessage = "";

            if (!TextUtils.isEmpty(tag)) {
                logMessage += tag + " ";
            }

            if (!TextUtils.isEmpty(message)) {
                logMessage += message + " ";
            }

            if (t != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                t.printStackTrace(pw);
                logMessage += "\n" + sw.toString();
            }

            System.out.println(logMessage);
        }
    }
}
