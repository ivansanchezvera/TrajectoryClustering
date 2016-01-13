/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lcss.load;

import lcss.structure.GeographicalSpots;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bob
 */
public class ReadFromTXT {
    
    private static BufferedReader reader = null;
    
    public ReadFromTXT(){}
    
    /**
     * Reads the txt file and returns an Arraylist of dataset
     * @param path
     * @return 
     */
    public ArrayList<GeographicalSpots> read(String path){
    ArrayList<GeographicalSpots> list = new ArrayList<>(100);
        try {
        reader = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = reader.readLine();    
            } catch (IOException ex) {
                Logger.getLogger(ReadFromTXT.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //parsing the comma attributes
            while (line != null) {
                //System.out.println("line : " + line);
                
                StringTokenizer st2 = new StringTokenizer(line, ",");
                int count = 0;
		while (st2.hasMoreElements()) {
                    if(count <=1 ){
                        count ++;
                        st2.nextElement();
                    }
                    else{
                        //System.out.println(" latidute : " + st2.nextElement() + " longitude : " + st2.nextElement());    
                        list.add(new GeographicalSpots(Double.parseDouble((String)st2.nextElement()), Double.parseDouble((String)st2.nextElement())));
                    }
		}
                
                
                sb.append(line);
                sb.append(System.lineSeparator());
                try {
                    line = reader.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(ReadFromTXT.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(ReadFromTXT.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }   catch (FileNotFoundException ex) {
            Logger.getLogger(ReadFromTXT.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
}
