package main.sdl;

public class Settings {
    private double lightsourceFactor = 0.1; // or a bit of contrast
    private double epsilon = 0.3; // the difference that will be subtracted for shadowing
    private int maxRecurseLevel = 5; // TODO: move to SDL parameter
    private double dw = 0.1; // width lightbeam coming from source
    private boolean shadowsEnabled = true;
    private boolean reflection = true;
    private boolean refraction = true;

    public Settings() {
    }

    public double getLightsourceFactor() {
        return lightsourceFactor;
    }

    public void setLightsourceFactor(double lightsourceFactor) {
        this.lightsourceFactor = lightsourceFactor;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public int getMaxRecurseLevel() {
        return maxRecurseLevel;
    }

    public void setMaxRecurseLevel(int maxRecurseLevel) {
        this.maxRecurseLevel = maxRecurseLevel;
    }

    public double getDw() {
        return dw;
    }

    public void setDw(double dw) {
        this.dw = dw;
    }

    public boolean isShadowsEnabled() {
        return shadowsEnabled;
    }

    public void setShadowsEnabled(boolean shadowsEnabled) {
        this.shadowsEnabled = shadowsEnabled;
    }

    public boolean isReflection() {
        return reflection;
    }

    public void setReflection(boolean reflection) {
        this.reflection = reflection;
    }

    public boolean isRefraction() {
        return refraction;
    }

    public void setRefraction(boolean refraction) {
        this.refraction = refraction;
    }
}
