/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Map;

import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonValue;

import java.util.TreeMap;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jonathan
 */
public class CakeMailTestTest {
    public ArrayList<MeteoJournaliere> lmj_test;
    public Map<Month, Pair<Double, Double>> minMaxMap;
    
    public CakeMailTestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    
        lmj_test=new ArrayList();
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-01-05"),-5,10,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-01-06"),3,15,2));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-01-07"),-7,-1,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-02-06"),-31,-25,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-02-07"),-15,-5,3));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-02-04"),-21,-3,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-04-05"),8,15,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-04-07"),7,12,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-04-09"),-5,2,0.5));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-07-05"),26,31,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-07-06"),27,35,0));
        lmj_test.add(new MeteoJournaliere(LocalDate.parse("2018-07-07"),21,28,0));
        
        minMaxMap = new TreeMap();
        minMaxMap.put(Month.JANUARY, new Pair(-7.0,15.0));
        minMaxMap.put(Month.FEBRUARY, new Pair(-31.0,-3.0));
        minMaxMap.put(Month.APRIL, new Pair(-5.0,15.0));
        minMaxMap.put(Month.JULY, new Pair(21.0,35.0));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class CakeMailTest.
     * 
     */
//    @Ignore
//    @Test (expected=IOException.class)
//    public void testMain() throws Exception {
//        System.out.println("main");
//        String[] args = null;
//        CakeMailTest.main(args);
//    }

    /**
     * Test of getTemperatureMinMaxMensuelle method, of class CakeMailTest.
     */
    @Test
    public void testGetTemperatureMinMaxMensuelle() {
        System.out.println("getTemperatureMinMaxMensuelle");
       
        Map<Month, Pair<Double, Double>> result = CakeMailTest.getTemperatureMinMaxMensuelle(lmj_test);
        //Verification que les Maps sont de la meme dimension
        System.out.println("Verification de la taille des Maps");
        assertEquals(minMaxMap.size(),result.size());
        
        Iterator<Map.Entry<Month, Pair<Double, Double>>> it=result.entrySet().iterator();
        Iterator<Map.Entry<Month, Pair<Double, Double>>> expIt=minMaxMap.entrySet().iterator();
        
        System.out.println("Verification des champs mois, temperatures minimum et maximum obtenues en format Map");
        while(it.hasNext()&&expIt.hasNext()){
           //Verification des clefs
           Map.Entry<Month, Pair<Double, Double>> entry=it.next();
           Map.Entry<Month,Pair<Double,Double>> expEntry= expIt.next();
           
           assertEquals(expEntry.getKey(), entry.getKey());
           
           //Verification des valeurs minimum et maximum obtenues
           assertEquals(expEntry.getValue().x, entry.getValue().x);
           assertEquals(expEntry.getValue().y, entry.getValue().y);
        }
        
        
        
    }

    /**
     * Test of satistiquesAnnuellesMapToJson method, of class CakeMailTest.
     */
    @Test
    public void testSatistiquesAnnuellesMapToJson() {
        System.out.println("satistiquesAnnuellesMapToJson");
        
        double precipitations_totales = 5.5;
        JsonObject expResult = null;
        JsonObject result = CakeMailTest.satistiquesAnnuellesMapToJson(minMaxMap, precipitations_totales);
        
        JsonArray minMaxMensuelJson = result.getJsonArray("TemperatureMinMaxMensuelle");
        
        //Verification de la taille de la Map et du tableau Json
        System.out.println("Comparaison entre la taille du tableau de format Map et Json");
        assertEquals(minMaxMap.size(), minMaxMensuelJson.size());
        
        Iterator<Map.Entry<Month, Pair<Double, Double>>> expIt = minMaxMap.entrySet().iterator();
        Iterator<JsonValue> it = minMaxMensuelJson.iterator();
        
        //Comparaison de tous les elements entre le tableau de format Map et Json
        System.out.println("Comparaison entre tous les objets dans le tableau de format Map et Json");
        while(it.hasNext()){
            JsonObject jo=(JsonObject)it.next();
            Map.Entry<Month, Pair<Double, Double>> expME = expIt.next();
            
            //Comparaison du mois
            assertEquals(expME.getKey().toString(),jo.getString("Mois"));
           
            //Comparaison de la temperature minimum et maximum
            assertEquals(expME.getValue().x, new Double(jo.getJsonNumber("Minimum").doubleValue()));
            assertEquals(expME.getValue().y, new Double(jo.getJsonNumber("Maximum").doubleValue()));
        }
        
        //Comparaison du total annuel des precipitations de neige
        System.out.println("Comparaison entre les precipitations de neige totales annuelles");
        assertEquals(new Double(precipitations_totales),new Double(result.getJsonNumber("PrecipitationsNeigeTotalesAnnuelles").doubleValue()));
        
    }

    /**
     * Test of creationListeMeteoJournaliereFichierCSV method, of class CakeMailTest.
     * 
     */
    @Test 
    public void testCreationListeMeteoJournaliereFichierCSV() throws Exception {
        System.out.println("creationListeMeteoJournaliereFichierCSV");
        String fileName = "./src/test/resources/test.csv";
        ArrayList<MeteoJournaliere> result = CakeMailTest.creationListeMeteoJournaliereFichierCSV(fileName);
        //Comparaison entre la liste existante et celle en format CVS
        for(int i=0;i<lmj_test.size();i++){
            assertEquals(lmj_test.get(i).getDate(), result.get(i).getDate());
            assertEquals(lmj_test.get(i).getPrecipitationNeige(), result.get(i).getPrecipitationNeige());
            assertEquals(lmj_test.get(i).getTemperatureMax(), result.get(i).getTemperatureMax());
            assertEquals(lmj_test.get(i).getTemperatureMin(), result.get(i).getTemperatureMin());
        }
        
    }

    /**
     * Test of getPrecipitationsNeigeTotales method, of class CakeMailTest.
     */
    @Test
    public void testGetPrecipitationsNeigeTotales() {
        System.out.println("getPrecipitationsNeigeTotales");
        ArrayList<MeteoJournaliere> lmj = null;
        double expResult = 0.0;
        double result = CakeMailTest.getPrecipitationsNeigeTotales(lmj_test);
        assertEquals(expResult, result, 10.0);      
      }
    
}