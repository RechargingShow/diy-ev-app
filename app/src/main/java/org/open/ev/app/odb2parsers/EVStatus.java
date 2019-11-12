package org.open.ev.app.odb2parsers;

import java.time.LocalDateTime;

public class EVStatus {

    private boolean charging;
    private boolean fastCharging;
    private boolean normalCharging;
    private Integer stateOfChargeDisplay;
    private Integer stateOfChargeBMS;
    private Integer batteryStateOfHealth;
    private Integer dcBatteryVoltage;
    private Integer auxBatteryVoltage;
    private Integer estimatedRange;
    private Double chargeRateKw;
    private Double chargeRateAmps;
    private Integer chargeRateVolts;
    private Double batteryTempMax;
    private Double batteryTempMin;
    private Double batteryTempInlet;
    private LocalDateTime lastUpdate;

    void reset(){
        charging = false;
        fastCharging = false;
        normalCharging = false;
        stateOfChargeDisplay = null;
        stateOfChargeBMS = null;
        batteryStateOfHealth = null;
        dcBatteryVoltage = null;
        auxBatteryVoltage = null;
        estimatedRange = null;
        chargeRateKw = null;
        chargeRateAmps = null;
        chargeRateVolts = null;
        batteryTempMax = null;
        batteryTempMin = null;
        batteryTempInlet = null;
        lastUpdate = null;
    }

    public boolean isCharging() {
        return charging;
    }

    public void setCharging(boolean charging) {
        this.charging = charging;
    }

    public boolean isFastCharging() {
        return fastCharging;
    }

    public void setFastCharging(boolean fastCharging) {
        this.fastCharging = fastCharging;
    }

    public boolean isNormalCharging() {
        return normalCharging;
    }

    public void setNormalCharging(boolean normalCharging) {
        this.normalCharging = normalCharging;
    }

    public Integer getStateOfChargeDisplay() {
        return stateOfChargeDisplay;
    }

    public void setStateOfChargeDisplay(Integer stateOfChargeDisplay) {
        this.stateOfChargeDisplay = stateOfChargeDisplay;
    }

    public Integer getStateOfChargeBMS() {
        return stateOfChargeBMS;
    }

    public void setStateOfChargeBMS(Integer stateOfChargeBMS) {
        this.stateOfChargeBMS = stateOfChargeBMS;
    }

    public Integer getBatteryStateOfHealth() {
        return batteryStateOfHealth;
    }

    public void setBatteryStateOfHealth(Integer batteryStateOfHealth) {
        this.batteryStateOfHealth = batteryStateOfHealth;
    }

    public Integer getDcBatteryVoltage() {
        return dcBatteryVoltage;
    }

    public void setDcBatteryVoltage(Integer dcBatteryVoltage) {
        this.dcBatteryVoltage = dcBatteryVoltage;
    }

    public Integer getAuxBatteryVoltage() {
        return auxBatteryVoltage;
    }

    public void setAuxBatteryVoltage(Integer auxBatteryVoltage) {
        this.auxBatteryVoltage = auxBatteryVoltage;
    }

    public Integer getEstimatedRange() {
        return estimatedRange;
    }

    public void setEstimatedRange(Integer estimatedRange) {
        this.estimatedRange = estimatedRange;
    }

    public Double getChargeRateKw() {
        return chargeRateKw;
    }

    public void setChargeRateKw(Double chargeRateKw) {
        this.chargeRateKw = chargeRateKw;
    }

    public Double getChargeRateAmps() {
        return chargeRateAmps;
    }

    public void setChargeRateAmps(Double chargeRateAmps) {
        this.chargeRateAmps = chargeRateAmps;
    }

    public Integer getChargeRateVolts() {
        return chargeRateVolts;
    }

    public void setChargeRateVolts(Integer chargeRateVolts) {
        this.chargeRateVolts = chargeRateVolts;
    }

    public Double getBatteryTempMax() {
        return batteryTempMax;
    }

    public void setBatteryTempMax(Double batteryTempMax) {
        this.batteryTempMax = batteryTempMax;
    }

    public Double getBatteryTempMin() {
        return batteryTempMin;
    }

    public void setBatteryTempMin(Double batteryTempMin) {
        this.batteryTempMin = batteryTempMin;
    }

    public Double getBatteryTempInlet() {
        return batteryTempInlet;
    }

    public void setBatteryTempInlet(Double batteryTempInlet) {
        this.batteryTempInlet = batteryTempInlet;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "EVStatus{" +
                "charging=" + charging +
                ", fastCharging=" + fastCharging +
                ", normalCharging=" + normalCharging +
                ", stateOfChargeDisplay=" + stateOfChargeDisplay +
                ", stateOfChargeBMS=" + stateOfChargeBMS +
                ", batteryStateOfHealth=" + batteryStateOfHealth +
                ", dcBatteryVoltage=" + dcBatteryVoltage +
                ", auxBatteryVoltage=" + auxBatteryVoltage +
                ", estimatedRange=" + estimatedRange +
                ", chargeRateKw=" + chargeRateKw +
                ", chargeRateAmps=" + chargeRateAmps +
                ", chargeRateVolts=" + chargeRateVolts +
                ", batteryTempMax=" + batteryTempMax +
                ", batteryTempMin=" + batteryTempMin +
                ", batteryTempInlet=" + batteryTempInlet +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
