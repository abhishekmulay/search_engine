package hw2.indexing;

/**
 * Created by Abhishek Mulay on 6/5/17.
 */
public class CatalogEntry {
    private final String term;
    private final int position;
    private final int offset;

    public CatalogEntry(String term, int position, int offset) {
        this.term = term;
        this.position = position;
        this.offset = offset;
    }

    public String getTerm() {
        return term;
    }

    public int getPosition() {
        return position;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "CatalogEntry{term=" + term + ", position=" + position + ", offset=" + offset + '}';
    }
}
