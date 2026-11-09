import io.github.itzispyder.clickcrystals.scripting.ScriptSourceReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class ScriptSourceReaderTest {
    static void check(boolean ok, String why) { if (!ok) throw new AssertionError(why); }
    public static void main(String[] ignored) throws Exception {
        var dir = Files.createTempDirectory("ccs-source");
        try {
            var script = dir.resolve("utf8.ccs");
            Files.writeString(script, "say \"Привет // мир\"; // comment\nsay test // comment\n", StandardCharsets.UTF_8);
            check(ScriptSourceReader.read(script.toString()).equals("say \"Привет // мир\"; say test;"), "unicode/comments");
            var blank = dir.resolve("blank.ccs");
            Files.writeString(blank, "", StandardCharsets.UTF_8);
            check(ScriptSourceReader.read(blank.toString()).isEmpty(), "empty file");
            boolean failed = false;
            try { ScriptSourceReader.read(dir.resolve("missing.ccs").toString()); }
            catch (UncheckedIOException ex) { failed = true; }
            check(failed, "missing file must report failure");
            var invalid = dir.resolve("invalid.ccs");
            Files.write(invalid, new byte[] { (byte) 0xC3, (byte) 0x28 });
            failed = false;
            try { ScriptSourceReader.read(invalid.toString()); }
            catch (UncheckedIOException ex) { failed = true; }
            check(failed, "invalid UTF-8 must report failure");
        } finally {
            try (var stream = Files.list(dir)) {
                for (var file : stream.toList()) Files.deleteIfExists(file);
            }
            Files.deleteIfExists(dir);
        }
        System.out.println("ScriptSourceReaderTest: PASS");
    }
}
