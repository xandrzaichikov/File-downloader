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

public class FileDownloader implements Callable<Boolean> {
    private String url;
    private List<String> saveToFileList;
    private ExecutorService saveFileService;
    private int bytesInSec;

    public FileDownloader(String url, List<String> saveToFileList, int bytesInSec) {
        this.url = url;
        this.saveToFileList = saveToFileList;
        saveFileService = Executors.newSingleThreadExecutor();
        this.bytesInSec = bytesInSec;
    }

    @Override
    public Boolean call() throws Exception {
        URL urlToDownload = new URL(url);
        loadFile(urlToDownload, saveToFileList.get(0));
        return null;
    }

    private void loadFile(URL url, String file) throws IOException, InterruptedException {
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        try (InputStream in = new ThrottledInputStream(httpConn.getInputStream(), bytesInSec);
             FileOutputStream fos = new FileOutputStream(new File(file));
             BufferedOutputStream bos = new BufferedOutputStream(fos);) {
            System.out.println("reading file...");
            int length = -1;
            byte[] buffer = new byte[1024];
            long readByteCount = 0;
            Stopwatch stopwatch = Stopwatch.createStarted();
            while ((length = in.read(buffer)) > -1) {
                readByteCount += length;
                final byte[] writeBuffer = Arrays.copyOf(buffer, length);
                final int writeLength = length;
                //final BufferedOutputStream fbos = bos;
//                Callable<Boolean> write = () -> {
//                    fbos.write(writeBuffer, 0, writeLength);
//                    return true;
//                };
//                saveFileService.submit(write);
                WriteTask task = new WriteTask(writeBuffer, writeLength, fos);
//                saveFileService.submit(()->{
//                    try {
//                        fbos.write(writeBuffer, 0, writeLength);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                });
//                bos.write(writeBuffer, 0, writeLength);

            }
            stopwatch.stop();
//            System.out.printf("download time: %1$.2f", stopwatch.elapsed().);
            System.out.printf("file: %s was downloaded. Read %d Bytes, load time: %.3f secs\r\n", url,
                    readByteCount, ((float)stopwatch.elapsed().toMillis() / 1000));
            saveFileService.shutdown();
            while (!saveFileService.awaitTermination(1, TimeUnit.SECONDS)) {
            }
            bos.flush();
        } finally {
            httpConn.disconnect();
        }
    }


    class WriteTask implements Runnable {
        byte[] writeBuffer;
        int writeLength;
        OutputStream bos;

        public WriteTask(byte[] writeBuffer, int writeLength, OutputStream bos) {
            this.writeBuffer = writeBuffer;
            this.writeLength = writeLength;
            this.bos = bos;
        }

        @Override
        public void run() {
            try {
                bos.write(writeBuffer, 0, writeLength);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
