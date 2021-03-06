package org.spaceroots.mantissa.ode;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/** This class represents an interpolator over the last step during an
 * ODE integration for Runge-Kutta and Runge-Kutta-Fehlberg
 * integrators.
 *
 * @see RungeKuttaIntegrator
 * @see RungeKuttaFehlbergIntegrator
 *
 * @version $Id: RungeKuttaStepInterpolator.java 1666 2005-12-15 16:37:55Z luc $
 * @author L. Maisonobe
 *
 */

abstract class RungeKuttaStepInterpolator
  extends AbstractStepInterpolator {

  /** Simple constructor.
   * This constructor builds an instance that is not usable yet, the
   * {@link #reinitialize} method should be called before using the
   * instance in order to initialize the internal arrays. This
   * constructor is used only in order to delay the initialization in
   * some cases. The {@link RungeKuttaIntegrator} and {@link
   * RungeKuttaFehlbergIntegrator} classes uses the prototyping design
   * pattern to create the step interpolators by cloning an
   * uninitialized model and latter initializing the copy.
   */
  protected RungeKuttaStepInterpolator() {
    super();
    yDotK     = null;
    equations = null;
  }

  /** Copy constructor.

  * <p>The copied interpolator should have been finalized before the
  * copy, otherwise the copy will not be able to perform correctly any
  * interpolation and will throw a {@link NullPointerException}
  * later. Since we don't want this constructor to throw the
  * exceptions finalization may involve and since we don't want this
  * method to modify the state of the copied interpolator,
  * finalization is <strong>not</strong> done automatically, it
  * remains under user control.</p>

  * <p>The copy is a deep copy: its arrays are separated from the
  * original arrays of the instance.</p>

  * @param interpolator interpolator to copy from.

  */
  public RungeKuttaStepInterpolator(RungeKuttaStepInterpolator interpolator) {

    super(interpolator);

    if (interpolator.currentState != null) {
      int dimension = currentState.length;

      yDotK = new double[interpolator.yDotK.length][];
      for (int k = 0; k < interpolator.yDotK.length; ++k) {
        yDotK[k] = new double[dimension];
        System.arraycopy(interpolator.yDotK[k], 0,
                         yDotK[k], 0, dimension);
      }

    } else {
      yDotK = null;
    }

    // we cannot keep any reference to the equations in the copy
    // the interpolator should have been finalized before
    equations = null;

  }

  /** Reinitialize the instance
   * <p>Some Runge-Kutta integrators need fewer functions evaluations
   * than their counterpart step interpolators. So the interpolator
   * should perform the last evaluations they need by themselves. The
   * {@link RungeKuttaIntegrator RungeKuttaIntegrator} and {@link
   * RungeKuttaFehlbergIntegrator RungeKuttaFehlbergIntegrator}
   * abstract classes call this method in order to let the step
   * interpolator perform the evaluations it needs. These evaluations
   * will be performed during the call to <code>doFinalize</code> if
   * any, i.e. only if the step handler either calls the {@link
   * AbstractStepInterpolator#finalizeStep finalizeStep} method or the
   * {@link AbstractStepInterpolator#getInterpolatedState
   * getInterpolatedState} method (for an interpolator which needs a
   * finalization) or if it clones the step interpolator.</p>
   * @param equations set of differential equations being integrated
   * @param y reference to the integrator array holding the state at
   * the end of the step
   * @param yDotK reference to the integrator array holding all the
   * intermediate slopes
   * @param forward integration direction indicator
   */
  public void reinitialize(FirstOrderDifferentialEquations equations,
                           double[] y, double[][] yDotK, boolean forward) {
    reinitialize(y, forward);
    this.yDotK = yDotK;
    this.equations = equations;
  }

  /** Save the state of the instance.
   * @param out stream where to save the state
   * @exception IOException in case of write error
   */
  public void writeExternal(ObjectOutput out)
    throws IOException {

    // save the state of the base class
    writeBaseExternal(out);

    // save the local attributes
    out.writeInt(yDotK.length);
    for (int k = 0; k < yDotK.length; ++k) {
      for (int i = 0; i < currentState.length; ++i) {
        out.writeDouble(yDotK[k][i]);
      }
    }

    // we do not save any reference to the equations

  }

  /** Read the state of the instance.
   * @param in stream where to read the state from
   * @exception IOException in case of read error
   */
  public void readExternal(ObjectInput in)
    throws IOException {

    // read the base class 
    double t = readBaseExternal(in);

    // read the local attributes
    int kMax = in.readInt();
    yDotK = new double[kMax][];
    for (int k = 0; k < kMax; ++k) {
      yDotK[k] = new double[currentState.length];
      for (int i = 0; i < currentState.length; ++i) {
        yDotK[k][i] = in.readDouble();
      }
    }

    equations = null;

    try {
      // we can now set the interpolated time and state
      setInterpolatedTime(t);
    } catch (DerivativeException e) {
      IOException ioe = new IOException(e);
        throw ioe;
    }

  }

  /** Slopes at the intermediate points */
  protected double[][] yDotK;

  /** Reference to the differential equations beeing integrated. */
  protected FirstOrderDifferentialEquations equations;

}
