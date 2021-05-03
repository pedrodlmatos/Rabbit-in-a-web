package com.ua.hiah.model;

public enum StemTableFile {

    CDMV501("StemTableV5.0.1.csv", "StemTableDefaultMappingV5.0.1.csv"),
    CDMV510("StemTableV5.1.0.csv", "StemTableDefaultMappingV5.1.0.csv"),
    CDMV520("StemTableV5.2.0.csv", "StemTableDefaultMappingV5.2.0.csv"),
    CDMV530("StemTableV5.3.0.csv", "StemTableDefaultMappingV5.3.0.csv"),
    CDMV531("StemTableV5.3.1.csv", "StemTableDefaultMappingV5.3.1.csv"),
    CDMV531_O("StemTableV5.3.1.csv", "StemTableDefaultMappingV5.3.1.csv"),
    CDMV60("StemTableV6.0.csv", "StemTableDefaultMappingV6.0.csv"),
    CDMV60_O("StemTableV6.0.csv", "StemTableDefaultMappingV6.0.csv");

    public final String fileName;
    public final String defaultMappings;

    StemTableFile(String fileName, String defaultMappings) {
        //this.fileName = "src/main/resources/CDM_Versions/" + fileName;
        this.fileName = "Stem_Tables/" + fileName;
        this.defaultMappings = "Stem_Tables/" + defaultMappings;
    }

}
