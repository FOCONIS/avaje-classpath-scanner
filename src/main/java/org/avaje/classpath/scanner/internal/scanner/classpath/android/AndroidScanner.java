/**
 * Copyright 2010-2016 Boxfuse GmbH
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.avaje.classpath.scanner.internal.scanner.classpath.android;

import android.content.Context;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import org.avaje.classpath.scanner.ClassPathScanException;
import org.avaje.classpath.scanner.Location;
import org.avaje.classpath.scanner.ClassFilter;
import org.avaje.classpath.scanner.ResourceFilter;
import org.avaje.classpath.scanner.Resource;
import org.avaje.classpath.scanner.andriod.ContextHolder;
import org.avaje.classpath.scanner.internal.ResourceAndClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Class & resource scanner for Android.
 */
public class AndroidScanner implements ResourceAndClassScanner {

  private static final Logger LOG = LoggerFactory.getLogger(AndroidScanner.class);

  private final Context context;

  private final PathClassLoader classLoader;

  public AndroidScanner(ClassLoader classLoader) {
    this.classLoader = (PathClassLoader) classLoader;
    context = ContextHolder.getContext();
    if (context == null) {
      throw new ClassPathScanException("Unable to create scanner. " +
          "Within an activity you can fix this with org.avaje.classpath.scanner.android.ContextHolder.setContext(this);");
    }
  }

  public List<Resource> scanForResources(Location location, ResourceFilter predicate) {

    try {
      List<Resource> resources = new ArrayList<Resource>();
      String path = location.getPath();
      for (String asset : context.getAssets().list(path)) {
        if (predicate.isMatch(asset)) {
          resources.add(new AndroidResource(context.getAssets(), path, asset));
        }
      }

      return resources;

    } catch (IOException e) {
      throw new ClassPathScanException(e);
    }
  }

  public List<Class<?>> scanForClasses(Location location, ClassFilter predicate) {

    try {

      String pkg = location.getPath().replace("/", ".");

      List<Class<?>> classes = new ArrayList<Class<?>>();

      DexFile dex = new DexFile(context.getApplicationInfo().sourceDir);
      Enumeration<String> entries = dex.entries();
      while (entries.hasMoreElements()) {
        String className = entries.nextElement();
        if (className.startsWith(pkg)) {
          Class<?> clazz = classLoader.loadClass(className);
          if (predicate.isMatch(clazz)) {
            classes.add(clazz);
            LOG.trace("... found class: {}", className);
          }
        }
      }
      return classes;

    } catch (IOException e) {
      throw new ClassPathScanException(e);

    } catch (ClassNotFoundException e) {
      throw new ClassPathScanException(e);
    }
  }
}
