package test.filedownloader.model;

import java.awt.*;

public class Configuration {
    private int numberOfThreads;
    private int loadRate;
    private String file;
    private String outputFolder;
    private boolean rateInKb;
    private boolean rateInMb;

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public int getLoadRate() {
        return loadRate;
    }

    public void setLoadRate(int loadRate) {
        this.loadRate = loadRate;
    }

    public void setLoadRate(String loadRate) {
        if(loadRate.endsWith("k")) {
            rateInKb = true;
            setLoadRate(Integer.parseInt(loadRate.substring(0, loadRate.length()-1)));
        } else  if(loadRate.endsWith("m")) {
            rateInMb = true;
            setLoadRate(Integer.parseInt(loadRate.substring(0, loadRate.length()-1)));
        } else
            setLoadRate(Integer.parseInt(loadRate));
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public boolean isRateInKb() {
        return rateInKb;
    }

    public boolean isRateInMb() {
        return rateInMb;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "numberOfThreads=" + numberOfThreads +
                ", loadRate=" + loadRate +
                ", file='" + file + '\'' +
                ", outputFolder='" + outputFolder + '\'' +
                ", rateInKb=" + rateInKb +
                ", rateInMb=" + rateInMb +
                '}';
    }
}
