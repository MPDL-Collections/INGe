package de.mpg.mpdl.inge.pubman.web.appbase;

/**
 * 
 * This interface defines the implementing class as internationalized. All localized messages should
 * be fetched through the getMessage method.
 * 
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface Internationalized {
  /**
   * Returns the localized message to a given placeholder.
   * 
   * @param placeholder A string representing a message in the resource bundles.
   * 
   * @return The according localized message.
   */
  String getMessage(String placeholder);

  /**
   * Returns the localized label to a given placeholder.
   * 
   * @param placeholder A string representing a label in the resource bundles.
   * 
   * @return The according localized label.
   */
  String getLabel(String placeholder);
}
