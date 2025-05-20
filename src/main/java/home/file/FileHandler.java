/*******************************************************************************
 * Copyright 2021-2025 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import home.gui.component.CustomJFileChooserImpExp.DataFormat;

public class FileHandler {

    // Used two variants because of Eclipse's result comparision tool
    // (in test results always showing difference in newline characters), but
    // - 'LINE_SEPARATOR_XML_JSON' works for XML and JSON files,
    // - 'LINE_SEPARATOR_YAML_CSV' works for YAML and CSV files.
    private static final String LINE_SEPARATOR_XML_JSON = System.lineSeparator();
    private static final String LINE_SEPARATOR_YAML_CSV = "\n";

    public static String readStringFromFile(String filePath, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }

        String lineSeparator = getLineSeparator(filePath);

        try (BufferedReader reader = Files
                .newBufferedReader(Paths.get(filePath), charset)) {
            var sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(lineSeparator);
            }
            return sb.toString().strip();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while read file : " + filePath, e);
        }
    }

    private static String getLineSeparator(String filePath) {
        String extension = getExtension(filePath);
        if (extension == null
                || extension.equalsIgnoreCase(DataFormat.YAML.getExtension())
                || extension.equalsIgnoreCase(DataFormat.CSV.getExtension())) {
            return LINE_SEPARATOR_YAML_CSV;
        }
        return LINE_SEPARATOR_XML_JSON;
    }

    private static String getExtension(String filePath) {
        String extension = null;
        int i = filePath.lastIndexOf('.');
        if (i > 0) {
            extension = filePath.substring(i + 1);
        }
        return extension;
    }

    public static void writeStringToFile(File file, String text) {
        try (BufferedWriter writer = Files
                .newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while write to file : "
                    + file.getAbsolutePath(), e);
        }
    }
}
