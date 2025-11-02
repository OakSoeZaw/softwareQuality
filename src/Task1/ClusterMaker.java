package Task1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class ClusterMaker {

    public static void main(String [] args) throws IOException{
        ArrayList<SiteData> siteDatas = new ArrayList<>();
        File folder = new File("src/cache/persistent/");

        File[] files = folder.listFiles();
        if(files!= null){
            for(File file : files){
                if (file.isHidden() || file.getName().startsWith(".")) {
                    continue;
                }
                if(file.isFile()){
                    siteDatas.add(ClusterLoader.readSiteData(file));
                }
            }
        }else{
            System.out.println("No files found in "+ folder.getAbsolutePath());
        }
        ArrayList<SiteData> centroids = new ArrayList<>();
        ArrayList<ArrayList<SiteData>> clusterBag = new ArrayList<>();
        int iteration = 0;
        boolean converged = false;
        int Max_Iteration = 50;
        while(!converged && iteration< Max_Iteration){
            clusterBag = makingCluster(5,siteDatas,300,centroids);
            ArrayList<SiteData> newCentroids = recalculateCluster(clusterBag);

            converged = true;
            if (centroids.size() != newCentroids.size()){
                converged = false;
            }else{
                for(int i = 0; i < centroids.size();i++){
                    if(!centroids.get(i).equals(newCentroids.get(i))){
                        converged = false;
                        break;
                    }
                }
            }
            centroids = newCentroids;
            iteration++;
        }
        createPersistentFile(centroids,"Centroid");
        createPersistentFile(clusterBag,"ClusterBag");
        System.out.println("done in "+ iteration);

    }

    private static ArrayList<SiteData> recalculateCluster(ArrayList<ArrayList<SiteData>> cluster) {
        ArrayList<SiteData> result = new ArrayList<>();
        int count = 0;
        for(ArrayList<SiteData> bag: cluster){
            int n = bag.size();
            double norm = 0;
            HashTable meanVector = new HashTable();
            for (SiteData sd : bag){
                HashTable docMap = sd.getTfidf();
                for(String i : docMap.getWords()){
                    meanVector.add(i,meanVector.getValue(i)+docMap.getValue(i));
                }
            }
            for(String j: meanVector.getWords()){
                meanVector.set(j,(meanVector.getValue(j)/n ));
                norm += meanVector.getValue(j) * meanVector.getValue(j);
            }
            //L2Normalization
            norm = Math.sqrt(norm);
            for(String k : meanVector.getWords()){
                meanVector.set(k,(meanVector.getValue(k)/norm));
            }
            result.add(new SiteData("",meanVector,meanVector,count));
            count ++;
        }
        return result;
    }

    public static ArrayList<ArrayList<SiteData>> makingCluster(int nCluster, ArrayList<SiteData> sData, int nMaxIterations,
                                                               ArrayList<SiteData> centroids) throws IOException {
        ArrayList<ArrayList<SiteData>> resultClusterBag = new ArrayList<>();
        ArrayList<SiteData> resultCentroids = new ArrayList<>(centroids);
        ClusterLoader cl = new ClusterLoader();
        Tfidf tfidf = new Tfidf(ClusterLoader.allDoc);
        if (centroids.isEmpty()) {
            Random rm = new Random();
            for (int i =0 ; i < nCluster; i++){
                int randomIndex = rm.nextInt(sData.size());
                SiteData placeHolder = sData.get(randomIndex);
                placeHolder.setClusterId(i);
                ArrayList<SiteData> centricCluster = new ArrayList<>();
                centricCluster.add(placeHolder);
                resultClusterBag.add(centricCluster);
                resultCentroids.add(placeHolder);
            }
        }
        for (SiteData i : centroids){
            resultClusterBag.add(new ArrayList<SiteData>());
        }
        for (SiteData i : sData){
            SiteData bestCentroid = null;
            double maxScore = -1;
            for(SiteData j : resultCentroids){
                double score = tfidf.cosineSimilarity2(i,j);
                if(score > maxScore){
                    maxScore = score;
                    bestCentroid = j;
                }
            }
            int index = resultCentroids.indexOf(bestCentroid);
            resultClusterBag.get(index).add(i);
            i.setClusterId(index);
        }

        System.out.println("success");
        return resultClusterBag;
        // recalculate the mean centriod of the cluster
    }
    private static void createPersistentFile(Object siteData,String fileName) {
        try(FileOutputStream fileOut = new FileOutputStream("src/cache/"+fileName+".ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(siteData);
            System.out.println("successful serialized");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
