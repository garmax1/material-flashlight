package co.garmax.materialflashlight.ui;

import co.garmax.materialflashlight.di.FragmentScope;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface RootModule {

    @FragmentScope
    @ContributesAndroidInjector
    LightFragment lightFragment();

    @FragmentScope
    @ContributesAndroidInjector
    MainFragment mainFragment();
}
