package home.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    // Commented, because it doesn't work with Eclipse's result comparision tool
    // (in test results always showing difference in newline characters).
    // private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String LINE_SEPARATOR = "\n";

    public static String readStringFromFile(String filePath, Charset charset) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }

        try (BufferedReader reader = Files
                .newBufferedReader(Paths.get(filePath), charset)) {
            var sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(LINE_SEPARATOR);
            }
            return sb.toString().strip();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while read file : " + filePath, e);
        }
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
