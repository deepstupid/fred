package com.onionnetworks.fec;

/**
 * This is the abstract class is subclassed in order to plug in new FEC 
 * implementations.  If you wish to use the default implementation defined by 
 * the property "com.onionnetworks.com.onionnetworks.fec.defaultcodefactoryclass" you should
 * simply call:
 *
 * <code>
 *   FECCodeFactory factory = FECCodeFactory.getDefault();
 * </code>
 *
 * (c) Copyright 2001 Onion Networks
 * (c) Copyright 2000 OpenCola
 *
 * @author Justin F. Chapweske (justin@chapweske.com)
 */
public abstract class FECCodeFactory {

    public static final FECCodeFactory the = new DefaultFECCodeFactory();

//    protected FECCodeFactory() {}

    /**
     * @return An FECCode for the appropriate <code>k</code> and <code>n</code>
     * values.
     */
    public abstract com.onionnetworks.fec.FECCode createFECCode(int k, int n);

    //    /**
//     * @return The default FECCodeFactory which is defined by the property
//     * "com.onionnetworks.com.onionnetworks.fec.defaultcodefactoryclass".  If this property is
//     * not defined then DefaultFECCodeFactory will be used by default.
//     */
//    public static FECCodeFactory getDefault() {
//        if (def == null) {
////            try {
////                Class clazz = Class.forName
////                    (System.getProperty
////                     ("com.onionnetworks.fec.defaultcodefactoryclass",
////                      "com.onionnetworks.fec.DefaultFECCodeFactory"));
////                def = (FECCodeFactory) clazz.newInstance();
////            } catch (Exception e) {
////                // krunky structure, but the easiest way to deal with the
////                // exception.
//                def = new DefaultFECCodeFactory();
////            }
//        }
//        return def;
//    }
}
