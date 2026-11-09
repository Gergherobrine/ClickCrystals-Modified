package io.github.itzispyder.clickcrystals.scripting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/** Loads CCS as UTF-8 and reports I/O errors rather than silently executing an empty script. */
public final class ScriptSourceReader {
    private ScriptSourceReader() {}

    public static String read(String path) {
        Objects.requireNonNull(path, "path");
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = ScriptComments.stripLine(line);
                if (line.isEmpty()) continue;
                result.append(line);
                if (!line.endsWith("{") && !line.endsWith("}") && !line.endsWith(";")) {
                    result.append(";");
                }
                result.append(" ");
            }
        } catch (IOException ex) {
            throw new UncheckedIOException("Cannot read CCS script: " + path, ex);
        }
        return result.toString().trim();
    }
}
