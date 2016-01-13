/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lcss.structure;

import java.util.ArrayList;

/**
 *
 * @author bob
 */
public class Structure {

    
    private static ArrayList<GeographicalSpots> first = new ArrayList<>(100); // first sample
    private static ArrayList<GeographicalSpots> second = new ArrayList<>(100); //second sample

    
    /**
     * Add an element at the first sample
     * @param spot 
     */
    public static void setElementAtFirst(GeographicalSpots spot){
        first.add(spot);
    }
    
    /**
     * Add an element at the second sample
     * @param spot 
     */
    public static void setElementAtSecond(GeographicalSpots spot){
        second.add(spot);
    }
    
    /**
     * getter
     * @return the first Sample Arraylist 
     */
    public static ArrayList<GeographicalSpots> getFirst() {
        return first;
    }

    /**
     * getter
     * @return the second Sample Arraylist 
     */
    public static ArrayList<GeographicalSpots> getSecond() {
        return second;
    }
    
    /**
     * 
     * @param first Arraylist
     */
    public static void setFirst(ArrayList<GeographicalSpots> first) {
        Structure.first = first;
    }

    
    /**
     * 
     * @param second Arraylist
     */
    public static void setSecond(ArrayList<GeographicalSpots> second) {
        Structure.second = second;
    }

}
