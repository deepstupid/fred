package org.spaceroots.mantissa;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** This class is the base class for all specific exceptions thrown by
 * the mantissa classes.

 * <p>When the mantissa classes throw exceptions that are specific to
 * the package, these exceptions are always subclasses of
 * MantissaException. When exceptions that are already covered by the
 * standard java API should be thrown, like
 * ArrayIndexOutOfBoundsException or IllegalArgumentException, these
 * standard exceptions are thrown rather than the mantissa specific
 * ones.</p>

 * @version $Id: MantissaException.java 1686 2005-12-16 12:59:51Z luc $
 * @author L. Maisonobe

 */

public class MantissaException
  extends Exception {

  private static final long serialVersionUID = 1L;
  private static final ResourceBundle resources
  = ResourceBundle.getBundle("org.spaceroots.mantissa.MessagesResources");

  /** Translate a string.
   * @param s string to translate
   * @return translated string
   */
  public static String translate(String s) {
    try {
      return resources.getString(s);
    } catch (MissingResourceException mre) {
      return s;
    }
  }

  /** Translate a message.
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   * @return translated message
   */
  public static String translate(String specifier, String[] parts) {
    return new MessageFormat(translate(specifier)).format(parts);
  }

  /** Simple constructor.
   * Build an exception with an empty message
   */
  public MantissaException() {
    super();
  }

  /** Simple constructor.
   * Build an exception by translating the specified message
   * @param message message to translate
   */
  public MantissaException(String message) {
    super(translate(message));
  }

  /** Simple constructor.
   * Build an exception by translating and formating a message
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   */
  public MantissaException(String specifier, String[] parts) {
    super(translate(specifier, parts));
  }

  /** Simple constructor.
   * Build an exception from a cause
   * @param cause cause of this exception
   */
  public MantissaException(Throwable cause) {
    super(cause);
  }

  /** Simple constructor.
   * Build an exception from a message and a cause
   * @param message message to translate
   * @param cause cause of this exception
   */
  public MantissaException(String message, Throwable cause) {
    super(translate(message), cause);
  }

  /** Simple constructor.
   * Build an exception from a message and a cause
   * @param specifier format specifier (to be translated)
   * @param parts to insert in the format (no translation)
   * @param cause cause of this exception
   */
  public MantissaException(String specifier, String[] parts, Throwable cause) {
    super(translate(specifier, parts), cause);
  }

}
