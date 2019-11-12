package org.open.ev.app.odb2;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * http://www.obdtester.com/elm-usb-commands
 * Meaning of the commands
 * ATD: Set defaults
 * ATZ: Reset
 * ATEn: Echo
 * ATLn: Line feed enabled/disabled
 * ATSn: ?
 * ATHn: Set display header
 * ATSTFF: ?
 * ATFE: ?
 * ATSPn: Set desired communication protocol
 * ATCRA7EC: "sets the receive filter for the scanner to ignore replies from modules other than the BECM"
 * ATLP: Low power mode
 * ATPC: Terminate session
 */
public class ODB2Data {

    private static final String SOC_SOH_COMMAND = "220105";
    private static final String OTHER_DATA_COMMAND = "220101";
    static final String[] DONGLE_INIT_COMMANDS
            = new String[]{"ATD", "ATZ", "ATE0", "ATL0", "ATS0", "ATH1", "ATSTFF", "ATFE", "ATSP6", "ATCRA7EC"};
    static final String ODB2_LOW_POWER_MODE_COMMAND = "ATLP\r";
    static final String ODB2_TERMINATE_SESSION_COMMAND = "ATPC\r";

    private static ODB2Data instance;
    private int sendInitCommands;
    private String currentCommandResponse;
    private String currentDataCommand;
    private String socSohResult;
    private String otherDataResult;
    private boolean sessionEnded;

    private ODB2Data() {
        sendInitCommands = 0;
        socSohResult = "";
        otherDataResult = "";
        currentDataCommand = null;
        currentCommandResponse = "";
        sessionEnded = false;
    }

    public String getNextInitCommand() {
        String odb2Command = DONGLE_INIT_COMMANDS[sendInitCommands];
        sendInitCommands++;
        return odb2Command;
    }

    int getSendInitCommands() {
        return sendInitCommands;
    }

    boolean allDataReceived() {
        return socSohResult.length() > 0 && otherDataResult.length() > 0;
    }

    public String getDataCommand() {
        if (currentDataCommand == null) {
            currentDataCommand = SOC_SOH_COMMAND;
            return SOC_SOH_COMMAND;
        }
        currentDataCommand = OTHER_DATA_COMMAND;
        return OTHER_DATA_COMMAND;
    }

    boolean isADataCommandSend(){
        return currentDataCommand != null;
    }

    public void setByteDataForDataCommand() {
        if (SOC_SOH_COMMAND.equals(currentDataCommand)) {
            socSohResult = currentCommandResponse;
        } else {
            otherDataResult = currentCommandResponse;
        }
    }

    String getCurrentCommandResponse() {
        return currentCommandResponse;
    }

    public void addResonseStringToCommandResponse(String response){
        this.currentCommandResponse += response;
    }

    public void resetCommandResponse(){
        this.currentCommandResponse = "";
    }

    public void sessionEnded(){
        this.sessionEnded = true;
    }

    public boolean isSessionEnded() {
        return sessionEnded;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("socSoh", socSohResult);
        jsonObject.put("otherData", otherDataResult);
        return jsonObject;
    }

    public static ODB2Data getInstance() {
        if (instance == null) {
            instance = new ODB2Data();
        }
        return instance;
    }

    public static void reset() {
        instance = new ODB2Data();
    }
}
