package Task1;

import java.io.Serializable;

public class SiteData implements Serializable {
    private String url;
    private HashTable tfidf;
    private HashTable doc;
    private int clusterId;

    public SiteData(String url, HashTable tfidf, HashTable doc, int clusterId){
        this.url = url;
        this.tfidf = tfidf;
        this.doc = doc;
        this.clusterId = clusterId;
    }
    //methods to get things
    public String getUrl(){
        return url.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }
    public HashTable getTfidf(){
        return this.tfidf;
    }
    public HashTable getDoc(){
        return this.doc;
    }
    public int getClusterId(){
        return this.clusterId;
    }
    public void setClusterId(int id){
        this.clusterId = id;
    }
}
