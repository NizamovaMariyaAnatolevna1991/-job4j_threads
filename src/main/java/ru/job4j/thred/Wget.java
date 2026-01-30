package ru.job4j.thred;

import java.io.FileOutputStream;
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
        int totalBytes = 0;

        try (var input = new URL(url).openStream();
             var output = new FileOutputStream("downloaded_file")) {

            int bytesRead;
            while ((bytesRead = input.read(buffer, 0, buffer.length)) != -1) {
                long start = System.nanoTime();

                output.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;

                long elapsedNs = System.nanoTime() - start;
                long elapsedMs = elapsedNs / 1_000_000;

                long requiredTimeMs = (long) Math.ceil((double) bytesRead / speed);
                long pauseMs = requiredTimeMs - elapsedMs;

                if (pauseMs > 0) {
                    Thread.sleep(pauseMs);
                    System.out.println("Сделали паузу " + pauseMs);
                }

            }
            System.out.printf("\rDownload complete: %d bytes%n", totalBytes);

        } catch (Exception e) {
            System.err.println("Download error: " + e.getMessage());
        }
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
