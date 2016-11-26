package org.spaceroots.mantissa.ode;

/**
 * This class implements the common part of all fixed step Runge-Kutta
 * integrators for Ordinary Differential Equations.

 * <p>These methods are explicit Runge-Kutta methods, their Butcher
 * arrays are as follows :
 * <pre>
 *    0  |
 *   c2  | a21
 *   c3  | a31  a32
 *   ... |        ...
 *   cs  | as1  as2  ...  ass-1
 *       |--------------------------
 *       |  b1   b2  ...   bs-1  bs
 * </pre>
 * </p>

 * <p>Some methods are qualified as <i>fsal</i> (first same as last)
 * methods. This means the last evaluation of the derivatives in one
 * step is the same as the first in the next step. Then, this
 * evaluation can be reused from one step to the next one and the cost
 * of such a method is really s-1 evaluations despite the method still
 * has s stages. This behaviour is true only for successful steps, if
 * the step is rejected after the error estimation phase, no
 * evaluation is saved. For an <i>fsal</i> method, we have cs = 1 and
 * asi = bi for all i.</p>

 * @see EulerIntegrator
 * @see ClassicalRungeKuttaIntegrator
 * @see GillIntegrator
 * @see MidpointIntegrator

 * @version $Id: RungeKuttaIntegrator.java 1719 2007-09-26 19:46:57Z luc $
 * @author L. Maisonobe

 */

public abstract class RungeKuttaIntegrator
  implements FirstOrderIntegrator {

  /** Simple constructor.
   * Build a Runge-Kutta integrator with the given
   * step. The default step handler does nothing.
   * @param fsal indicate that the method is an <i>fsal</i>
   * @param c time steps from Butcher array (without the first zero)
   * @param a internal weights from Butcher array (without the first empty row)
   * @param b external weights for the high order method from Butcher array
   * @param prototype prototype of the step interpolator to use
   * @param step integration step
   */
  protected RungeKuttaIntegrator(boolean fsal,
                                 double[] c, double[][] a, double[] b,
                                 RungeKuttaStepInterpolator prototype,
                                 double step) {
    this.fsal       = fsal;
    this.c          = c;
    this.a          = a;
    this.b          = b;
    this.prototype  = prototype;
    this.step       = step;
    handler         = DummyStepHandler.getInstance();
    switchesHandler = new SwitchingFunctionsHandler();
    resetInternalState();
  }

  /** Get the name of the method.
   * @return name of the method
   */
  public abstract String getName();

  /** Set the step handler for this integrator.
   * The handler will be called by the integrator for each accepted
   * step.
   * @param handler handler for the accepted steps
   */
  public void setStepHandler (StepHandler handler) {
    this.handler = handler;
  }

  /** Get the step handler for this integrator.
   * @return the step handler for this integrator
   */
  public StepHandler getStepHandler() {
    return handler;
  }

  /** Add a switching function to the integrator.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   */
  public void addSwitchingFunction(SwitchingFunction function,
                                   double maxCheckInterval,
                                   double convergence) {
    switchesHandler.add(function, maxCheckInterval, convergence);
  }

  public void integrate(FirstOrderDifferentialEquations equations,
                        double t0, double[] y0,
                        double t, double[] y)
  throws DerivativeException, IntegratorException {

    // sanity check
    if (equations.getDimension() != y0.length) {
      throw new IntegratorException("dimensions mismatch: ODE problem has dimension {0},"
                                    + " state vector has dimension {1}",
                                    new String[] {
                                      Integer.toString(equations.getDimension()),
                                      Integer.toString(y0.length)
                                    });
    }
    if (Math.abs(t - t0) <= 1.0e-12 * Math.max(Math.abs(t0), Math.abs(t))) {
      throw new IntegratorException("too small integration interval: length = {0}",
                                    new String[] {
                                      Double.toString(Math.abs(t - t0))
                                    });
    }
    
    boolean forward = (t > t0);

    // create some internal working arrays
    int stages = c.length + 1;
    if (y != y0) {
      System.arraycopy(y0, 0, y, 0, y0.length);
    }
    double[][] yDotK = new double[stages][];
    for (int i = 0; i < stages; ++i) {
      yDotK [i] = new double[y0.length];
    }
    double[] yTmp = new double[y0.length];

    // set up an interpolator sharing the integrator arrays
    AbstractStepInterpolator interpolator;
    if (handler.requiresDenseOutput() || (! switchesHandler.isEmpty())) {
      RungeKuttaStepInterpolator rki = (RungeKuttaStepInterpolator) prototype.clone();
      rki.reinitialize(equations, yTmp, yDotK, forward);
      interpolator = rki;
    } else {
      interpolator = new DummyStepInterpolator(yTmp, forward);
    }
    interpolator.storeTime(t0);

    // recompute the step
    long    nbStep    = Math.max(1l, Math.abs(Math.round((t - t0) / step)));
    boolean firstTime = true;
    boolean lastStep  = false;
    stepStart = t0;
    stepSize  = (t - t0) / nbStep;
    handler.reset();
    for (long i = 0; ! lastStep; ++i) {

      interpolator.shift();

      boolean needUpdate = false;
      for (boolean loop = true; loop;) {

        if (firstTime || !fsal) {
          // first stage
          equations.computeDerivatives(stepStart, y, yDotK[0]);
          firstTime = false;
        }

        // next stages
        for (int k = 1; k < stages; ++k) {

          for (int j = 0; j < y0.length; ++j) {
            double sum = a[k-1][0] * yDotK[0][j];
            for (int l = 1; l < k; ++l) {
              sum += a[k-1][l] * yDotK[l][j];
            }
            yTmp[j] = y[j] + stepSize * sum;
          }

          equations.computeDerivatives(stepStart + c[k-1] * stepSize, yTmp, yDotK[k]);

        }

        // estimate the state at the end of the step
        for (int j = 0; j < y0.length; ++j) {
          double sum    = b[0] * yDotK[0][j];
          for (int l = 1; l < stages; ++l) {
            sum    += b[l] * yDotK[l][j];
          }
          yTmp[j] = y[j] + stepSize * sum;
        }

        // Switching functions handling
        interpolator.storeTime(stepStart + stepSize);
        if (switchesHandler.evaluateStep(interpolator)) {
          needUpdate = true;
          stepSize = switchesHandler.getEventTime() - stepStart;
        } else {
          loop = false;
        }

      }

      // the step has been accepted
      stepStart += stepSize;
      System.arraycopy(yTmp, 0, y, 0, y0.length);
      switchesHandler.stepAccepted(stepStart, y);
      if (switchesHandler.stop()) {
        lastStep = true;
      } else {
        lastStep = (i == (nbStep - 1));
      }

      // provide the step data to the step handler
      interpolator.storeTime(stepStart);
      handler.handleStep(interpolator, lastStep);

      if (fsal) {
        // save the last evaluation for the next step
        System.arraycopy(yDotK[stages - 1], 0, yDotK[0], 0, y0.length);
      }

      if (switchesHandler.reset(stepStart, y) && ! lastStep) {
        // some switching function has triggered changes that
        // invalidate the derivatives, we need to recompute them
        equations.computeDerivatives(stepStart, y, yDotK[0]);
      }

      if (needUpdate) {
        // a switching function has changed the step
        // we need to recompute stepsize
        nbStep = Math.max(1l, Math.abs(Math.round((t - stepStart) / step)));
        stepSize = (t - stepStart) / nbStep;
        i = -1;
      }

    }

    resetInternalState();

  }

  public double getCurrentStepStart() {
    return stepStart;
  }

  public double getCurrentStepsize() {
    return stepSize;
  }

  /** Reset internal state to dummy values. */
  private void resetInternalState() {
    stepStart = Double.NaN;
    stepSize  = Double.NaN;
  }

  /** Indicator for <i>fsal</i> methods. */
  private final boolean fsal;

  /** Time steps from Butcher array (without the first zero). */
  private final double[] c;

  /** Internal weights from Butcher array (without the first empty row). */
  private final double[][] a;

  /** External weights for the high order method from Butcher array. */
  private final double[] b;

  /** Prototype of the step interpolator. */
  private final RungeKuttaStepInterpolator prototype;
                                         
  /** Integration step. */
  private final double step;

  /** Step handler. */
  private StepHandler handler;

  /** Switching functions handler. */
  protected SwitchingFunctionsHandler switchesHandler;

  /** Current step start time. */
  private double stepStart;

  /** Current stepsize. */
  private double stepSize;

}
