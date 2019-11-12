package org.open.ev.app.odb2parsers;

public class SocDisplayParser extends AbstractODB2Parser {

    private static final String DATA_BLOCK = "7EC25";
    private static final String END_DATA_BLOCK = "7EC26";

    public SocDisplayParser(String odb2Data) {
        super(odb2Data);
    }

    @Override
    public void parse(EVStatus evStatus) throws Exception {
        String data = extractData(DATA_BLOCK, END_DATA_BLOCK);
        int socDisplay = parseInt(data.substring(0, 2)) / 2;
        evStatus.setStateOfChargeDisplay(socDisplay);
    }
}
