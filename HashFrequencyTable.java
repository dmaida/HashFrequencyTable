
import javax.annotation.processing.SupportedSourceVersion;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Iterable;

public class HashFrequencyTable<K> implements FrequencyTable<K>, Iterable<K> {

  private class Entry {
    public K key;
    public int count;

    public Entry(K k) {
      key = k;
      count = 1;
    }
  }

  private ArrayList<Entry> table;
  private float maxLoadFactor;
  private int numEntries;
  private int tablesize;

  private int a = 0;

  public void click(K key) {
      insert(key);
  }

  public int count(K key) {
    int i = searchAux(key);
    if (table.get(i) == null) {
      return 0;
    }

    return table.get(i).count;

  }

  public HashFrequencyTable(int initialCapacity, float maxLoadFactor) {
    this.maxLoadFactor = maxLoadFactor;
    int sz = nextPowerOfTwo(initialCapacity);
    tablesize = sz;

    table = new ArrayList<>(sz);
    for (int i = 0; i < sz; i++) {
      table.add(null);
    }
    numEntries = 0;

  }

  private int searchAux(K key) {
    int N = tablesize;
    int i = 0;

    int h = key.hashCode() & (N - 1);
    //System.out.println("key= " + key + "h= " + h);

    Entry e;
    while ((e = table.get(h)) != null) {
      if (key.equals(e.key)) {
        return h;  // hit
      }
      i++;
      h = (h + i * (i + 1) / 2) & (N - 1);
    }
    //System.out.println(h);
    return h; // miss
  }

  public void insert(K key) {
    int index = searchAux(key);
    Entry e = table.get(index);
    if (e == null) {
      e = new Entry(key);
      table.set(index, e);
      numEntries++;

      if (loadFactor() >= maxLoadFactor) {
        doubleSizeAndRehash();
      }
    } else { // duplicate key
      e.count++;
      e.key = key;
    }
  }

  private void doubleSizeAndRehash() {
    ArrayList<Entry> oldTable = table;
    int oldTableSize = tablesize;
    int M = 2 * tablesize;
    table = new ArrayList<>(M);
    for (int i = 0; i < M; i++) {
      table.add(null);
    }
    numEntries = 0;
    tablesize = M;
    for (int i = 0; i < oldTableSize; i++) {
      Entry e = oldTable.get(i);
      if (e != null) {
        insert(e.key);
      }
    }
  }

  private boolean isEmpty() {
    return numEntries == 0;
  }

  private float loadFactor() {
    return (float) numEntries / tablesize;
  }

  private static int nextPowerOfTwo(int n) {
    int e = 1;
    while ((1 << e) < n) {
      e++;
    }
    return 1 << e;
  }

  private void dump() {
    for (int i = 0; i < tablesize; i++) {
      if (table.get(i) == null) {
        System.out.println(i + ":  " + null);
      } else {
        System.out.println(i + ":  " + "key=" + table.get(i).key + " count=" + table.get(i).count);
      }
    }
  }

  public Iterator<K> iterator() {
    return new TableIterator();
  }

  private class TableIterator implements Iterator<K> {
    private int i;

    public TableIterator() {
      i = 0;
    }

    public boolean hasNext() {
      while (i < table.size() && table.get(i) == null)
        i++;
      return i < table.size();
    }

    public K next() {
      return table.get(i++).key;
    }

    public void remove() {
      throw new UnsupportedOperationException("Remove not supported");
    }
  }

  public static void main(String[] args) {

    HashFrequencyTable<String> symtab = new HashFrequencyTable<String>(4, 0.95F);
    symtab.insert("what");
    symtab.insert("Mac");
    symtab.insert("Cheese");
    symtab.insert("Tomato");
    symtab.insert("Cheese");
    symtab.insert("Tomato");
    symtab.insert("Mac");

    symtab.dump();
  }
}
