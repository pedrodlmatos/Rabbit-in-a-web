package com.ua.hiah.utilities.mapping;

import java.io.Serializable;

public interface MappableItem extends Serializable {
    public String outputName();
    public String getName();
    //public Database getDb();
    public boolean isStem();
    public void setStem(boolean isStem);
}
