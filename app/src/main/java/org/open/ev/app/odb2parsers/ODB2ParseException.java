package org.open.ev.app.odb2parsers;

public class ODB2ParseException extends Exception {

    public ODB2ParseException(){

    }

    public ODB2ParseException(String message){
        super(message);
    }

    public ODB2ParseException(String message, Throwable throwable){
        super(message, throwable);
    }
}
