package test.filedownloader.model;

public class Configuration {
    private int numberOfThreads;
    private int loadRate;
    private String file;
    private String outputDirectory;

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(String loadRate) {
        if (loadRate.endsWith("k")) {
            this.loadRate = Integer.parseInt(loadRate.substring(0, loadRate.length() - 1)) * 1024;
        } else if (loadRate.endsWith("m")) {
            this.loadRate = Integer.parseInt(loadRate.substring(0, loadRate.length() - 1)) * 1024 * 1024;
        } else
            this.loadRate = Integer.parseInt(loadRate);
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "numberOfThreads=" + numberOfThreads +
                ", loadRate=" + loadRate +
                ", file='" + file + '\'' +
                ", outputDirectory='" + outputDirectory + '\'' +
                '}';
    }
}
