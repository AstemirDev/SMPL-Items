package org.astemir.uniblend.event;

public enum EventExecutionResult {
    PROCEED,CANCEL,NO_RESULT;


    public static EventExecutionResult from(boolean value){
        if (value){
            return PROCEED;
        }else{
            return CANCEL;
        }
    }

    public boolean isCancelled(){
        return this == CANCEL;
    }
}
