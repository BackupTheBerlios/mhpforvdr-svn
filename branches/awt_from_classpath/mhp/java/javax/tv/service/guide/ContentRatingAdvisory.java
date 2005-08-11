
package javax.tv.service.guide;

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
public interface ContentRatingAdvisory {

/*
 
 Returns a list of names of all dimensions in this rating
 region by which the ProgramEvent is rated. 
 Returns: An array of strings representing all rated dimensions in this
 rating region for the ProgramEvent . See Also:   RatingDimension  
 
 
 */

public java.lang.String[] getDimensionNames ();


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
           throws javax.tv.service.SIException ;


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
                  throws javax.tv.service.SIException ;


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

public java.lang.String getDisplayText ();


/*
 
 Compares the current rating value with the system rating
 ceiling. The rating ceiling is set in a system-dependent manner.
 Content that exceeds the rating ceiling cannot be displayed. 
 Returns:  true if the rating exceeds the current
 system rating ceiling; false otherwise. 
 
 
*/

public boolean exceeds ();



}

