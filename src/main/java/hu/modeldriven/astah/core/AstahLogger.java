package hu.modeldriven.astah.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AstahLogger {

    private static String filePath;

    static {
        var now = LocalDateTime.now();
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        var formattedDateTime = now.format(formatter);

        // Create a unique file name with the current date and time
        var fileName = "astah_openapi_" + formattedDateTime + ".txt";

        // Get the temporary directory path
        var tempDir = System.getProperty("java.io.tmpdir");

        // Construct the full file path
        AstahLogger.filePath = Paths.get(tempDir, fileName).toString();
    }

    public static void log(String message) {
        try {
            var writer = new BufferedWriter(new FileWriter(filePath, true));

            // Append the content to the file
            writer.write(message);
            writer.newLine(); // Add a newline for readability

            // Close the writer
            writer.close();

            System.err.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
