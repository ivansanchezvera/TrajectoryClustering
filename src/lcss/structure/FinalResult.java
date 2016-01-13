/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lcss.structure;

import java.util.ArrayList;

/**
 *
 * @author nikolas
 */

/**
 * This struct contains the Final result
 */
public class FinalResult {

    private String path = null;
    private Integer maxLcss = null;
    private Integer minLength = null;
    private Double S = null;
    private ArrayList<GeographicalSpots> union = null;
    private long time;

    /**
     * Initialize the Object
     * @param path
     * @param maxLcss
     * @param minLength
     * @param S
     * @param time
     * @param union 
     */
    public FinalResult(String path, Integer maxLcss, Integer minLength, Double S, long time, ArrayList<GeographicalSpots> union) {
        this.path = path;
        this.maxLcss = maxLcss;
        this.minLength = minLength;
        this.union = union;
        this.S = S;
        this.time = time;
    }

    
    @Override
    public String toString() {

        String str = "S : " + this.S + "\n\nTime : " + this.time + " seconds." +"\n\nPath : " + this.path ;
        for (int i = union.size()-1 ; i >=0; i--) {
            str += "\n" + union.get(i).toString();
        }
        return str;
    }

    /*
     Seters geters
     */
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getMaxLcss() {
        return maxLcss;
    }

    public void setMaxLcss(Integer maxLcss) {
        this.maxLcss = maxLcss;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Double getS() {
        return S;
    }

    public void setS(Double S) {
        this.S = S;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    
    
    public ArrayList<GeographicalSpots> getUnion() {
        return union;
    }

    public void setUnion(ArrayList<GeographicalSpots> union) {
        this.union = union;
    }

}
