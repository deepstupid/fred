package org.spaceroots.mantissa.utilities;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class dispatch data between an array and several domain objects.

 * This class handles all the burden of mapping each domain object it
 * handles to a slice of a single array.

 * @see ArraySliceMappable

 * @version $Id: ArrayMapper.java 1709 2006-12-03 21:16:50Z luc $
 * @author L. Maisonobe

 */

public class ArrayMapper {

  /** Simple constructor.
   * Build an empty array mapper
   */
  public ArrayMapper() {
    domainObjects = new ArrayList();
    size          = 0;
    internalData  = null;
  }

  /** Simple constructor.
   * Build an array mapper managing one object. Other objects can be
   * added later using the {@link #manageMappable manageMappable}
   * method. This call is equivalent to build the mapper with the
   * default constructor and adding the object.
   * @param object domain object to handle
   */
  public ArrayMapper(ArraySliceMappable object) {

    domainObjects = new ArrayList();
    domainObjects.add(new ArrayMapperEntry(object, 0));

    size = object.getStateDimension();

    internalData = new double [size];

  }

  /** Take a new domain object into account.
   * @param object domain object to handle
   */
  public void manageMappable(ArraySliceMappable object) {

    domainObjects.add(new ArrayMapperEntry(object, size));

    size += object.getStateDimension();

    if (internalData != null) {
      internalData = new double [size];
    }

  }

  /** Get the data array.
   * @return copy of the data array
   */
  public double[] getDataArray() {
    if (internalData == null) {
      internalData = new double [size];
    }
    return internalData.clone();
  }

  /** Map data from the internal array to the domain objects.
   */
  public void updateObjects() {
    if (internalData == null) {
      internalData = new double [size];
    }
    updateObjects(internalData);
  }

  /** Map data from the specified array to the domain objects.
   * @param data flat array holding the data to dispatch
   */
  public void updateObjects(double[] data) {
    for (Object domainObject : domainObjects) {
      ArrayMapperEntry entry = (ArrayMapperEntry) domainObject;
      entry.object.mapStateFromArray(entry.offset, data);
    }
  }

  /** Map data from the domain objects to the internal array.
   */
  public void updateArray() {
    if (internalData == null) {
      internalData = new double [size];
    }
    updateArray(internalData);
  }

  /** Map data from the domain objects to the specified array.
   * @param data flat array where to put the data
   */
  public void updateArray(double[] data) {
    for (Iterator iter = domainObjects.iterator(); iter.hasNext();) {
      ArrayMapperEntry entry = (ArrayMapperEntry) iter.next();
      entry.object.mapStateToArray(entry.offset, data);
    }
  }

  /** Container for all handled objects. */
  private final ArrayList domainObjects;

  /** Total number of scalar elements handled.
   * (size of the array)
   */
  private int size;

  /** Flat array holding all data.
   * This is null as long as nobody uses it (lazy creation)
   */
  private double[] internalData;

}
