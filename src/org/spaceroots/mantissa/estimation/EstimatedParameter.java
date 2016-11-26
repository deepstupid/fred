package org.spaceroots.mantissa.estimation;

import java.io.Serializable;

/** This class represent the estimated parameters of an estimation problem.

 * <p>The parameters of an estimation problem have a name, a value and
 * a bound flag. The value of bound parameters is considered trusted
 * and the solvers should not adjust them. On the other hand, the
 * solvers should adjust the value of unbounds parameters until they
 * satisfy convergence criterions specific to each solver.</p>

 * @version $Id: EstimatedParameter.java 1666 2005-12-15 16:37:55Z luc $
 * @author L. Maisonobe

 */

public class EstimatedParameter
  implements Serializable {

  /** Simple constructor.
   * Build an instance from a first estimate of the parameter,
   * initially considered unbound.
   * @param name name of the parameter
   * @param firstEstimate first estimate of the parameter
   */
  public EstimatedParameter(String name, double firstEstimate) {
    this.name = name;
    estimate  = firstEstimate;
    bound     = false;
  }

  /** Simple constructor.
   * Build an instance from a first estimate of the parameter and a
   * bound flag
   * @param name name of the parameter
   * @param firstEstimate first estimate of the parameter
   * @param bound flag, should be true if the parameter is bound
   */
  public EstimatedParameter(String name,
                            double firstEstimate,
                            boolean bound) {
    this.name  = name;
    estimate   = firstEstimate;
    this.bound = bound;
  }

  /** Copy constructor.
   * Build a copy of a parameter
   * @param parameter instance to copy
   */
  public EstimatedParameter(EstimatedParameter parameter) {
    name     = parameter.name;
    estimate = parameter.estimate;
    bound    = parameter.bound;
  }

  /** Set a new estimated value for the parameter.
   * @param estimate new estimate for the parameter
   */
  public void setEstimate(double estimate) {
    this.estimate = estimate;
  }

  /** Get the current estimate of the parameter
   * @return current estimate
   */
  public double getEstimate() {
    return estimate;
  }

  /** get the name of the parameter
   * @return parameter name
   */
  public String getName() {
    return name;
  }

  /** Set the bound flag of the parameter
   * @param bound this flag should be set to true if the parameter is
   * bound (i.e. if it should not be adjusted by the solver).
   */
  public void setBound(boolean bound) {
    this.bound = bound;
  }

  /** Check if the parameter is bound
   * @return true if the parameter is bound */
  public boolean isBound() {
    return bound;
  }

  /** Name of the parameter */
  private   String  name;

  /** Current value of the parameter */
  protected double  estimate;

  /** Indicator for bound parameters
   * (ie parameters that should not be estimated)
   */
  private   boolean bound;

  private static final long serialVersionUID = -555440800213416949L;

}
