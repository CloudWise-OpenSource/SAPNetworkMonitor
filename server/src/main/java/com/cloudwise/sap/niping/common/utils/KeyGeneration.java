package com.cloudwise.sap.niping.common.utils;

public class KeyGeneration {

    private KeyGeneration() {}

    public static String getKey() {return ShortUUID.generate();}
}