// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies, a Verizon Company
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/proj/Ellipsoid.java,v $
// $RCSfile: Ellipsoid.java,v $
// $Revision: 1.1.1.1 $
// $Date: 2003/02/14 21:35:49 $
// $Author: dietrick $
// 
// **********************************************************************



package geo.openmap;

/**
 * A class representing a reference Ellipsoid for UTM calculations. <p>
 *
 * Adapted to Java by Colin Mummery (colin_mummery@agilent.com) from
 * C++ code by Chuck Gantz (chuck.gantz@globalstar.com).
 */
public class Ellipsoid {

    //The ellipsoids defined for this implementation
    /** "Airy" */
    public final static Ellipsoid AIRY = new Ellipsoid("Airy",6377563.0d, 0.00667054d); 
    /** "Australian National" */
    public final static Ellipsoid AUSTRALIAN_NATIONAL = new Ellipsoid("Australian National",6378160.0d, 0.006694542d); 
    /** "Bessel 1841" */
    public final static Ellipsoid BESSEL_1841 = new Ellipsoid("Bessel 1841",6377397.0d, 0.006674372d); 
    /**  "Bessel 1841 (Nambia) " */
    public final static Ellipsoid BESSEL_1841_NAMIBIA = new Ellipsoid("Bessel 1841 Namibia",6377484.0d, 0.006674372d); 
    /** "Clarke 1866" */
    public final static Ellipsoid CLARKE_1866 = new Ellipsoid("Clarke 1866",6378206.0d, 0.006768658d); 
    /**  "Clarke 1880" */
    public final static Ellipsoid CLARKE_1880 = new Ellipsoid("Clarke 1880",6378249.0d, 0.006803511d); 
    /**  "Everest" */
    public final static Ellipsoid EVEREST = new Ellipsoid("Everest",6377276.0d, 0.006637847d);
    /**  "Fischer 1960 (Mercury) " */
    public final static Ellipsoid FISHER_1960_MERCURY = new Ellipsoid("Fisher 1960 Mercury",6378166.0d, 0.006693422d);
    /**  "Fischer 1968" */
    public final static Ellipsoid FISHER_1968 = new Ellipsoid("Fisher 1968",6378150.0d, 0.006693422d);
    /**  "GRS 1967" */
    public final static Ellipsoid GRS_1967 = new Ellipsoid("GRS 1967",6378160.0d, 0.006694605d);
    /**  "GRS 1980" */
    public final static Ellipsoid GRS_1980 = new Ellipsoid("GRS 1980",6378137.0d, 0.00669438d);
    /**  "Helmert 1906" */
    public final static Ellipsoid HELMERT_1906 = new Ellipsoid("Helmert 1906",6378200.0d, 0.006693422d);
    /**  "Hough" */
    public final static Ellipsoid HOUGH = new Ellipsoid("Hough",6378270.0d, 0.00672267d); 
    /**  "International" */
    public final static Ellipsoid INTERNATIONAL = new Ellipsoid("International",6378388.0d, 0.00672267d); 
    /** "Krassovsky" */
    public final static Ellipsoid KRASSOVSKY = new Ellipsoid("Krassovsky",6378245.0d, 0.006693422d); 
    /** "Modified Airy" */
    public final static Ellipsoid MODIFIED_AIRY = new Ellipsoid("Modified Airy",6377340.0d, 0.00667054d); 
    /** "Modified Everest" */
    public final static Ellipsoid MODIFIED_EVEREST = new Ellipsoid("Modified Everest",6377304.0d, 0.006637847d); 
    /** "Modified Fischer 1960" */
    public final static Ellipsoid MODIFIED_FISCHER_1960 = new Ellipsoid("Modified Fischer",6378155.0d, 0.006693422d); 
    /** "South American 1969" */
    public final static Ellipsoid SOUTH_AMERICAN_1969 = new Ellipsoid("South American 1969",6378160.0d, 0.006694542d);
    /** "WGS 60" */
    public final static Ellipsoid WGS_60 = new Ellipsoid("WSG 60",6378165.0d, 0.006693422d); 
    /** "WGS 66" */
    public final static Ellipsoid WGS_66 = new Ellipsoid("WGS 66",6378145.0d, 0.006694542d); 
    /** "WGS-72" */
    public final static Ellipsoid WGS_72 = new Ellipsoid("WGS 72",6378135.0d, 0.006694318d); 
    /** "WGS-84" */
    public final static Ellipsoid WGS_84 = new Ellipsoid("WGS 84",6378137.0d, 0.00669438d); 

    /**
     * The display name for this ellipsoid.
     */
    public String name;

    /**
     * The equitorial radius for this ellipsoid.
     */
    public double radius;

    /**
     * The square of this ellipsoid's eccentricity.
     */
    public double eccsq;

    /**
     * Constructs a new Ellipsoid instance.
     * @param radius The earth radius for this ellipsoid.
     * @param eccsq The square of the eccentricity for this ellipsoid.
     */
    public Ellipsoid(String name, double radius, double eccsq){
	this.name=name;
	this.radius=radius; 
	this.eccsq=eccsq;
    }

    /**
     * Returns an array of all available ellipsoids in alphabetical order by name.
     * 
     * @return An Ellipsoid[] array containing all the available ellipsoids
     */
    public static Ellipsoid[] getAllEllipsoids(){

	Ellipsoid[] all=
 		{AIRY,
		AUSTRALIAN_NATIONAL,
		BESSEL_1841,
		BESSEL_1841_NAMIBIA,
		CLARKE_1866,
		CLARKE_1880,
		EVEREST,
		FISHER_1960_MERCURY,
		FISHER_1968,
		GRS_1967,
		GRS_1980,
		HELMERT_1906,
		HOUGH,
		INTERNATIONAL,
		KRASSOVSKY,
		MODIFIED_AIRY,
		MODIFIED_EVEREST,
		MODIFIED_FISCHER_1960,
		SOUTH_AMERICAN_1969,
		WGS_60,
 		WGS_66,
		WGS_72,
		WGS_84};

	return all;
    }


    /**
     * Returns a string representation of the object.
     * @return String representation
     */
    public String toString () {
	return "Ellipsoid[name=" + name + ", radius=" + radius + ", eccsq=" + eccsq + "]";
    }
}
