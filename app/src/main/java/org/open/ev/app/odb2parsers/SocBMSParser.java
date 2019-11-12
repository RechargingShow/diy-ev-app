package org.open.ev.app.odb2parsers;

public class SocBMSParser extends AbstractODB2Parser {

    private static final String DATA_BLOCK = "7EC21";
    private static final String END_DATA_BLOCK = "7EC22";

    public SocBMSParser(String odb2Data) {
        super(odb2Data);
    }

    @Override
    public void parse(EVStatus evStatus) throws Exception {
        String data = extractData(DATA_BLOCK, END_DATA_BLOCK);
        int socBMS = parseInt(data.substring(2, 4)) / 2;
        evStatus.setStateOfChargeBMS(socBMS);
    }
}
