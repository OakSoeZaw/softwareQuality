package Task1;

import java.util.ArrayList;
import Task1.HashTable;

public class Tfidf {
    private ArrayList<HashTable> allDoc;


    public Tfidf(ArrayList<HashTable> allDoc){
        this.allDoc = allDoc;
    }
    public double calculateIDF(String term){
        int count = 0;
        for(HashTable cod : allDoc){
            // fix the idf use global
            if (cod.contains(term)){
                count ++;
            }
        }
        return Math.log((float) allDoc.size() / (count+1));
    }

    public double calculateTF(String term,HashTable doc){
        int termCount = doc.getCount(term);
        int TotalCount = doc.getTotalWord();
        return (float) termCount/TotalCount;
    }
    public HashTable calculateTfidf(HashTable doc){
        HashTable result = new HashTable();
        for (String word : doc.getWords()){
            double tf = calculateTF(word,doc);
            double idf = calculateIDF(word);
            result.add(word,tf*idf);
        }
        return result;
    }
    double cosineSimilarity(HashTable doc1, HashTable doc2){
        HashTable doc1Result = calculateTfidf(doc1);
        HashTable doc2Result = calculateTfidf(doc2);
        ArrayList<String> doc1Words = doc1.getWords();
        ArrayList<String> doc2Words = doc2.getWords();
        HashTable commonDoc = new HashTable();
        for (String word : doc1Words){
            commonDoc.add(word,null);
        }
        for (String word: doc2Words){
            commonDoc.add(word,null);
        }
        double dotProduct = 0.0, mag1 = 0.0, mag2 = 0.0;
        ArrayList<String> UnionWords = commonDoc.getWords();
        for(String word : UnionWords){
            double val1 = doc1Result.getValue(word); //tfidf score
            double val2 = doc2Result.getValue(word);

            dotProduct += val1 * val2;
            mag1 += val1 * val1;
            mag2 += val2 * val2;
        }
//        System.out.println("dotProduct: " + dotProduct + ", mag1: " + mag1 + ", mag2: " + mag2);
        if (mag1 == 0 || mag2 == 0) return 0.0;
        return dotProduct / (Math.sqrt(mag1)* Math.sqrt(mag2));
    }

    double cosineSimilarity2(SiteData sD1, SiteData sD2){
        HashTable idfSD1 =sD1.getTfidf();
        HashTable idfSD2 = sD2.getTfidf();
        ArrayList<String> wordSD1 = sD1.getDoc().getWords();
        ArrayList<String> wordSD2 = sD2.getDoc().getWords();
        HashTable Union = new HashTable();
        for(String i : wordSD1){
            Union.add(i,null);
        }
        for(String j : wordSD2){
            Union.add(j,null);
        }
        double dotProduct = 0.0 ,mag1 = 0.0, mag2 = 0.0;
        ArrayList<String> UnionWords = Union.getWords();
        for(String item: UnionWords){
            double val1 = idfSD1.getValue(item);
            double val2 = idfSD2.getValue(item);

            dotProduct += val1 * val2;
            mag1 += val1 *val1;
            mag2 += val2 * val2;
        }

        if( mag1 ==0 || mag2 == 0) return 0.0;
        return dotProduct / (Math.sqrt(mag1)* Math.sqrt(mag2));
    }


}
