package com.jaynewstrom.autoModuleProcessor;

import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static java.util.Arrays.asList;

public final class AutoModuleProcessorTest {

    @Test public void testProcessorWithoutAddsTo() {
        JavaFileObject sampleActivity = JavaFileObjects.forSourceLines("com.example.MainActivity",
                "package com.example;",
                "import com.jaynewstrom.autoModule.AutoModule;",
                "@AutoModule",
                "public final class MainActivity {",
                "}"
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceLines("MainActivityModule",
                "package com.example;",
                "",
                "import dagger.Module;",
                "import java.lang.Void;",
                "",
                "@Module(",
                "    injects = MainActivity.class,",
                "    addsTo = Void.class",
                ")",
                "final class MainActivityModule {",
                "}"
        );

        assertAbout(javaSource())
                .that(sampleActivity)
                .processedWith(new AutoModuleProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void testProcessorWithAddsTo() {
        JavaFileObject sampleActivity = JavaFileObjects.forSourceLines("com.example.MainActivity",
                "package com.example;",
                "import com.jaynewstrom.autoModule.AutoModule;",
                "import com.example.MainActivity.BaseModule;",
                "@AutoModule(addsTo = BaseModule.class)",
                "public final class MainActivity {",
                "static final class BaseModule {}",
                "}"
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceLines("MainActivityModule",
                "package com.example;",
                "",
                "import dagger.Module;",
                "",
                "@Module(",
                "    injects = MainActivity.class,",
                "    addsTo = MainActivity.BaseModule.class",
                ")",
                "final class MainActivityModule {",
                "}"
        );

        assertAbout(javaSource())
                .that(sampleActivity)
                .processedWith(new AutoModuleProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test public void testProcessorWithAddsToModuleInAnotherPackage() {
        JavaFileObject sampleActivity = JavaFileObjects.forSourceLines("com.example.MainActivity",
                "package com.example;",
                "import com.jaynewstrom.autoModule.AutoModule;",
                "import com.example.extra.BaseModule;",
                "@AutoModule(addsTo = BaseModule.class)",
                "public final class MainActivity {",
                "}"
        );

        JavaFileObject baseModule = JavaFileObjects.forSourceLines("com.example.extra.BaseModule",
                "package com.example.extra;",
                "public final class BaseModule {",
                "}"
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceLines("MainActivityModule",
                "package com.example;",
                "",
                "import com.example.extra.BaseModule;",
                "import dagger.Module;",
                "",
                "@Module(",
                "    injects = MainActivity.class,",
                "    addsTo = BaseModule.class",
                ")",
                "final class MainActivityModule {",
                "}"
        );

        assertAbout(javaSources())
                .that(asList(baseModule, sampleActivity))
                .processedWith(new AutoModuleProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }
}
