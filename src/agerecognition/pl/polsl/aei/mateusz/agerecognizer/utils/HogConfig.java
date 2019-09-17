package pl.polsl.aei.mateusz.agerecognizer.utils;

public class HogConfig {
    private boolean isHog;
    private double cellSizeInPx = 3;
    private int nbins = 9;

    public HogConfig(boolean isHog) {
        this.isHog = isHog;
    }

    @Override
    public String toString() {
        return "HogConfig{" +
                "isHog=" + isHog +
                ", cellSizeInPx=" + cellSizeInPx +
                ", nbins=" + nbins +
                '}';
    }

    public HogConfig(boolean isHog, double cellSizeInPx, int nbins) {
        this.isHog = isHog;
        this.cellSizeInPx = cellSizeInPx;
        this.nbins = nbins;
    }

    public boolean isHog() {
        return isHog;
    }

    public void setHog(boolean hog) {
        isHog = hog;
    }

    public double getCellSizeInPx() {
        return cellSizeInPx;
    }

    public void setCellSizeInPx(double cellSizeInPx) {
        this.cellSizeInPx = cellSizeInPx;
    }

    public int getNbins() {
        return nbins;
    }

    public void setNbins(int nbins) {
        this.nbins = nbins;
    }
}
