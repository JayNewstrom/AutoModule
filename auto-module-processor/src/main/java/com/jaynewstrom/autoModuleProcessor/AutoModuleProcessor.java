package com.jaynewstrom.autoModuleProcessor;

import com.google.auto.service.AutoService;
import com.jaynewstrom.autoModule.AutoModule;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import dagger.Module;

@AutoService(Processor.class)
public final class AutoModuleProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoModule.class.getCanonicalName());
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoModule.class)) {
            try {
                if (element.getKind() != ElementKind.CLASS) {
                    error(element, "%s annotations can only be applied to classes!", AutoModule.class.getSimpleName());
                    return false;
                }
                AutoModule instance = element.getAnnotation(AutoModule.class);

                String classPackage = getPackageName(element);
                String className = getClassName((TypeElement) element, classPackage);

                AnnotationSpec.Builder moduleAnnotation = AnnotationSpec.builder(Module.class)
                                                                        .addMember("injects", className + ".class");

                moduleAnnotation.addMember("addsTo", getField("addsTo", instance));

                TypeSpec autoModule = TypeSpec.classBuilder(className + "Module")
                                              .addModifiers(Modifier.FINAL)
                                              .addAnnotation(moduleAnnotation.build())
                                              .build();

                JavaFile javaFile = JavaFile.builder(classPackage, autoModule).build();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                error(element, "Unable to generate module.\n\n%s", e.getMessage());
            }
        }
        return true;
    }

    /*
     * Calling instance.addsTo() throws an exception.
     * This method get's the information needed, without making the compiler explode.
     * Ideally I would figure out how Annotation implements toString and use that.
     */
    private static String getField(String fieldName, AutoModule instance) {
        String instanceToString = instance.toString();
        // example toString "@com.jaynewstrom.autoModule.AutoModule(addsTo=java.lang.Void)"
        // 3 because of the @, (, and =
        int startIndex = AutoModule.class.getName().length() + fieldName.length() + 3;
        return instanceToString.substring(startIndex, instanceToString.length() - 1) + ".class";
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private String getPackageName(Element type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }
}
