package org.open.ev.app.odb2parsers;


public class BatteryTemperaturesParser extends AbstractODB2Parser {

    private static final String SECOND_BLOCK = "7EC22";
    private static final String THIRD_BLOCK = "7EC23";
    private static final String FOURTH_BLOCK = "7EC24";

    public BatteryTemperaturesParser(String odb2Data) {
        super(odb2Data);
    }

    @Override
    public void parse(EVStatus evStatus) throws Exception {
        String extractedSecondBlock = extractData(SECOND_BLOCK, THIRD_BLOCK);
        String extractedThirdBlock = extractData(THIRD_BLOCK, FOURTH_BLOCK);
        double batteryTempMax = parseSigned(extractedSecondBlock.substring(8, 10)); // fifth byte within 2nd block
        double batteryTempMin = parseSigned(extractedSecondBlock.substring(10, 12)); // sixth byte within 2nd block
        double batteryTempInlet = parseSigned(extractedThirdBlock.substring(10, 12)); // sixth byte within 3rd block
        evStatus.setBatteryTempInlet(roundDouble(batteryTempInlet));
        evStatus.setBatteryTempMax(roundDouble(batteryTempMax));
        evStatus.setBatteryTempMin(roundDouble(batteryTempMin));
    }
}
