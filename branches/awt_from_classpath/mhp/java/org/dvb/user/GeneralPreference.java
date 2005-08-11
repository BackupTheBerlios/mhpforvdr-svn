package org.dvb.user;

//Taken and adapted from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 7.3.2004
* @status not implemented
* @module internal
* @HOME
*/

/*
This class de nes a set of general preferences. These preferences are read from
the receiver and each application (downloaded or not) can access them through
the UserPreferenceManager.read method. The standardized preferences are "User
Language", "Parental Rating", "User Name", "User Address", "User @", "Country
Code", "Default Font Size". When constructed, objects of this class are empty
and have no values de ned. Values may be added using the add methods inherited
from the Preference class or by calling UserPreferenceManager.read. The
encodings of these standardized preferences are as follows. " User Language: 3
letter ISO 639 language codes; " Parental rating: string using the same encoding
as returned by javax.tv.service.guide.ContentRatingAdvisory. getDisplayText; "
User name: name of the user, first name(s) first and last name last; " User
Address: postal address of the user, may contain multiple lines separated by
carriage return characters (as defined in table D-4). " User @: e-mail address
of the user in the SMTP form as defined in RFC821; " Country Code: two letter
ISO 3166-1 country code; " Default font size: preferred font size for normal
body text expressed in points, decimal integer value encoded as a string (26 is
the default; differing size indicates a preference of different font size than
usual)
*/

public final class GeneralPreference extends Preference {

//These values of these fields are standardized by the spec,
//but the existence of these fields is not official API.
final static String UserLanguage = "User Language";
final static String ParentalRating = "Parental Rating";
final static String UserName = "User Name";
final static String UserAddress = "UserAddress";
final static String UserEmail = "User @";
final static String CountryCode = "Country Code";
final static String DefaultFontSize = "Default Font Size";

public GeneralPreference (String name) throws IllegalArgumentException {
   //I am not sure if we should ensure that only the above names are supported.
   //For now, leave it.
   super(name);
}

}
