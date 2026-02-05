package ru.job4j.thred;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class Wget implements Runnable {
    private final String url;
    private final int speed;

    public Wget(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        long start = System.currentTimeMillis();
        int totalBytes = 0;

        try (var input = new URL(url).openStream();
             var output = new FileOutputStream(getFileName(url))) {

            int bytesRead;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) != -1) {
                output.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;

                if (totalBytes >= speed) {
                    long elapsedMs = System.currentTimeMillis() - start;
                    long pauseMs = 1000 - elapsedMs;  // 1000 мс = 1 секунда

                    if (pauseMs > 0) {
                        Thread.sleep(pauseMs);
                        System.out.println("Сделали паузу " + pauseMs + " мс");
                    }

                    totalBytes = 0;
                    start = System.currentTimeMillis();
                }
            }

            System.out.println("\nDownload complete: " + getFileName(url));

        } catch (Exception e) {
            System.err.println("Download error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFileName(String url) {
        String name = url.substring(url.lastIndexOf('/') + 1).split("\\?")[0];
        return name.isEmpty() ? "downloaded_file" : name;
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Usage: java Wget <url> <speed_in_bytes_per_ms>");
        }

        String url = args[0];
        int speed = Integer.parseInt(args[1]);

        if (url.isEmpty() || !url.startsWith("http")) {
            throw new IllegalArgumentException("Invalid URL");
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }

        Thread wget = new Thread(new Wget(url, speed));
        wget.start();
        wget.join();
    }

}
