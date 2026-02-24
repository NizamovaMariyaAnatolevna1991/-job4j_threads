package ru.job4j.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Predicate;

public class ContentStrategery {
    private File file;

    public String content(ParseFile parseFile, Predicate<Character> filter) throws FileNotFoundException {
        File file = parseFile.getFile();
        if (!file.exists() || !file.canRead()) {
            throw new FileNotFoundException("File not found or not readable: " + file.getAbsolutePath());
        }

        StringBuilder output = new StringBuilder();

        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            int data;

            while ((data = input.read()) != -1) {
                char ch = (char) data;
                if (filter.test(ch)) {
                    output.append(ch);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return output.toString();
    }

    public void saveContent(ParseFile parseFile, String content) {
        try (OutputStream output = new BufferedOutputStream(new FileOutputStream(parseFile.getFile()))) {
            output.write(content.getBytes(StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
