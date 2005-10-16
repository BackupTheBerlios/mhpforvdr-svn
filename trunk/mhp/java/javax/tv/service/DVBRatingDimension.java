package javax.tv.service;

/*

   Implements the DVB age based rating dimension.
   All values according to annex of of the MHP spec.

*/
public class DVBRatingDimension implements RatingDimension {

public static final String dimensionName = "DVB Age based rating";

/*
 
 Returns a string which represents the dimension name being described by
 this object. One dimension in the U.S. rating region, for example, is
 used to describe the MPAA list. The dimension name for such a case may
 be defined as "MPAA". 
 Returns: A string representing the name of this rating dimension. 
 
 
 */
 
DVBRatingDimension() {
}

public java.lang.String getDimensionName () {
   return dimensionName;
}


/*
 
 Returns the number of levels defined for this dimension. 
 Returns: The number of levels in this dimension. 
 
 
 */

public short getNumberOfLevels () {
   return 15; //from 4 to 18 years
}


/*
 
 Returns a pair of strings describing the specified rating level for
 this dimension. 
 Parameters:  ratingLevel - The rating level for which to retrieve the
 textual description. Returns: A pair of strings representing the names for the
 specified rating level. The first string represents the abbreviated
 name for the rating level. The second string represents the
 full name for the rating level. Throws:  SIException  - If ratingLevel is not valid for
 this RatingDimension . See Also:   ContentRatingAdvisory.getRatingLevel(java.lang.String)  
 
 
*/

public java.lang.String[] getRatingLevelDescription (short ratingLevel)
                       throws SIException 
{
   return new String [] { "Over "+(ratingLevel+4), "Recommended minimum age: "+(ratingLevel+4)+" years" };
}



}

