package com.onionnetworks.util;

import java.util.EventListener;

/**
 * An interface for an Exception handler.
 *
 * @author Justin F. Chapweske
 */
public interface ExceptionHandler extends EventListener {

    String HANDLE_EXCEPTION = "handleException";
    
    String[] EVENTS = new String[] { HANDLE_EXCEPTION };

    void handleException(ExceptionEvent ev);
}
