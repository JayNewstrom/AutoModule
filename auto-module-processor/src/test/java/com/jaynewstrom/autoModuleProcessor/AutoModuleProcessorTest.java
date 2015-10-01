package com.jaynewstrom.autoModuleProcessor;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public final class AutoModuleProcessorTest {

    @Test public void testProcessorWithoutAddsTo() {
        JavaFileObject sampleActivity = JavaFileObjects.forSourceString("com.example.MainActivity",
                Joiner.on('\n').join(
                        "package com.example;",
                        "import com.jaynewstrom.autoModule.AutoModule;",
                        "@AutoModule",
                        "public final class MainActivity {",
                        "}"
                )
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("MainActivityModule",
                Joiner.on('\n').join(
                        "package com.example;",
                        "",
                        "import dagger.Module;",
                        "",
                        "@Module(",
                        "    injects = MainActivity.class,",
                        "    addsTo = java.lang.Void.class",
                        ")",
                        "final class MainActivityModule {",
                        "}"));

        assertAbout(javaSource())
                 .that(sampleActivity)
                 .processedWith(new AutoModuleProcessor())
                 .compilesWithoutError()
                 .and()
                 .generatesSources(expectedSource);
    }

    @Test public void testProcessorWithAddsTo() {
        JavaFileObject sampleActivity = JavaFileObjects.forSourceString("com.example.MainActivity",
                Joiner.on('\n').join(
                        "package com.example;",
                        "import com.jaynewstrom.autoModule.AutoModule;",
                        "import com.example.MainActivity.BaseModule;",
                        "@AutoModule(addsTo = BaseModule.class)",
                        "public final class MainActivity {",
                        "static final class BaseModule {}",
                        "}"
                )
        );

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("MainActivityModule",
                Joiner.on('\n').join(
                        "package com.example;",
                        "",
                        "import dagger.Module;",
                        "",
                        "@Module(",
                        "    injects = MainActivity.class,",
                        "    addsTo = com.example.MainActivity.BaseModule.class",
                        ")",
                        "final class MainActivityModule {",
                        "}"));

        assertAbout(javaSource())
                 .that(sampleActivity)
                 .processedWith(new AutoModuleProcessor())
                 .compilesWithoutError()
                 .and()
                 .generatesSources(expectedSource);
    }
}
