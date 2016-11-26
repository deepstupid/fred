package org.spaceroots.mantissa.ode;

/** This interface represents a first order differential equations set.
 *
 * <p>This interface should be implemented by all real first order
 * differential equation problems before they can be handled by the
 * integrators {@link FirstOrderIntegrator#integrate} method.</p>
 *
 * <p>A first order differential equations problem, as seen by an
 * integrator is the time derivative <code>dY/dt</code> of a state
 * vector <code>Y</code>, both being one dimensional arrays. From the
 * integrator point of view, this derivative depends only on the
 * current time <code>t</code> and on the state vector
 * <code>Y</code>.</p>
 *
 * <p>For real problems, the derivative depends also on parameters
 * that do not belong to the state vector (dynamical model constants
 * for example). These constants are completely outside of the scope
 * of this interface, the classes that implement it are allowed to
 * handle them as they want.</p>
 *
 * @see FirstOrderIntegrator
 * @see FirstOrderConverter
 * @see SecondOrderDifferentialEquations
 * @see org.spaceroots.mantissa.utilities.ArraySliceMappable
 *
 * @version $Id: FirstOrderDifferentialEquations.java 1719 2007-09-26 19:46:57Z luc $
 * @author L. Maisonobe
 *
 */

public interface FirstOrderDifferentialEquations {
    
    /** Get the dimension of the problem.
     * @return dimension of the problem
     */
    int getDimension();
    
    /** Get the current time derivative of the state vector.
     * @param t current value of the independent <I>time</I> variable
     * @param y array containing the current value of the state vector
     * @param yDot placeholder array where to put the time derivative of the state vector
     * @throws DerivativeException this exception is propagated to the caller if the
     * underlying user function triggers one
     */
    void computeDerivatives(double t, double[] y, double[] yDot)
    throws DerivativeException;
    
}
