// 
// Decompiled by Procyon v0.6.0
// 

package com.lemonclient.api.util.misc;

import java.util.zip.ZipEntry;
import java.util.function.Function;
import java.util.Comparator;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import java.net.URL;
import java.util.ArrayList;

public class ClassUtil
{
    public static ArrayList<Class<?>> findClassesInPath(final String classPath) {
        final ArrayList<Class<?>> foundClasses = new ArrayList<Class<?>>();
        final String resource = ClassUtil.class.getClassLoader().getResource(classPath.replace(".", "/")).getPath();
        if (resource.contains("!")) {
            try {
                final ZipInputStream file = new ZipInputStream(new URL(resource.substring(0, resource.lastIndexOf(33))).openStream());
                ZipEntry entry;
                while ((entry = file.getNextEntry()) != null) {
                    final String name = entry.getName();
                    if (name.startsWith(classPath.replace(".", "/") + "/") && name.endsWith(".class")) {
                        try {
                            final Class<?> clazz = Class.forName(name.substring(0, name.length() - 6).replace("/", "."));
                            foundClasses.add(clazz);
                        }
                        catch (final ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (final IOException e2) {
                e2.printStackTrace();
            }
        }
        else {
            try {
                final URL classPathURL = ClassUtil.class.getClassLoader().getResource(classPath.replace(".", "/"));
                if (classPathURL != null) {
                    final File file2 = new File(classPathURL.getFile());
                    if (file2.exists()) {
                        final String[] classNamesFound = file2.list();
                        if (classNamesFound != null) {
                            for (final String className : classNamesFound) {
                                if (className.endsWith(".class")) {
                                    foundClasses.add(Class.forName(classPath + "." + className.substring(0, className.length() - 6)));
                                }
                            }
                        }
                    }
                }
            }
            catch (final Exception e3) {
                e3.printStackTrace();
            }
        }
        foundClasses.sort(Comparator.comparing((Function<? super Class<?>, ? extends Comparable>)Class::getName));
        return foundClasses;
    }
}
