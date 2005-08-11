
package javax.tv.service.guide;

import javax.tv.service.DVBRatingDimension;

/*

ContentRatingAdvisory indicates, for a given program event, ratings
 for any or all of the rating dimensions defined in the content
 rating system for the local rating region. A program event without
 a content advisory indicates that the rating value for any rating
 dimension is zero. The absence of ratings for a specific dimension
 is equivalent to having a zero-valued rating for such a
 dimension. The absence of ratings for a specific region implies the
 absence of ratings for all the dimensions in the region. 
*/
public class DVBContentRatingAdvisory implements ContentRatingAdvisory {

short level=-1;

//parameter: value in the parental rating descriptor, between 0x1 and 0xF
public DVBContentRatingAdvisory(short level) {
   if ( 0x1 <= level && level >= 0xF)
      this.level = (short)(level-1);
}

/*
 
 Returns a list of names of all dimensions in this rating
 region by which the ProgramEvent is rated. 
 Returns: An array of strings representing all rated dimensions in this
 rating region for the ProgramEvent . See Also:   RatingDimension  
 
 
 */

public java.lang.String[] getDimensionNames () {
   String ret[]=new String[1];
   ret[0]=DVBRatingDimension.dimensionName;
   return ret;
}


/*
 
 Returns a number representing the rating level in the specified
 RatingDimension associated with this rating region
 for the related ProgramEvent . 
 Parameters:  dimensionName - The name of the RatingDimension 
 for which to obtain the rating level. Returns: A number representing the rating level. The meaning is
 dependent on the associated rating dimension. Throws:  SIException  - If dimensionName is not a valid
 name of a RatingDimension for the ProgramEvent. See Also:   RatingDimension.getDimensionName()  
 
 
 */

public short getRatingLevel (java.lang.String dimensionName)
           throws javax.tv.service.SIException
{
   if (dimensionName.equals(DVBRatingDimension.dimensionName)) {
      return level;
   } else
      throw new javax.tv.service.SIException("Unknown RatingDimension "+dimensionName);
}


/*
 
 Returns the rating level display string for the specified
 dimension. The string is identical to
 d.getRatingLevelDescription(getRatingLevel(dimensionName))[1] ,
 where d is the RatingDimension obtained
 by
 javax.tv.service.SIManager.getRatingDimension(dimensionName) . 
 Parameters:  dimensionName - The name of the RatingDimension 
 for which to obtain the rating level text. Returns: A string representing the textual value of this rating level. Throws:  SIException  - If dimensionName is not a valid
 RatingDimension name for the ProgramEvent . See Also:   RatingDimension.getDimensionName() , 
 RatingDimension.getRatingLevelDescription(short)  
 
 
 */

public java.lang.String getRatingLevelText (java.lang.String dimensionName)
                  throws javax.tv.service.SIException
{
   if (dimensionName.equals(DVBRatingDimension.dimensionName)) {
      if ( 0 <= level && level < 15)
         return "Recommended minimum age: "+(level+4)+" years";
      else 
         return "";
   } else
      throw new javax.tv.service.SIException("Unknown RatingDimension "+dimensionName);   
}


/*
 
 Provides a single string representing textual rating values for all
 dimensions in which the program event is rated.
 The result will be a representation of the strings obtained via
 d.getRatingLevelDescription(getRatingLevel(d.getDimensionName()))[0] ,
 for all dimensions d obtained through
 javax.tv.service.SIManager.getRatingDimension(n) ,
 for all dimension names n obtained from
 getDimensionNames() . 
 Returns: A string representing the rating level values for all
 dimensions in which this program event is rated. The format of
 the string may be implementation-specific. See Also:   getDimensionNames() , 
 RatingDimension.getRatingLevelDescription(short)  
 
 
 */

public java.lang.String getDisplayText () {
   if ( 0 <= level && level < 15)
      return "Over "+(level+4);
   else 
      return "";
}


/*
 
 Compares the current rating value with the system rating
 ceiling. The rating ceiling is set in a system-dependent manner.
 Content that exceeds the rating ceiling cannot be displayed. 
 Returns:  true if the rating exceeds the current
 system rating ceiling; false otherwise. 
 
 
*/

public boolean exceeds () {
   return false; //we don't like parental rating
}



}

