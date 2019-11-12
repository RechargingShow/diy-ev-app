package org.open.ev.app.odb2;

import android.util.Log;

import org.open.ev.app.bluetooth.BluetoothWriter;
import org.open.ev.app.network.ODB2PostAsyncTask;

public class ODB2MessageProcessor {

    private static final String LOG_TAG = "ODB2MessageProcessor";
    private static final String ODB2_COMMAND_RESPOSNE_END_CHARACTER = ">";

    public void process(String odb2DataString){
        odb2DataString = odb2DataString.replaceAll("/\\s/g", "");
        odb2DataString = odb2DataString.replaceAll("\\r", "");
        ODB2Data odb2Data = ODB2Data.getInstance();
        odb2Data.addResonseStringToCommandResponse(odb2DataString);

        //The '>' means the end of a response of 1 command
        if (odb2Data.getCurrentCommandResponse().endsWith(ODB2_COMMAND_RESPOSNE_END_CHARACTER)) {
            Log.d(LOG_TAG, "ODB2 data received");
            Log.d(LOG_TAG, String.format("Data: %s", odb2Data.getCurrentCommandResponse()));
            BluetoothWriter bluetoothWriter = BluetoothWriter.getInstance();

            if (odb2Data.getCurrentCommandResponse().contains("ERROR") ||
                    odb2Data.getCurrentCommandResponse().contains("UNABLETOCONNECT") ||
                    odb2Data.getCurrentCommandResponse().contains("BUFFERFULL") ||
                    odb2Data.getCurrentCommandResponse().contains("NO DATA")) {
                Log.d(LOG_TAG, "An odb2Data error detected, disconnecting.");
                odb2Data.sessionEnded();
                //bluetoothWriter.writeMessage(ODB2Data.ODB2_LOW_POWER_MODE_COMMAND);
                bluetoothWriter.writeMessage(ODB2Data.ODB2_TERMINATE_SESSION_COMMAND);
                return;
            }

            //All Init commands are send and received
            if (odb2Data.getSendInitCommands() == ODB2Data.DONGLE_INIT_COMMANDS.length) {
                //If no data command is send yet, send the first one
                if (!odb2Data.isADataCommandSend()) {
                    bluetoothWriter.writeMessage(odb2Data.getDataCommand());
                    return;
                }

                odb2Data.setByteDataForDataCommand();
                if (odb2Data.allDataReceived()) {
                    Log.d(LOG_TAG, "All odb2 data received, posting to server");
                    ODB2PostAsyncTask odb2PostAsyncTask = new ODB2PostAsyncTask();
                    odb2PostAsyncTask.execute(odb2Data);
                    odb2Data.sessionEnded();
                    //bluetoothWriter.writeMessage(ODB2Data.ODB2_LOW_POWER_MODE_COMMAND);
                    bluetoothWriter.writeMessage(ODB2Data.ODB2_TERMINATE_SESSION_COMMAND);
                } else {
                    bluetoothWriter.writeMessage(odb2Data.getDataCommand());
                }
            } else {
                //We are not done intializing yet, send next initialize command
                bluetoothWriter.writeMessage(odb2Data.getNextInitCommand());
            }
        }
    }
}
