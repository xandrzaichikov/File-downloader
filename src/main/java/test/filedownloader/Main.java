package test.filedownloader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import test.filedownloader.downloader.FileDownloader;
import test.filedownloader.model.Configuration;

import java.util.Arrays;
import java.util.Collections;

//         -n количество одновременно качающих потоков (1,2,3,4....)
//        -l общее ограничение на скорость скачивания, для всех потоков, размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)
//        -f путь к файлу со списком ссылок
//        -o имя папки, куда складывать скачанные файлы
//java -jar utility.jar -n 5 -l 2000k -o output_folder -f links.txt
public class Main {
    private static final String NUMBER = "n";
    private static final String LOAD_RATE = "l";
    private static final String FILE = "f";
    private static final String OUTPUT = "o";


    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<Integer> number = parser.accepts(NUMBER).withRequiredArg().ofType(Integer.class);
        ArgumentAcceptingOptionSpec<String> loadRate = parser.accepts(LOAD_RATE).withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> file = parser.accepts(FILE).withRequiredArg().ofType(String.class);
        ArgumentAcceptingOptionSpec<String> output = parser.accepts(OUTPUT).withRequiredArg().ofType(String.class);
        OptionSet optionSet = parser.parse(args);
        if (!optionSet.has(number) || !optionSet.hasArgument(number)
                || !optionSet.has(loadRate) || !optionSet.hasArgument(loadRate)
                || !optionSet.has(file) || !optionSet.hasArgument(file)
                || !optionSet.has(output) || !optionSet.hasArgument(output)) {
            printHelp();
            System.exit(1);
        }
        Configuration config = new Configuration();
        config.setNumberOfThreads(optionSet.valueOf(number));
        config.setLoadRate(optionSet.valueOf(loadRate));
        config.setFile(optionSet.valueOf(file));
        config.setOutputFolder(optionSet.valueOf(output));

        FileDownloader downloader = new FileDownloader("http://www.goldmansachs.com/gs-collections/presentations/2014_05_19_NY_Java_User_Group.pdf",
                Arrays.asList("D:/test.pdf"), 300000);
        try {
            long start = System.currentTimeMillis();
            Boolean call = downloader.call();
            long finish = System.currentTimeMillis();
            float loadTime = ((float)(finish - start))/((float)1000);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void printHelp() {
        System.out.println("Enter command line with all options like: -n <number> -l <rate> -o <output_folder> -f <file_path>");
    }
}
