package org.spaceroots.mantissa.quadrature.vectorial;

import org.spaceroots.mantissa.functions.ExhaustedSampleException;
import org.spaceroots.mantissa.functions.FunctionException;
import org.spaceroots.mantissa.functions.vectorial.SampledFunctionIterator;

/** This interface represents an integrator for vectorial samples.

 * <p>The classes which are devoted to integrate vectorial samples
 * should implement this interface.</p>

 * @see org.spaceroots.mantissa.functions.vectorial.SampledFunctionIterator
 * @see ComputableFunctionIntegrator

 * @version $Id: SampledFunctionIntegrator.java 1231 2002-03-12 20:07:04Z luc $
 * @author L. Maisonobe

 */

public interface SampledFunctionIntegrator {

  /** Integrate a sample over its overall range
   * @param iter iterator over the sample to integrate
   * @return value of the integral over the sample range
   * @exception ExhaustedSampleException if the sample does not have
   * enough points for the integration scheme
   * @exception FunctionException if the underlying sampled function throws one
   */
  double[] integrate(SampledFunctionIterator iter)
    throws ExhaustedSampleException, FunctionException;

}
