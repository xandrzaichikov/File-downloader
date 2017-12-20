package test.filedownloader.downloader;

import test.filedownloader.model.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadStarter {
    private ExecutorService service;

    DownloadStarter(Configuration conf) {
        service = Executors.newFixedThreadPool(conf.getNumberOfThreads());


    }


}
