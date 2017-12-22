package test.filedownloader;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import test.filedownloader.downloader.DownloadStarter;
import test.filedownloader.model.Configuration;

import java.io.File;

public class Main {
    private static final String NUMBER = "n";
    private static final String LOAD_RATE = "l";
    private static final String FILE = "f";
    private static final String OUTPUT = "o";


    public static void main(String[] args) {
        Configuration config = parseCommandLine(args);
        checkConfig(config);
        DownloadStarter downloadStarter = new DownloadStarter(config);
        downloadStarter.start();
        downloadStarter.shutdown();
    }

    private static Configuration parseCommandLine(String[] args) {
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
            System.exit(0);
        }
        Configuration config = new Configuration();
        config.setNumberOfThreads(optionSet.valueOf(number));
        config.setLoadRate(optionSet.valueOf(loadRate));
        config.setFile(optionSet.valueOf(file));
        config.setOutputDirectory(optionSet.valueOf(output));
        return config;
    }

    private static void checkConfig(Configuration config) {
        File dir = new File(config.getOutputDirectory());
        if (!dir.exists()) {
            System.out.printf("directory $s does not exist", config.getOutputDirectory());
            System.exit(0);
        }
    }

    private static void printHelp() {
        System.out.println("Enter command line with all options like: -n <number> -l <rate> -o <output_folder> -f <file_path>");
    }
}
