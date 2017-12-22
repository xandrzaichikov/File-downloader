package test.filedownloader.downloader;

import com.google.common.base.Stopwatch;
import test.filedownloader.io.ThrottledInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class FileDownloader implements Callable<Boolean> {
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private final int BUFFER_SIZE = 1024;

    private String url;
    private List<String> saveToFileList;
    private int bytesInSec;
    private String outputDirectory;

    public FileDownloader(String url, List<String> saveToFileList, String outputDirectory, int bytesInSec) {
        this.url = url;
        this.saveToFileList = saveToFileList;
        this.bytesInSec = bytesInSec;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public Boolean call() throws Exception {
        URL urlToDownload = new URL(url);
        loadFile(urlToDownload, saveToFileList);
        return true;
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
            System.out.printf("%sstart downloading file: %s\r\n", getCurrentTimeString(), url);
            int length = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            long readByteCount = 0;
            Stopwatch stopwatch = Stopwatch.createStarted();
            while ((length = in.read(buffer)) > -1) {
                readByteCount += length;
                final byte[] writeBuffer = Arrays.copyOf(buffer, length);
                write(writeBuffer, length, bosList);
            }
            stopwatch.stop();
            System.out.printf("%sfile: %s was downloaded. Read: %d bytes, loading duration: %.3f secs\r\n", getCurrentTimeString(),
                    url, readByteCount, ((float) stopwatch.elapsed().toMillis() / 1000));
            for (BufferedOutputStream os : bosList) {
                os.flush();
            }

        } catch (IOException ioe) {
            System.out.printf("%sfile $s was not downloaded IOException: %s\r\n", getCurrentTimeString(), url);
        } finally {
            for (BufferedOutputStream os : bosList) {
                os.close();
            }
            httpConn.disconnect();
        }
    }

    private String getCurrentTimeString() {
        return "[" + dateFormat.format(new Date()) + "] ";
    }

    private void write(byte[] writeBuffer, int writeLength, List<BufferedOutputStream> bosList) {
        try {
            for (BufferedOutputStream os : bosList) {
                os.write(writeBuffer, 0, writeLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
