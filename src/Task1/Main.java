package Task1;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.*;

public class Main {

    public static HashTable globalBag = new HashTable();
    public static int globalCount = 0;

    public static ArrayList<String> getUrlScrape(String filePath){
        BufferedReader reader = null;
        String line;
        ArrayList<String> UrlStore = new ArrayList<>();
        try{
            reader = new BufferedReader(new FileReader(filePath));
            while((line = reader.readLine()) != null){
                UrlStore.add(line);
            }
        }catch(Exception e){
            System.out.println("Error found "+ e.getMessage());
        }
        return UrlStore;
    }

    public static ArrayList<String> cacheProcess(ArrayList<String> URLlist) throws IOException {
        ArrayList<String> Result = new ArrayList<>();
        for(String url : URLlist){
            Document doc = Jsoup.connect(url).get();
            Result.add(doc.select("p").text());
        }
        return Result;
    }
    public static ArrayList<HashTable> getTextMap(ArrayList<String> UrlList) throws IOException {
        ArrayList<HashTable> result = new ArrayList<>();
        Set<String> stopWords = Set.of(
                "a","an","the","and","or","but","is","am","are","was","were",
                "be","been","being","do","did","does","has","have","had",
                "will","would","can","could","shall","should","may","might","must",
                "i","you","he","she","it","we","they","me","him","her","us","them",
                "in","on","at","by","for","with","about","of","to","this","that"
        );

        File savedDoc = new File("src/cache/savedDoc");
        if (!(savedDoc.exists())){
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/cache/savedDoc"));
            ArrayList<String> TextNeeded = cacheProcess(UrlList);
            for (String text : TextNeeded){
                String uniqueWords = Arrays.stream(text.toLowerCase().split("\\W+"))
                        .filter(w ->(!stopWords.contains(w) && !w.isBlank()))
                        .collect(Collectors.joining(" "));
                String cleanedText = uniqueWords.replaceAll("\\d+", " ");
                try{
                    bw.write(cleanedText); // saving to file
                    bw.newLine();
                    HashTable freqTable = new HashTable();
                    String [] singleWords = cleanedText.split(" ");
                    for (String i : singleWords){
                        freqTable.add(i);
                        globalBag.add(i);
                        globalCount++;
                    }
                    result.add(freqTable);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            BufferedReader br = new BufferedReader(new FileReader(savedDoc));
            String line;
            while((line = br.readLine())!= null){
                HashTable freqTable = new HashTable();
                String[] singleWords = line.split(" ");
                for(String i : singleWords){
                    freqTable.add(i);
                    globalBag.add(i);
                    globalCount++;
                }
                result.add(freqTable);
            }
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        getInput();
    }

    private static void getInput() throws IOException {
        ArrayList<ArrayList<SiteData>> OptionsCluster = (ArrayList<ArrayList<SiteData>>) readSiteData(new File("src/cache/ClusterBag.ser"));
        ArrayList<SiteData> Centroid = (ArrayList<SiteData>) readSiteData(new File("src/cache/Centroid.ser"));
        ArrayList<String> options1 = getUrlScrape("src/cache/data.csv");
        JFrame frame = new JFrame();
        frame.setSize(800,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ArrayList<SiteData> displayCenter = new ArrayList<>();
        ArrayList<String> options2 = new ArrayList<>();
        Tfidf tfidf = new Tfidf(ClusterLoader.allDoc);
        for(SiteData i : Centroid){

            int ClusId = i.getClusterId();
            options2.add(String.valueOf(ClusId));
            ArrayList<SiteData> placeholderClus = OptionsCluster.get(ClusId);
            SiteData closest = null; double maxSimilarity = -1;
            for (SiteData j : placeholderClus){
                double similarity = tfidf.cosineSimilarity2(j,i);
                if(similarity>maxSimilarity){
                    maxSimilarity = similarity;
                    closest = j;
                }
            }
            displayCenter.add(closest);
        }

        JComboBox<String> dropdown1 = new JComboBox<>(options1.toArray(new String[0]));
        JComboBox<String> dropdown2 = new JComboBox<>(options2.toArray(new String[0]));
        JButton button1 = new JButton("Submit for two most similar");
        JButton button2 = new JButton("Submit for cluster");

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 1, 0, 3));

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel1.add(dropdown1); panel1.add(button1);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel2.add(dropdown2); panel2.add(button2);

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel,BoxLayout.Y_AXIS));

        mainPanel.add(panel1);
        mainPanel.add(panel2);
        mainPanel.add(resultPanel);


        button1.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String input1 = (String) dropdown1.getSelectedItem();
                System.out.println(input1);
                try {
                    ArrayList<Object> results = processInput(input1);
                    resultPanel.removeAll();
                    resultPanel.add(new JLabel("Two most similar web pages"));
                    for (Object item: results){
                        resultPanel.add(new JLabel(item.toString()));
                    }
                    resultPanel.revalidate();
                    resultPanel.repaint();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        button2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try{
                    int input = Integer.parseInt((String) dropdown2.getSelectedItem());
                    ArrayList<SiteData> inputSite = OptionsCluster.get(input);
                    resultPanel.removeAll();
                    resultPanel.add(new Label("Closest center for Cluster"));
                    resultPanel.add(new Label(displayCenter.get(input).getUrl() ));
                    resultPanel.add(new Label(" Cluster Size: "+inputSite.size()));
                    resultPanel.revalidate();
                    resultPanel.repaint();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static  ArrayList<Object> processInput(String input) throws IOException {
        ArrayList<String> UrlToScrape = getUrlScrape("src/cache/data.csv");
        int inputIndex = UrlToScrape.indexOf(input);
        HashTable cosineScore = new HashTable();
        ArrayList<HashTable> TextMap = getTextMap(UrlToScrape);

        HashTable inputTable = TextMap.get(inputIndex);
        int cosineIndex = 0;
        for (HashTable item : TextMap){
            Tfidf tfidf = new Tfidf(TextMap);
            double cosine = tfidf.cosineSimilarity(item,inputTable);
            cosineScore.add(UrlToScrape.get(cosineIndex),cosine);
            cosineIndex++;
        }
        return getTwoHighest(cosineScore,UrlToScrape,input);
    }

    private static ArrayList<Object> getTwoHighest(HashTable cosineScore, ArrayList<String> UrlToScrape,String input) {
        cosineScore.remove(input);
        UrlToScrape.remove(UrlToScrape.get(UrlToScrape.indexOf(input)));
        double maxVal1 =0.0, maxVal2 = 0.0;
        String maxURL1 = null, maxURL2 = null;

        for (int i =0; i< UrlToScrape.size()-1; ++i){
            String key = UrlToScrape.get(i);
            double value = cosineScore.getValue(key);
            if(value  > maxVal1){
                maxVal2= maxVal1;
                maxURL2 = maxURL1;
                maxVal1 = value;
                maxURL1 = key;
            }else if (value > maxVal2 && value != maxVal1){
                maxVal2 = value;
                maxURL2 = key;
            }
        }
        ArrayList<Object> result = new ArrayList<>();
        result.add("Website "+maxURL1 + "   Similarity Score: " + maxVal1);
        result.add("Website "+maxURL2 + "   Similarity Score:" + maxVal2);

        return  result;

    }

    public static Object readSiteData(File file){
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))){
            return in.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("Error reading " + file + ": " + e.getMessage());
            return null;
        }
    }
}