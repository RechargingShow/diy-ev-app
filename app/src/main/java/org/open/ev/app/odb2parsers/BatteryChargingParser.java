package org.open.ev.app.odb2parsers;


public class BatteryChargingParser extends AbstractODB2Parser {

    private static final String SECOND_BLOCK = "7EC22";
    private static final String THIRD_BLOCK = "7EC23";
    private static final String SEVENTH_BLOCK = "7EC27";
    private static final String EIGHT_BLOCK = "7EC28";
    private GPSData gpsData;

    public BatteryChargingParser(String odb2Data, GPSData gpsData) {
        super(odb2Data);
        this.gpsData = gpsData;
    }

    @Override
    public void parse(EVStatus evStatus) throws Exception {
        String data = extractData(SECOND_BLOCK, THIRD_BLOCK);
        int dcBatteryVoltage = ((parseInt(data.substring(4, 6)) << 8) + parseInt(data.substring(6, 8))) / 10;
        double chargingRateAmps = parseSigned(data.substring(0, 2) + data.substring(2, 4)) * 0.1;
        double chargeRateKw = dcBatteryVoltage * chargingRateAmps / 1000.0;

        evStatus.setDcBatteryVoltage(dcBatteryVoltage);
        evStatus.setChargeRateAmps(roundDouble(Math.abs(chargingRateAmps)));
        evStatus.setChargeRateVolts(dcBatteryVoltage);
        evStatus.setChargeRateKw(roundDouble(Math.abs(chargeRateKw)));

        if (new Integer(0).equals(gpsData.getSpeed()) && evStatus.getChargeRateKw() > 0.5) {
            evStatus.setCharging(true);
            if (evStatus.getChargeRateKw() > 8.0) {
                evStatus.setFastCharging(true);
            } else {
                double internalToNetVolt = dcBatteryVoltage / 230.0;
                evStatus.setChargeRateVolts(230);
                evStatus.setChargeRateAmps(roundDouble(Math.abs(chargingRateAmps * internalToNetVolt)));
                evStatus.setNormalCharging(true);
            }
        }
    }
}
