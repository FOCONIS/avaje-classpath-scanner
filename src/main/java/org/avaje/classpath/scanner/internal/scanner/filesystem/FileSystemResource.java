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
package org.avaje.classpath.scanner.internal.scanner.filesystem;

import org.avaje.classpath.scanner.ClassPathScanException;
import org.avaje.classpath.scanner.Resource;
import org.avaje.classpath.scanner.internal.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * A resource on the filesystem.
 */
public class FileSystemResource implements Resource, Comparable<FileSystemResource> {
  /**
   * The location of the resource on the filesystem.
   */
  private File location;

  /**
   * Creates a new ClassPathResource.
   *
   * @param location The location of the resource on the filesystem.
   */
  public FileSystemResource(String location) {
    this.location = new File(location);
  }

  public String toString() {
    return location.toString();
  }

  /**
   * @return The location of the resource on the classpath.
   */
  public String getLocation() {
    return location.getPath().replace('\\','/');
  }

  /**
   * Retrieves the location of this resource on disk.
   *
   * @return The location of this resource on disk.
   */
  public String getLocationOnDisk() {
    return location.getAbsolutePath();
  }

  /**
   * Loads this resource as a string.
   *
   * @param encoding The encoding to use.
   * @return The string contents of the resource.
   */
  public String loadAsString(String encoding) {
    try {
      InputStream inputStream = new FileInputStream(location);
      Reader reader = new InputStreamReader(inputStream, Charset.forName(encoding));

      return FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      throw new ClassPathScanException("Unable to load filesystem resource: " + location.getPath() + " (encoding: " + encoding + ")", e);
    }
  }

  /**
   * Loads this resource as a byte array.
   *
   * @return The contents of the resource.
   */
  public byte[] loadAsBytes() {
    try {
      InputStream inputStream = new FileInputStream(location);
      return FileCopyUtils.copyToByteArray(inputStream);
    } catch (IOException e) {
      throw new ClassPathScanException("Unable to load filesystem resource: " + location.getPath(), e);
    }
  }

  /**
   * @return The filename of this resource, without the path.
   */
  public String getFilename() {
    return location.getName();
  }

  @SuppressWarnings("NullableProblems")
  public int compareTo(FileSystemResource o) {
    return location.compareTo(o.location);
  }
}
