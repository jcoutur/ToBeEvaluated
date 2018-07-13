/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonathan
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonathan
 */


import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

public class MeteoJournaliere {
    private final LocalDate date;
    private final Double temp_min;
    private final Double temp_max;
    private final Double prec_neige;
    
    public MeteoJournaliere(LocalDate d, double temperature_min,double temperature_max,double precipitation_neige){
        date=d;
        temp_min=temperature_min;
        temp_max=temperature_max;
        prec_neige=precipitation_neige;
    }
    
    public LocalDate getDate(){
        return date;
    }
    
    public Double getTemperatureMin(){
        return temp_min;
    }
    
    public Double getTemperatureMax(){
        return temp_max;
    }
    
    public Double getPrecipitationNeige(){
        return prec_neige;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String toString(){
        return "Date: "+ date+" Temperature minimale: "+ temp_min+" Temperature maximale: "+ temp_max+" Precipitations en neige: "+ prec_neige;
    }
        
    public Month getMonth(){
        return date.getMonth();
    }
}
