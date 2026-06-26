/*
 * Copyright (c) 2026.  little3201.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.leafage.common.core.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File service.
 *
 * @author wq li
 * @version $Id: $Id
 * @since 0.4.0
 */
public abstract class AbstractFileService {

    /**
     * Construct
     */
    protected AbstractFileService() {
    }

    /**
     * create zip file
     *
     * @param sourceDir source dir.
     * @param zipFile   zip file.
     */
    protected void createZipFile(Path sourceDir, Path zipFile) {
        try (FileOutputStream fos = new FileOutputStream(zipFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos);
             Stream<Path> walk = Files.walk(sourceDir)) {

            walk.filter(Files::isRegularFile).forEach(filePath -> {
                try {
                    zipFile(filePath, sourceDir, zos);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to zip file: " + filePath, e);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException("Failed to create zip file", e);
        }
    }

    /**
     * zip file.
     *
     * @param fileToZip the file will to zip.
     * @param sourceDir source dir.
     * @param zos       the stream.
     * @throws java.io.IOException the io exception.
     */
    protected void zipFile(Path fileToZip, Path sourceDir, ZipOutputStream zos) throws IOException {
        Path relativePath = sourceDir.relativize(fileToZip);
        ZipEntry zipEntry = new ZipEntry(relativePath.toString());
        zos.putNextEntry(zipEntry);

        Files.copy(fileToZip, zos);
        zos.closeEntry();
    }

    /**
     * clear history dir.
     *
     * @param path the target dir.
     */
    protected void clearHistory(Path path) {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> pathStream = Files.walk(path)) {
            pathStream.sorted(Comparator.reverseOrder()).forEach(file -> {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    throw new RuntimeException("Clear history error.", e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("File delete failure.", e);
        }
    }

}
