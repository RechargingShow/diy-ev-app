package org.open.ev.app.odb2parsers;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class ODB2Parser {

    public void parse(ODB2Data odb2Data, EVStatus evStatus, GPSData gpsData) throws ODB2ParseException {
        if (odb2Data == null){
            throw new ODB2ParseException("ODB2 data is null");
        }

        try{
            for(AbstractODB2Parser parser : getSocSohParsers(odb2Data.socSoh)){
                parser.parse(evStatus);
            }
            for(AbstractODB2Parser parser : getOtherDataParsers(odb2Data.otherData, gpsData)){
                parser.parse(evStatus);
            }
        } catch (Exception exception) {
            throw new ODB2ParseException("Exception during the parsing of the ODB2 data", exception);
        }
    }

    private List<AbstractODB2Parser> getSocSohParsers(String odb2Data){
        return Arrays.asList(
                new SocDisplayParser(odb2Data),
                new SohParser(odb2Data)
        );
    }

    private List<AbstractODB2Parser> getOtherDataParsers(String odb2Data, GPSData gpsData){
        return Arrays.asList(
                new SocBMSParser(odb2Data),
                new BatteryTemperaturesParser(odb2Data),
                new BatteryChargingParser(odb2Data, gpsData),
                new AuxBatteryVoltageParser(odb2Data)
        );
    }
}
