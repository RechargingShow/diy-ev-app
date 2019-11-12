package org.open.ev.app.odb2parsers;

public class SohParser extends AbstractODB2Parser {

    private static final String DATA_BLOCK = "7EC24";
    private static final String END_DATA_BLOCK = "7EC25";

    public SohParser(String odb2Data) {
        super(odb2Data);
    }

    @Override
    public void parse(EVStatus evStatus) throws Exception {
        String data = extractData(DATA_BLOCK, END_DATA_BLOCK);
        int soh = ((parseInt(data.substring(2, 4)) << 8) + parseInt(data.substring(4, 6))) / 10;
        evStatus.setBatteryStateOfHealth(soh);
    }

}
