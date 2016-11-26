package org.spaceroots.mantissa.fitting;

import org.spaceroots.mantissa.estimation.EstimatedParameter;
import org.spaceroots.mantissa.estimation.Estimator;
import org.spaceroots.mantissa.estimation.GaussNewtonEstimator;

/** This class implements a curve fitting specialized for polynomials.

 * <p>Polynomial fitting is a very simple case of curve fitting. The
 * estimated coefficients are the polynom coefficients. They are
 * searched by a least square estimator.</p>

 * <p>This class <emph>is by no means optimized</emph>, neither in
 * space nor in time performance.</p>

 * @see PolynomialCoefficient

 * @version $Id: PolynomialFitter.java 1709 2006-12-03 21:16:50Z luc $
 * @author L. Maisonobe

 */

public class PolynomialFitter
  extends AbstractCurveFitter {

  /** Simple constructor.

   * <p>The polynomial fitter built this way are complete polynoms,
   * ie. a n-degree polynom has n+1 coefficients. In order to build
   * fitter for sparse polynoms (for example <code>a x^20 - b
   * x^30</code>, on should first build the coefficients array and
   * provide it to {@link
   * #PolynomialFitter(PolynomialCoefficient[], int, double, double,
   * double)}.</p>
   * @param degree maximal degree of the polynom
   * @param estimator estimator to use for the fitting
   */
  public PolynomialFitter(int degree, Estimator estimator) {
    super(degree + 1, estimator);
    for (int i = 0; i < coefficients.length; ++i) {
      coefficients[i] = new PolynomialCoefficient(i);
    }
  }

  /** Simple constructor.

   * <p>This constructor can be used either when a first estimate of
   * the coefficients is already known (which is of little interest
   * because the fit cost is the same whether a first guess is known or
   * not) or when one needs to handle sparse polynoms like <code>a
   * x^20 - b x^30</code>.</p>

   * @param coefficients first estimate of the coefficients.
   * A reference to this array is hold by the newly created
   * object. Its elements will be adjusted during the fitting process
   * and they will be set to the adjusted coefficients at the end.
   * @param estimator estimator to use for the fitting
   */
  public PolynomialFitter(PolynomialCoefficient[] coefficients,
                          Estimator estimator) {
    super(coefficients, estimator);
  }

  /** Simple constructor.

   * <p>The polynomial fitter built this way are complete polynoms,
   * ie. a n-degree polynom has n+1 coefficients. In order to build
   * fitter for sparse polynoms (for example <code>a x^20 - b
   * x^30</code>, on should first build the coefficients array and
   * provide it to {@link
   * #PolynomialFitter(PolynomialCoefficient[], int, double, double,
   * double)}.</p>
   * @param degree maximal degree of the polynom
   * @param maxIterations maximum number of iterations allowed
   * @param convergence criterion threshold below which we do not need
   * to improve the criterion anymore
   * @param steadyStateThreshold steady state detection threshold, the
   * problem has reached a steady state (read converged) if
   * <code>Math.abs (Jn - Jn-1) < Jn * convergence</code>, where
   * <code>Jn</code> and <code>Jn-1</code> are the current and
   * preceding criterion value (square sum of the weighted residuals
   * of considered measurements).
   * @param epsilon threshold under which the matrix of the linearized
   * problem is considered singular (see {@link
   * org.spaceroots.mantissa.linalg.SquareMatrix#solve(
   * org.spaceroots.mantissa.linalg.Matrix,double) SquareMatrix.solve}).
 
   * @deprecated replaced by {@link #PolynomialFitter(int,Estimator)}
   * as of version 7.0
   */
  public PolynomialFitter(int degree,
                          int maxIterations, double convergence,
                          double steadyStateThreshold, double epsilon) {
    this(degree,
         new GaussNewtonEstimator(maxIterations, steadyStateThreshold,
                                  convergence, epsilon));
  }

  /** Simple constructor.

   * <p>This constructor can be used either when a first estimate of
   * the coefficients is already known (which is of little interest
   * because the fit cost is the same whether a first guess is known or
   * not) or when one needs to handle sparse polynoms like <code>a
   * x^20 - b x^30</code>.</p>

   * @param coefficients first estimate of the coefficients.
   * A reference to this array is hold by the newly created
   * object. Its elements will be adjusted during the fitting process
   * and they will be set to the adjusted coefficients at the end.
   * @param maxIterations maximum number of iterations allowed
   * @param convergence criterion threshold below which we do not need
   * to improve the criterion anymore
   * @param steadyStateThreshold steady state detection threshold, the
   * problem has reached a steady state (read converged) if
   * <code>Math.abs (Jn - Jn-1) < Jn * convergence</code>, where
   * <code>Jn</code> and <code>Jn-1</code> are the current and
   * preceding criterion value (square sum of the weighted residuals
   * of considered measurements).
   * @param epsilon threshold under which the matrix of the linearized
   * problem is considered singular (see {@link
   * org.spaceroots.mantissa.linalg.SquareMatrix#solve(
   * org.spaceroots.mantissa.linalg.Matrix,double) SquareMatrix.solve}).

   * @deprecated replaced by {@link #PolynomialFitter(PolynomialCoefficient[],
   * Estimator)} as of version 7.0
   */
  public PolynomialFitter(PolynomialCoefficient[] coefficients,
                          int maxIterations, double convergence,
                          double steadyStateThreshold, double epsilon) {
    this(coefficients,
         new GaussNewtonEstimator(maxIterations, steadyStateThreshold,
                                  convergence, epsilon));
  }

  /** Get the value of the function at x according to the current parameters value.
   * @param x abscissa at which the theoretical value is requested
   * @return theoretical value at x
   */
  public double valueAt(double x) {
    double y = coefficients[coefficients.length - 1].getEstimate();
    for (int i = coefficients.length - 2; i >= 0; --i) {
      y = y * x + coefficients[i].getEstimate();
    }
    return y;
  }

  /** Get the derivative of the function at x with respect to parameter p.
   * @param x abscissa at which the partial derivative is requested
   * @param p parameter with respect to which the derivative is requested
   * @return partial derivative
   */
  public double partial(double x, EstimatedParameter p) {
    if (p instanceof PolynomialCoefficient) {
      return Math.pow(x, ((PolynomialCoefficient) p).degree);
    }
    throw new RuntimeException("internal error");
  }

  private static final long serialVersionUID = -744904084649890769L;

}
