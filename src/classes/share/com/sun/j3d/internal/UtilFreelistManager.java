/*
 * $RCSfile$
 *
 * Copyright (c) 2004 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision$
 * $Date$
 * $State$
 */

package com.sun.j3d.internal;


public class UtilFreelistManager {

    private static final boolean DEBUG = false;

    // constants that represent the freelists managed by the Manager
    public static final int VECTOR3D = 0;
    public static final int POINT3D = 1;
    public static final int PICKRESULT = 2;
    public static final int MAXINT = 2;
    
    // what list we are going to shrink next
    private static int currlist = 0;

    // the freelists managed by the manager
    public static UtilMemoryFreelist vector3dFreelist = new UtilMemoryFreelist("javax.vecmath.Vector3d");
    public static UtilMemoryFreelist point3dFreelist = new UtilMemoryFreelist("javax.vecmath.Point3d");
    public static UtilMemoryFreelist pickResultFreelist = new UtilMemoryFreelist("com.sun.j3d.utils.picking.PickResult");


//     static MemoryFreeList[] freelist = new MemoryFreeList[MAXINT+1];

//     static void createFreeLists() {
// 	freelist[VECTOR3D] = new MemoryFreeList("javax.vecmath.Vector3d");
// 	freelist[POINT3D] = new MemoryFreeList("javax.vecmath.Point3d");
//     }


//     // see if the current list can be shrunk
//     static void manageLists() {
// // 	System.out.println("manageLists");
// 	if (freelist[currlist] != null) {
// 	    freelist[currlist].shrink();
// 	}
	
// 	currlist++;
// 	if (currlist > MAXINT) currlist = 0;
//     }

//     // return the freelist specified by the list param
//     static MemoryFreeList getFreeList(int list) {
// 	if (list < 0 || list > MAXINT) {
// 	    if (DEBUG) System.out.println("illegal list");
// 	    return null;
// 	}
// 	else {
// 	    return freelist[list];
// 	}
//     }

    
}
