package org.dvb.dsmcc;

/**
* @author tejopa
* @date 7.3.2004
* @status not implemented
* @module internal
* @HOME
*/
public class ServiceXFRReference  {

   String pathName;
   org.davic.net.Locator serviceLocator;
   int carouselId;
   byte[] nsapAddress;

   public ServiceXFRReference(org.davic.net.Locator serviceLocator, int carouselId, String pathName) {
      this.serviceLocator=serviceLocator;
      this.carouselId=carouselId;
      this.pathName=pathName;
   }

   public ServiceXFRReference(byte[] nsapAddress, String pathName){
      this.nsapAddress=nsapAddress;
      this.pathName=pathName;
   }

   public org.davic.net.Locator getLocator(){
      return serviceLocator;
   }

   public int getCarouselId(){
      return carouselId;
   }

   public String getPathName(){
      return pathName;
   }

   public byte[] getNSAPAddress(){
      return nsapAddress;
   }

}
