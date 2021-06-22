package com.ua.riaw.model;

public enum CDMVersion {
    CDMV4("CDMV4.csv"),
    CDMV5("CDMV5.csv"),
    CDMV501("CDMV5.0.1.csv"),
    CDMV510("CDMV5.1.0.csv"),
    CDMV520("CDMV5.2.0.csv"),
    CDMV530("CDMV5.3.0.csv"),
    CDMV531("CDMV5.3.1.csv"),
    CDMV531_O("CDMV5.3.1_Oncology.csv"),
    CDMV60("CDMV6.0.csv"),
    CDMV60_O("CDMV6.0_Oncology.csv");

    public final String fileName;

    CDMVersion(String fileName) {
        //this.fileName = "src/main/resources/CDM_Versions/" + fileName;
        this.fileName = "CDM_Versions/" + fileName;
    }
}