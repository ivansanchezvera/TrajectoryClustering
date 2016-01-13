/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lcss.load;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author bob
 */
public class Loader {

    private String datasetsPackage, small, medium, large;
    private Double e = 0.0;
    
    /**
     * Constructor initialize the attributes
     */
    public Loader() {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("resources/load.properties");
            Properties prop = new Properties();
            prop.load(is);
            this.datasetsPackage = prop.getProperty("datasetsPackage");
            this.small = prop.getProperty("small");
            this.medium = prop.getProperty("medium");
            this.large = prop.getProperty("large");
            String e_ = prop.getProperty("e");
            e = Double.valueOf(e_);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    Setters and Getters.
    */
    
    
    public  Double getE() {
        return this.e;
    }

    public  String getDatasetsPackage() {
        return datasetsPackage != null ? datasetsPackage : "";
    }

    public  String getSmall() {
        return small != null ? small : "";
    }

    public String getMedium() {
        return medium != null ? medium : "";
    }

    public String getLarge() {
        return large != null ? large : "";
    }

}
