package Task1;

import java.util.*;
import java.io.*;

public class HashTable implements java.io.Serializable {
    static final class Node implements java.io.Serializable{
        Object key;
        Node next;
        int count = 1;
        Object value;
        Node(Object k, Node n) {key = k; next = n;}
        Node(Object k,Object v, Node n) {key = k; value=v; next = n;}
    }
    Node [] table = new Node[8];
    int size = 0;
    boolean contains(Object key){
        int h = key.hashCode();
        int i = h & (table.length-1);
        for (Node e = table[i]; e != null; e= e.next){
            if (key.equals(e.key)){
                return true;
            }
        }
        return false;
    }
    void add(Object key){
        int h = key.hashCode();
        int i = h & (table.length-1);
        for (Node e = table[i]; e != null; e = e.next){
            if (key.equals(e.key)){
                e.count = e.count +1;
                return;
            }
        }
        table[i] = new Node(key,table[i]);

        ++size;
        if ((float)size/ table.length >= 0.75f){
            resizeV2();
        }
    }
    void add(Object key, Object value){
        int h = key.hashCode();
        int i = h & (table.length-1);
        for (Node e = table[i]; e!=null; e=e.next){
            if(key.equals(e.key)){
                return;
            }
        }
        table[i] = new Node(key,value,table[i]);
        ++size;
        if ((float)size/ table.length >= 0.75f){
            resizeV2();
        }
    }

    void resizeV2(){
        Node [] oldTable = table;
        int oldCapacity = oldTable.length;
        int newCapactiy = oldCapacity << 1;
        Node [] newTable = new Node[newCapactiy];
        for (int i = 0; i < oldCapacity; ++i){
            Node e = oldTable[i];
            while (e != null){
                Node next = e.next;
                int h = e.key.hashCode();
                int j = h & (newTable.length-1);
                e.next = newTable[j];
                newTable[j] = e;
                e = next;
            }
        }
        table = newTable;
    }

    void remove(Object key){
        int h = key.hashCode();
        int i = h & (table.length -1);
        Node e = table[i], p =null;
        while (e != null){
            if ( key.equals( e.key)){
                if (p == null){
                    table[i] = e.next;
                }else{
                    p.next = e.next;
                }
                break;
            }
            p = e;
            e = e.next;
        }
    }
    int getCount(Object key){
        int h = key.hashCode();
        int i = h & (table.length-1);
        for (Node e = table[i]; e != null; e = e.next){
            if (key.equals(e.key)){
                return e.count;
            }
        }
        return 0;
    }

    int getTotalWord(){
        int total = 0;
        for (int i = 0; i< table.length; ++i){
            for (Node e = table[i]; e!=null; e= e.next){
                total += e.count;
            }
        }
        return total;
    }

    void printAll(){
        for(int i = 0; i< table.length; ++i){
            for(Node e = table[i]; e != null; e= e.next){
                System.out.println(e.key);
                System.out.println(e.value);
            }
        }
    }
    ArrayList<String> getWords(){
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < table.length; ++i){
            for (Node e = table[i]; e != null; e = e.next){
                result.add(e.key.toString());
            }
        }
        return result;
    }
    double getValue(Object key){
        int h = key.hashCode();
        int i = h & (table.length-1);
        for (Node e = table[i]; e != null; e = e.next){
            if (key.equals(e.key)){
                return (double) e.value;
            }
        }
        return 0.0;
    }
    boolean set(Object key, Object value){
        int h = key.hashCode();
        int i = h & (table.length-1);
        for (Node e = table[i]; e != null; e = e.next){
            if (key.equals(e.key)){
                e.value = value;
                return true;
            }
        }
        return false;
    }


    private void writeObject(ObjectOutputStream s) throws Exception{
        s.defaultWriteObject();
        s.writeInt(size);
        for (int i = 0; i < table.length; ++i){
            for (Node e = table[i]; e != null; e = e.next){
                s.writeObject(e.key);
            }
        }
    }
    private void readObject(ObjectInputStream s) throws Exception{
        s.defaultReadObject();
        int n = s.readInt();
        for (int i = 0; i < n ; ++i){
            add(s.readObject());
        }
    }
}
