
import java.nio.file.*;
import java.io.*;
import java.util.*;

public class Q7_GenerateFiles {

    public static void main(String[] args)
            throws IOException {

        Path directory = Paths.get("large_scores");

        Files.createDirectories(directory);

        Random random = new Random(42);

        for (int i = 1; i <= 10000; i++) {

            Path file = directory.resolve(
                    "student_" + i + ".txt");

            if (i % 100 == 0) {
                Files.writeString(file, "corrupt");
            } else {
                int score = random.nextInt(101);
                Files.writeString(
                        file, String.valueOf(score));
            }
        }

        System.out.println(
                "10,000 score files generated.");

        System.out.println(
                "Folder: " + directory.toAbsolutePath());
    }
}
