package com.kayusgold.logonmanager;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;

public class MyISORequestListener implements ISORequestListener {
    @Override
    public boolean process(ISOSource source, ISOMsg m) {
        //log the source and the message
        System.out.println("ISORequestListener Source: " + source.toString());
        System.out.println("ISORequestListener Message: " + m.toString());
        return false;
    }
}
