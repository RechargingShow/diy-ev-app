package org.open.ev.app.odb2parsers;


public class AuxBatteryVoltageParser extends AbstractODB2Parser {

    private static final String FOURTH_BLOCK = "7EC24";
    private static final String FIFTH_BLOCK = "7EC25";

    public AuxBatteryVoltageParser(String odb2Data) {
        super(odb2Data);
    }

    @Override
    public void parse(EVStatus evStatus) throws Exception {
        String data = extractData(FOURTH_BLOCK, FIFTH_BLOCK);
        int auxBatteryVoltage = parseInt(data.substring(10, 12)) / 10;
        evStatus.setAuxBatteryVoltage(auxBatteryVoltage);
    }
}

