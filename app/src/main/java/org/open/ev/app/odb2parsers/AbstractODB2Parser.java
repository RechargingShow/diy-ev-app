package org.open.ev.app.odb2parsers;

import java.text.DecimalFormat;


public abstract class AbstractODB2Parser {

    String odb2Data;

    public AbstractODB2Parser(String odb2Data) {
        this.odb2Data = odb2Data;
    }

    public abstract void parse(EVStatus evStatus) throws Exception;

    int parseInt(String byteString) {
        return Integer.parseInt(byteString, 16);
    }

    double parseSigned(String byteString) {
        int bits = byteString.length() * 4;
        return ((parseInt(byteString) + Math.pow(2, bits - 1)) % Math.pow(2, bits)) - Math.pow(2, bits - 1);
    }

    String extractData(String fromBlock, String toBlock) {
        String data = odb2Data.substring(odb2Data.indexOf(fromBlock), odb2Data.indexOf(toBlock)).trim();
        return data.replace(fromBlock, "");
    }

    double roundDouble(double value){
        try {
            DecimalFormat df = new DecimalFormat("#.#");
            return Double.valueOf(df.format(value));
        } catch (NumberFormatException exception) {
            DecimalFormat df = new DecimalFormat("#,#");
            return Double.valueOf(df.format(value));
        }
    }
}
