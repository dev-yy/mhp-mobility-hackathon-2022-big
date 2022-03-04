package com.mhp.mobility.hackathon.showcase.kafka.subscriber.utils;

public final class StringHelper {
   
   public static final String EMPTY = "";
   
   private StringHelper() {}
   
   /**
    * Indicates whether the given string is null or empty.
    * 
    * @param string
    *           the given string
    * @return True, if the given string is null or empty
    */
   public static final boolean isNullOrEmpty(String string) {
      return string == null || string.length() == 0;
   }
   
   /**
    * Returns the string representation of the given string or an empty string if null.
    * 
    * @param object
    *           any object
    * @return the string representation of the given string or an empty string if null
    */
   public final static String getStringOrEmptyIfNull(Object object) {
      return getString(object, StringHelper.EMPTY);
   }
   
   /**
    * Returns the string representation of the given object or the given string if null.
    * 
    * @return the string representation of the given object or the given string if null
    * @param object
    *           any object
    */
   public final static String getString(Object object, String nullValueString) {
      if (object == null) {
         return nullValueString;
      } else {
         return object.toString();
      }
   }
   
   /**
    * Returns the string without the first element of the given string separated by separator.
    * 
    * @return the string without the first element of the given string separated by separator string.
    * @param string
    *           String
    * @param separator
    *           the separator
    */
   public final static String getWithoutFirstElement(String string, String separatorString) {
      int i = string.indexOf(separatorString);
      if (i == -1)
         return string;
      else
         return string.substring(i + 1);
      
   }
   
   /**
    * Returns the string without the last element of the given string separated by separator string.
    * 
    * @return the string without the last element of the given string separated by separator string
    * @param string
    *           String
    * @param separator
    *           the separator
    */
   public final static String getWithoutLastElement(String string, String separatorString) {
      int i = string.lastIndexOf(separatorString);
      if (i == -1)
         return string;
      else
         return string.substring(0, i);
      
   }
}
