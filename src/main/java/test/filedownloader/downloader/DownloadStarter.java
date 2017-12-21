package test.filedownloader.downloader;

import test.filedownloader.model.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class DownloadStarter {
    private ExecutorService service;
    private Configuration config;

    public DownloadStarter(Configuration config) {
        this.config = config;
        service = Executors.newFixedThreadPool(config.getNumberOfThreads());
    }

    public void start() {
        Map<String, List<String>> url2Files = parseFile(config.getFile());

        List<FileDownloader> fileDownloaderList = url2Files.entrySet().stream()
                .map((e) -> new FileDownloader(e.getKey(), e.getValue(), config.getOutputDirectory(), config.getLoadRate()))
                .collect(Collectors.toList());

        try {
            service.invokeAll(fileDownloaderList);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        service.shutdownNow();
    }

    private Map<String, List<String>> parseFile(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || Files.isDirectory(path)) {
            System.out.printf("file %s does not exit or is directory\r\n", filePath);
        }
        try {
            return Files.lines(path).map(s -> s.split(" +"))
                    .collect(Collectors.groupingBy(arr -> arr[0], Collectors.mapping(arr -> arr[1], toList())));
        } catch (IOException e) {
            System.out.printf("error occurred while read file:%s", path);
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }


}
