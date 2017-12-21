package test.filedownloader.downloader;

import com.google.common.base.Stopwatch;
import test.filedownloader.io.ThrottledInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FileDownloader implements Callable<Boolean> {
    private String url;
    private List<String> saveToFileList;
    private ExecutorService saveFileService;
    private int bytesInSec;
    private String outputDirectory;

    public FileDownloader(String url, List<String> saveToFileList, String outputDirectory, int bytesInSec) {
        this.url = url;
        this.saveToFileList = saveToFileList;
        saveFileService = Executors.newSingleThreadExecutor();
        this.bytesInSec = bytesInSec;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public Boolean call() throws Exception {
        URL urlToDownload = new URL(url);
        loadFile(urlToDownload, saveToFileList);
        return null;
    }

    private void loadFile(URL url, List<String> file) throws IOException, InterruptedException {
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        List<BufferedOutputStream> bosList = saveToFileList.stream()
                .map((entry) -> {
                    try {
                        return new BufferedOutputStream(new FileOutputStream(new File(outputDirectory + entry)));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        try (InputStream in = new ThrottledInputStream(httpConn.getInputStream(), bytesInSec)) {
            System.out.printf("start downloading file: %s\r\n", url);
            int length = -1;
            byte[] buffer = new byte[1024];
            long readByteCount = 0;
            Stopwatch stopwatch = Stopwatch.createStarted();
            while ((length = in.read(buffer)) > -1) {
                readByteCount += length;
                final byte[] writeBuffer = Arrays.copyOf(buffer, length);
                WriteTask task = new WriteTask(writeBuffer, length, bosList);
                saveFileService.submit(task);
            }
            stopwatch.stop();
            System.out.printf("file: %s was downloaded. Read: %d bytes, loading time: %.3f secs\r\n", url,
                    readByteCount, ((float) stopwatch.elapsed().toMillis() / 1000));
            saveFileService.shutdown();
            while (!saveFileService.awaitTermination(1, TimeUnit.SECONDS)) {
            }
            for (BufferedOutputStream os : bosList) {
                os.flush();
            }

        } finally {
            for (BufferedOutputStream os : bosList) {
                os.close();
            }
            httpConn.disconnect();
        }
    }


    class WriteTask implements Runnable {
        byte[] writeBuffer;
        int writeLength;
        List<BufferedOutputStream> bosList;

        WriteTask(byte[] writeBuffer, int writeLength, List<BufferedOutputStream> bos) {
            this.writeBuffer = writeBuffer;
            this.writeLength = writeLength;
            this.bosList = bos;
        }

        @Override
        public void run() {
            try {
                for (BufferedOutputStream os : bosList) {
                    os.write(writeBuffer, 0, writeLength);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
