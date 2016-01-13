/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lcss;

import java.text.DecimalFormat;
import lcss.load.Loader;
import lcss.structure.GeographicalSpots;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import lcss.structure.FinalResult;

/**
 *
 * @author bob
 */
public class LCSS {

    /**
     * Generate random number [start, end] I created this function for d
     * parameter.
     *
     * @param aStart
     * @param aEnd
     * @param aRandom
     */
    private static int getRandomInteger(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        Random random = new Random();
        //get the range, casting to long to avoid overflow problems
        long range = (long) end - (long) start + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * random.nextDouble());
        return (int) (fraction + start);
    }

    /**
     *
     * @param d1
     * @param d2
     * @return compare two Doubles
     */
    public static int compare(Double d1, Double d2) {
        return Double.compare(d1, d2);
    }

    /**
     *
     * @param i1
     * @param i2
     * @return compare two Integers
     */
    public static int compare(Integer i1, Integer i2) {
        return Integer.compare(i1, i2);
    }

    /**
     * @param d1 first Double
     * @param d2 second Double
     * @return the subtraction between two Doubles
     */
    public static Double subtraction(Double d1, Double d2) {
        return new Double(d1 - d2);
    }

    /**
     * @param d1 first Double
     * @param d2 second Double
     * @return the addition between two Doubles
     */
    public static Double addition(Double d1, Double d2) {
        return new Double(d1 + d2);
    }

    /**
     *print the Lcss array 
     * @param lengths
     */
    public static void printArray(int[][] lengths) {
        for (int i = 0; i < lengths.length; i++) {
            System.out.print("ROW : " + i);
            System.out.print("          | ");
            for (int j = 0; j < lengths[i].length; j++) {
                System.out.print(lengths[i][j] + " | ");
            }
            System.out.println("");
        }
    }

    /**
     * LCSS algorithm for difference lengths datasets
     *
     * @param first
     * @param second
     * @return
     */
    public static FinalResult lcssDatasets(List<GeographicalSpots> first, ArrayList<GeographicalSpots> second) {
        
        Loader loader = new Loader();
        
        int[][] lengths = new int[first.size() + 1][second.size() + 1];

        long startTime, stopTime, elapsedTime;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < first.size(); i++) {
            for (int j = 0; j < second.size(); j++) {

                int retval;
                retval = compare(first.get(i).getLatitude(), second.get(j).getLatitude());
                boolean equalsLatitude;
                boolean equalsLongitude;
                if (retval > 0) {
                    equalsLatitude = compare(first.get(i).getLatitude(), addition(second.get(j).getLatitude(), loader.getE())) <= 0;
                } else if (retval < 0) {
                    equalsLatitude = compare(second.get(j).getLatitude(), addition(first.get(i).getLatitude(), loader.getE())) <= 0;
                } else {
                    equalsLatitude = true;
                }

                retval = compare(first.get(i).getLongitude(), second.get(j).getLongitude());

                if (retval > 0) {
                    equalsLongitude = compare(first.get(i).getLongitude(), addition(second.get(j).getLongitude(), loader.getE())) <= 0;
                } else if (retval < 0) {
                    equalsLongitude = compare(second.get(j).getLongitude(), addition(first.get(i).getLongitude(), loader.getE())) <= 0;
                } else {
                    equalsLongitude = true;
                }

                if (equalsLatitude && equalsLongitude) {
                    lengths[i + 1][j + 1] = lengths[i][j] + 1;
                } else {
                    lengths[i + 1][j + 1]
                            = Math.max(lengths[i + 1][j], lengths[i][j + 1]);
                }
            }
        }

        //printArray(lengths);
        @SuppressWarnings("StringBufferMayBeStringBuilder")
        StringBuffer sb = new StringBuffer();
        ArrayList<GeographicalSpots> union = new ArrayList<>();
        Vector<Integer> lcss = new Vector<Integer>();
        for (int x = first.size(), y = second.size();
                x != 0 && y != 0;) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--;
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--;
            } else {
                union.add(new GeographicalSpots(first.get(x-1).getLatitude(), first.get(x-1).getLongitude()));
                lcss.add(lengths[x][y]);
                sb.append(lengths[x][y]).append(" ");
                x--;
                y--;

            }
        }
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;

        long time = elapsedTime / 1000;
        Integer maxLcss;
        if (lcss.isEmpty()) {
            maxLcss = 0;
        } else {
            maxLcss = Collections.max(lcss);
        }

        Integer minLength = Math.min(first.size(), second.size());
        Double S;
        S = (double) maxLcss / (double) minLength;
        DecimalFormat myFormat = new DecimalFormat("0.000");
        String myDoubleString = myFormat.format(S);
        S = Double.valueOf(myDoubleString);

        return new FinalResult(sb.toString(), maxLcss, minLength, S, time, union);
    }

    /**
     * LCSS algorithm for equals lengths datasets
     *
     * @param first
     * @param second
     * @return
     */
    public static FinalResult lcssEqualsDatasets(ArrayList<GeographicalSpots> first, ArrayList<GeographicalSpots> second) {
        Loader loader = new Loader();
        
        int[][] lengths = new int[first.size() + 1][second.size() + 1];

        long startTime, stopTime, elapsedTime;

        startTime = System.currentTimeMillis();

        for (int i = 0; i < first.size(); i++) {
            for (int j = 0; j < second.size(); j++) {

                int retval;
                retval = compare(first.get(i).getLatitude(), second.get(j).getLatitude());
                boolean equalsLatitude;
                boolean equalsLongitude;
                if (retval > 0) {
                    equalsLatitude = compare(first.get(i).getLatitude(), addition(second.get(j).getLatitude(), loader.getE())) <= 0;
                } else if (retval < 0) {
                    equalsLatitude = compare(second.get(j).getLatitude(), addition(first.get(i).getLatitude(), loader.getE())) <= 0;
                } else {
                    equalsLatitude = true;
                }

                retval = compare(first.get(i).getLongitude(), second.get(j).getLongitude());

                if (retval > 0) {
                    equalsLongitude = compare(first.get(i).getLongitude(), addition(second.get(j).getLongitude(), loader.getE())) <= 0;
                } else if (retval < 0) {
                    equalsLongitude = compare(second.get(j).getLongitude(), addition(first.get(i).getLongitude(), loader.getE())) <= 0;
                } else {
                    equalsLongitude = true;
                }

                if (equalsLatitude && equalsLongitude) {
                    lengths[i + 1][j + 1] = lengths[i][j] + 1;
                } else {
                    lengths[i + 1][j + 1]
                            = Math.max(lengths[i + 1][j], lengths[i][j + 1]);
                }
            }
        }
        //printArray(lengths);
        @SuppressWarnings("StringBufferMayBeStringBuilder")
        StringBuffer sb = new StringBuffer();
        ArrayList<GeographicalSpots> union = new ArrayList<>();
        Vector<Integer> lcss = new Vector<Integer>();
        for (int x = first.size(), y = second.size();
                x != 0 && y != 0;) {
            if (lengths[x][y] == lengths[x - 1][y]) {
                x--;
            } else if (lengths[x][y] == lengths[x][y - 1]) {
                y--;
            } else {
                union.add(new GeographicalSpots(first.get(x-1).getLatitude(), first.get(x-1).getLongitude()));
                lcss.add(lengths[x][y]);
                sb.append(lengths[x][y]).append(" ");
                x--;
                y--;
            }
        }
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;

        long time = elapsedTime / 1000;

        Integer maxLcss;
        if ( lcss.isEmpty()) {
            maxLcss = 0;
        } else {
            maxLcss = Collections.max(lcss);
        }
        Integer minLength = Math.min(first.size(), second.size());
        Double S;
        S = (double) maxLcss / (double) minLength;
        DecimalFormat myFormat = new DecimalFormat("0.000");
        String myDoubleString = myFormat.format(S);
        S = Double.valueOf(myDoubleString);
        
        return new FinalResult(sb.toString(), maxLcss, minLength, S, time, union);
    }

    /**
     *The Lcss for the overlapping 
     * 
     * @param first
     * @param second
     * @return
     */
    public static FinalResult lcssDifferenceDatasets(ArrayList<GeographicalSpots> first, ArrayList<GeographicalSpots> second) {

        ArrayList<FinalResult> results = new ArrayList<>();
        int diff = compare(first.size(), second.size());
        int d;

        if (diff > 0) {
            d = second.size() /2 ;
            int tempStart = 0;
            int tempEnd = second.size()-1; // + (d - 1);
            int temp = tempEnd;
            boolean bool = true;
            boolean firstTime = false;
            while (bool) {

                if (tempEnd > first.size()) {
                    tempEnd = first.size() - 1;
                    firstTime = true;
                }
                
                if(tempEnd == tempStart)
                    break;

                if((tempEnd-tempStart) < (second.size()-1) ){
                    tempEnd = first.size()-1;
                    tempStart = tempEnd - second.size()-1;
                }
                
                System.out.println("Toso to start : " + tempStart + " end : " + tempEnd);
                List<GeographicalSpots> subList = first.subList(tempStart, tempEnd);
                FinalResult res = lcssDatasets(subList, second);
                System.out.println("Mpike sto 1 kai exei S : " + res.getS());
                System.out.println("");
                results.add(res);
                tempStart = tempEnd - d;
                tempEnd = tempEnd + temp -d;

                if ((tempStart >= first.size() || tempEnd >= first.size()) && firstTime) {
                    bool = false;
                }
            }

            int finalI = 0;
            Double FinalS = new Double("0.0");
            for (int i = 0; i < results.size(); i++) {
                if (compare(results.get(i).getS(), FinalS) >= 0 && !results.get(i).getS().equals(0.0) ) {
                    FinalS = results.get(i).getS();
                    finalI = i;
                }
            }
            // Sad but true , no match
            if (finalI == 0) {
                for (int i = 0; i < results.size(); i++) {
                    if (compare(results.get(i).getS(), FinalS) >= 0) {
                        FinalS = results.get(i).getS();
                        finalI = i;
                    }
                }
            }
            return results.get(finalI);
        } else {
            d = first.size() / 2;
            int tempStart = 0;
            int tempEnd = first.size() -1 ;//+ (d - 1);
            int temp = tempEnd;
            boolean bool = true;
            boolean firstTime = false;

            while (bool) {
                if (tempEnd >= second.size()) {
                    tempEnd = second.size() - 1;
                    firstTime = true;
                }
                
                if(tempEnd == tempStart)
                    break;

                if((tempEnd-tempStart) < (first.size()-1) ){
                    tempEnd = second.size()-1;
                    tempStart = tempEnd - first.size()-1;
                }
                
                System.out.println("Toso to start : " +tempStart + " end : " +tempEnd);
                List<GeographicalSpots> subList = second.subList(tempStart, tempEnd);
                FinalResult res = lcssDatasets(subList, first);
                System.out.println("Mpike sto 2 kai exei S : " + res.getS());
                System.out.println("");
                results.add(res);
                tempStart = tempEnd - d;
                tempEnd = tempEnd + temp -d;
                
                if ((tempStart >= (second.size()) || tempEnd >= second.size()) && firstTime) {
                    bool = false;
                }
            }

            int finalI = 0;
            Double FinalS = new Double("0.0");
            for (int i = 0; i < results.size(); i++) {
                if (compare(results.get(i).getS(), FinalS) >= 0 && !results.get(i).getS().equals(0.0)) {
                    FinalS = results.get(i).getS();
                    finalI = i;
                }
            }

            // Sad but true , no match
            if (finalI == 0) {
                for (int i = 0; i < results.size(); i++) {
                    if (compare(results.get(i).getS(), FinalS) >= 0) {
                        FinalS = results.get(i).getS();
                        finalI = i;
                    }
                }
            }
            return results.get(finalI);
        }
    }

    /**
     * The final lcss algorithm
     *
     * @param first
     * @param second
     * @return
     */
    public static FinalResult lcss(ArrayList<GeographicalSpots> first, ArrayList<GeographicalSpots> second) {
        
        if (first.isEmpty() ||  second.isEmpty() ) {
            ArrayList<GeographicalSpots> union = new ArrayList<>();
            return new FinalResult("No matches" ,0,0,0.0,0,union);
        }
        
        if (first.size() == second.size()) {
            return lcssEqualsDatasets(first, second);
        } else {
            return lcssDifferenceDatasets(first, second);
        }
    }

}
