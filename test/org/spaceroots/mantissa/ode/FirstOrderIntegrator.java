package org.spaceroots.mantissa.ode;

/** This interface represents a first order integrator for
 * differential equations.

 * <p>The classes which are devoted to solve first order differential
 * equations should implement this interface. The problems which can
 * be handled should implement the {@link
 * FirstOrderDifferentialEquations} interface.</p>

 * @see FirstOrderDifferentialEquations
 * @see StepHandler
 * @see SwitchingFunction

 * @version $Id: FirstOrderIntegrator.java 1719 2007-09-26 19:46:57Z luc $
 * @author L. Maisonobe

 */

public interface FirstOrderIntegrator {

  /** Get the name of the method.
   * @return name of the method
   */
  public String getName();

  /** Set the step handler for this integrator.
   * The handler will be called by the integrator for each accepted
   * step.
   * @param handler handler for the accepted steps
   */
  public void setStepHandler (StepHandler handler);

  /** Get the step handler for this integrator.
   * @return the step handler for this integrator
   */
  public StepHandler getStepHandler();

  /** Add a switching function to the integrator.
   * @param function switching function
   * @param maxCheckInterval maximal time interval between switching
   * function checks (this interval prevents missing sign changes in
   * case the integration steps becomes very large)
   * @param convergence convergence threshold in the event time search
   */
  public void addSwitchingFunction(SwitchingFunction function,
                                   double maxCheckInterval,
                                   double convergence);

  /** Integrate the differential equations up to the given time.
   * <p>This method solves an Initial Value Problem (IVP).</p>
   * <p>Since this method stores some internal state variables made
   * available in its public interface during integration ({@link
   * #getCurrentStepsize()}), it is <em>not</em> thread-safe.</p>
   * @param equations differential equations to integrate
   * @param t0 initial time
   * @param y0 initial value of the state vector at t0
   * @param t target time for the integration
   * (can be set to a value smaller than <code>t0</code> for backward integration)
   * @param y placeholder where to put the state vector at each successful
   *  step (and hence at the end of integration), can be the same object as y0
   * @throws IntegratorException if the integrator cannot perform integration
   * @throws DerivativeException this exception is propagated to the caller if
   * the underlying user function triggers one
   */
  public void integrate (FirstOrderDifferentialEquations equations,
                         double t0, double[] y0,
                         double t, double[] y)
    throws DerivativeException, IntegratorException;

  /** Get the current value of the step start time t<sub>i</sub>.
   * <p>This method can be called during integration (typically by
   * the object implementing the {@link FirstOrderDifferentialEquations
   * differential equations} problem) if the value of the current step that
   * is attempted is needed.</p>
   * <p>The result is undefined if the method is called outside of
   * calls to {@link #integrate}</p>
   * @return current value of the step start time t<sub>i</sub>
   */
  public double getCurrentStepStart();

  /** Get the current value of the integration stepsize.
   * <p>This method can be called during integration (typically by
   * the object implementing the {@link FirstOrderDifferentialEquations
   * differential equations} problem) if the value of the current stepsize
   * that is tried is needed.</p>
   * <p>The result is undefined if the method is called outside of
   * calls to {@link #integrate}</p>
   * @return current value of the stepsize
   */
  public double getCurrentStepsize();

}