package Task1;

import java.io.*;
import java.util.ArrayList;

public class ClusterLoader {
    static ArrayList<String> UrlList = Main.getUrlScrape("src/cache/data.csv");
    public static ArrayList<HashTable> allDoc;

    public ClusterLoader() throws IOException {
        allDoc = Main.getTextMap(UrlList);
    }


    public static void main(String[] args) throws IOException{
        ClusterLoader cl = new ClusterLoader();
        Tfidf tfidf = new Tfidf(cl.allDoc);
        int i = 0;
        while(i < UrlList.size()){
            String url = UrlList.get(i);
            HashTable doc = cl.allDoc.get(i);
            HashTable Tfidf = tfidf.calculateTfidf(doc);
            SiteData sData = new SiteData(url,Tfidf,doc,-1);
            createPersistentFile(sData);
            i++;
        }
    }

    private static void createPersistentFile(SiteData siteData) {
        try(FileOutputStream fileOut = new FileOutputStream("src/cache/persistent/"+siteData.getUrl()+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(siteData);
            System.out.println("successful serialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SiteData readSiteData(File file){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            return (SiteData) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error reading " + file + ": " + e.getMessage());
            return null;
        }
    }
}
