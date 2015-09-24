package com.jaynewstrom.autoModuleSample;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true)
final class ApplicationModule {

    @Provides @Singleton String provideTestString() {
        return "It's injected";
    }
}
