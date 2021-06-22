/* Adapted from MappableItem (rabbit-core) */
package rabbitinahat.model;

public interface MappableItem {

    public String outputName();
    public String getName();
    public Database getDb();
    public boolean isStem();
    public void setStem(boolean isStem);
}
