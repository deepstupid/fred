package org.spaceroots.mantissa.functions.scalar;

import org.spaceroots.mantissa.functions.FunctionException;

import java.io.Serializable;

/** This class is a wrapper allowing to sample a
 * {@link ComputableFunction}.

 * <p>The sample produced is a regular sample. It can be specified by
 * several means :
 * <ul>
 *   <li> from an initial point a step and a number of points</li>
 *   <li> from a range and a number of points</li>
 *   <li> from a range and a step between points.</li>
 * </ul>
 * In the latter case, the step can optionaly be adjusted in order to
 * have the last point exactly at the upper bound of the range.</p>

 * <p>The sample points are computed on demand, they are not
 * stored. This allow to use this method for very large sample with
 * little memory overhead. The drawback is that if the same sample
 * points are going to be requested several times, they will be
 * recomputed each time. In this case, the user should consider
 * storing the points by some other means.</p>

 * @see ComputableFunction

 * @version $Id: ComputableFunctionSampler.java 1666 2005-12-15 16:37:55Z luc $
 * @author L. Maisonobe

 */
public class ComputableFunctionSampler
  implements SampledFunction, Serializable {

  /** Underlying computable function. */
  private final ComputableFunction function;

  /** Beginning abscissa. */
  private final double begin;

  /** Step between points. */
  private final double step;

  /** Total number of points. */
  private final int n;

  /**
   * Constructor.

   * Build a sample from an {@link ComputableFunction}. Beware of the
   * classical off-by-one problem !  If you want to have a sample like
   * this : 0.0, 0.1, 0.2 ..., 1.0, then you should specify step = 0.1
   * and n = 11 (not n = 10).

   * @param begin beginning of the range (will be the abscissa of the
   * first point)
   * @param step step between points
   * @param n number of points

   */
  public ComputableFunctionSampler(ComputableFunction function,
                                   double begin, double step, int n) {
    this.function = function;
    this.begin    = begin;
    this.step     = step;
    this.n        = n;
  }

  /**
   * Constructor.
   * Build a sample from an {@link ComputableFunction}.

   * @param range abscissa range (from <code>range [0]</code> to
   * <code>range [1]</code>)
   * @param n number of points
   */
  public ComputableFunctionSampler(ComputableFunction function,
                                   double[] range, int n) {
    this.function = function;
    begin         = range[0];
    step          = (range[1] - range[0]) / (n - 1);
    this.n        = n;
  }

  /**
   * Constructor.
   * Build a sample from an {@link ComputableFunction}.

   * @param range abscissa range (from <code>range [0]</code> to
   * <code>range [1]</code>)
   * @param step step between points
   * @param adjustStep if true, the step is reduced in order to have
   * the last point of the sample exactly at <code>range [1]</code>,
   * if false the last point will be between <code>range [1] -
   * step</code> and <code>range [1]</code> */
  public ComputableFunctionSampler(ComputableFunction function,
                                   double[] range, double step,
                                   boolean adjustStep) {
    this.function = function;
    begin         = range [0];
    if (adjustStep) {
      n         = (int) Math.ceil((range[1] - range[0]) / step);
      this.step = (range[1] - range[0]) / (n - 1);
    } else {
      n         = (int) Math.floor((range[1] - range[0]) / step);
      this.step = step;
    }
  }

  public int size() {
    return n;
  }

  public ScalarValuedPair samplePointAt(int index)
    throws ArrayIndexOutOfBoundsException, FunctionException {

    if (index < 0 || index >= n) {
      throw new ArrayIndexOutOfBoundsException();
    }

    double x = begin + index * step;
    return new ScalarValuedPair(x, function.valueAt(x));

  }

  private static final long serialVersionUID = -5127043442851795719L;

}
