package org.spaceroots.mantissa.ode;

/** This class converts second order differential equations to first
 * order ones.

 * <p>This class is a wrapper around a {@link
 * SecondOrderDifferentialEquations} which allow to use a {@link
 * FirstOrderIntegrator} to integrate it.</p>

 * <p>The transformation is done by changing the n dimension state
 * vector to a 2n dimension vector, where the first n components are
 * the initial state variables and the n last components are their
 * first time derivative. The first time derivative of this state
 * vector then really contains both the first and second time
 * derivative of the initial state vector, which can be handled by the
 * underlying second order equations set.</p>

 * <p>One should be aware that the data is duplicated during the
 * transformation process and that for each call to {@link
 * #computeDerivatives computeDerivatives}, this wrapper does copy 4n
 * scalars : 2n before the call to {@link
 * SecondOrderDifferentialEquations#computeSecondDerivatives
 * computeSecondDerivatives} in order to dispatch the y state vector
 * into z and zDot, and 2n after the call to gather zDot and zDDot
 * into yDot. Since the underlying problem by itself perhaps also
 * needs to copy data and dispatch the arrays into domain objects,
 * this has an impact on both memory and CPU usage. The only way to
 * avoid this duplication is to perform the transformation at the
 * problem level, i.e. to implement the problem as a first order one
 * and then avoid using this class.</p>

 * @see FirstOrderIntegrator
 * @see FirstOrderDifferentialEquations
 * @see SecondOrderDifferentialEquations

 * @version $Id: FirstOrderConverter.java 1253 2002-06-20 17:47:07Z luc $
 * @author L. Maisonobe

 */

public class FirstOrderConverter
  implements FirstOrderDifferentialEquations {

  /** Simple constructor.
   * Build a converter around a second order equations set.
   * @param equations second order equations set to convert
   */
  public FirstOrderConverter (SecondOrderDifferentialEquations equations) {
      this.equations = equations;
      dimension      = equations.getDimension();
      z              = new double[dimension];
      zDot           = new double[dimension];
      zDDot          = new double[dimension];
  }

  public int getDimension() {
    return 2 * dimension;
  }

  public void computeDerivatives(double t, double[] y, double[] yDot)
  throws DerivativeException {

    // split the state vector in two
    System.arraycopy(y, 0,         z,    0, dimension);
    System.arraycopy(y, dimension, zDot, 0, dimension);

    // apply the underlying equations set
    equations.computeSecondDerivatives(t, z, zDot, zDDot);

    // build the result state derivative
    System.arraycopy(zDot,  0, yDot, 0,         dimension);
    System.arraycopy(zDDot, 0, yDot, dimension, dimension);
    
  }

  /** Underlying second order equations set. */
  private final SecondOrderDifferentialEquations equations;

  /** second order problem dimension. */
  private final int dimension;

  /** state vector. */
  private final double[] z;

  /** first time derivative of the state vector. */
  private final double[] zDot;

  /** second time derivative of the state vector. */
  private final double[] zDDot;

}
