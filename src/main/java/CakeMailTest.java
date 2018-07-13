/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonathan
 */
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.time.Month;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;


import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;


public class CakeMailTest {
    private static final String fichier_cvs="./src/main/resources/eng-daily-01012018-12312018.csv";
    private static final String fichier_json="statistiques_annuelles.json";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        
        
        try{
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            //Lecture du ficher CSV et creation d'une liste de MeteoJournaliere
            ArrayList<MeteoJournaliere> lmj=creationListeMeteoJournaliereFichierCSV(fichier_cvs);
                       
            //Trouver la temperature minimale et maximale par mois
            Map<Month,Pair<Double,Double>>mj_sorted=getTemperatureMinMaxMensuelle(lmj);
            
            //Total des precipitations de neige pour l'annee
            double precipitations_totales =getPrecipitationsNeigeTotales(lmj);
            
            //Creation de l'objet Json
            JsonObject statistiques_annuelles=satistiquesAnnuellesMapToJson(mj_sorted,precipitations_totales);
           
            //Ecrire l'objet Json dans un fichier text
            OutputStream os = new FileOutputStream(fichier_json);
            JsonWriter jsonWriter = Json.createWriter(os);
           
            jsonWriter.writeObject(statistiques_annuelles);
            jsonWriter.close();
                
        }catch(IOException s){
            System.out.println("File not found!");
        }
        
    }
    //Methode qui retourne les precipitations totales de neige pour l'annee
    public static double getPrecipitationsNeigeTotales(ArrayList<MeteoJournaliere> lmj){
        return lmj.stream().mapToDouble(MeteoJournaliere::getPrecipitationNeige).sum();
    
    }
    //Methode qui prend les donnees statistiques annuelles et retourne une Map de la temperature minimale et maximale mensuelle
    public static Map<Month,Pair<Double,Double>> getTemperatureMinMaxMensuelle(ArrayList<MeteoJournaliere> lmj){
        Map<Month,Pair<Double,Double>> mj_unsorted = lmj.stream().collect(groupingBy(MeteoJournaliere::getMonth,mapping(mj -> new Pair<Double,Double>(mj.getTemperatureMin(),mj.getTemperatureMax()),reducing(new Pair<>(new Double(1000.0), new Double(-1000.0)),(a, b) -> new Pair<>(Math.min(a.x, b.x), Math.max(a.y, b.y))))));
   
        //Mettre les mois en ordre 
        Map<Month,Pair<Double,Double>> mj_sorted = new TreeMap<Month,Pair<Double,Double>>(mj_unsorted);
        
        return mj_sorted;
    }
   
    //Methode qui cree un objet Json contenant les statistiques annuelles
    public static JsonObject satistiquesAnnuellesMapToJson(Map<Month,Pair<Double,Double>> map_mmm,double precipitations_totales){
        
            JsonArrayBuilder min_max_annuel_builder = Json.createArrayBuilder();
            
            JsonObjectBuilder statistiques_annuelles_builder = Json.createObjectBuilder();
            
            //Cree le tableau des temperatures minimales et maximales mensuelles pour l'annee
            for(Map.Entry<Month,Pair<Double,Double>>mmm:map_mmm.entrySet()){
                JsonObjectBuilder min_max_mensuel_builder = Json.createObjectBuilder();
                min_max_mensuel_builder.add("Mois", mmm.getKey().toString());
                min_max_mensuel_builder.add("Minimum", mmm.getValue().x);
                min_max_mensuel_builder.add("Maximum", mmm.getValue().y);
                JsonObject min_max_mensuel = min_max_mensuel_builder.build();
                min_max_annuel_builder.add(min_max_mensuel);
            }
            JsonArray min_max_annuel = min_max_annuel_builder.build();
            
            //Creation de l'objet json contenant les statistiques annuelles
            statistiques_annuelles_builder.add("TemperatureMinMaxMensuelle", min_max_annuel);
            statistiques_annuelles_builder.add("PrecipitationsNeigeTotalesAnnuelles",precipitations_totales);
            JsonObject statistiques_annuelles = statistiques_annuelles_builder.build();
            
            return statistiques_annuelles;
    }

    public static ArrayList<MeteoJournaliere> creationListeMeteoJournaliereFichierCSV(String fileName)throws IOException {
        ArrayList<MeteoJournaliere> lmj = new ArrayList();
        try (
            Reader reader = Files.newBufferedReader(Paths.get(fileName));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
        ) {
            boolean hasReachedData =false;
            for(CSVRecord r:csvParser){
                //Deplacement dans le fichier CSV jusqu'a la ligne suivant l'entete Date/Time
                if(!hasReachedData){
                    hasReachedData=r.get(0).equals("Date/Time");
                }
                else{
                    //Les donnees sont invalide lorsque les champs 5 et 7 sont vide
                    if(!r.get(5).equals("")&&!r.get(7).equals("")){
                        
                        //Obtenir les champs correspondant a la temperature minimale et maximale, la date et les precipitations de neige
                        LocalDate date=LocalDate.parse(r.get(0));
                        double max=Double.parseDouble(r.get(5));
                        double min=Double.parseDouble(r.get(7));
                        
                        double prec_neige=Double.parseDouble(((r.get(17).equals(""))?"0":r.get(17)));
                        
                        //Ajout des donnees dans une liste
                        lmj.add(new MeteoJournaliere(date,min,max,prec_neige));                
                    }
                }
            }
            csvParser.close();
            return lmj;
        
        }
    }
}
    

