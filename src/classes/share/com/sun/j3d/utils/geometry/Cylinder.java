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

package com.sun.j3d.utils.geometry;

import com.sun.j3d.utils.geometry.*;
import java.io.*;
import java.util.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * Cylinder is a geometry primitive defined with a radius and a height.
 * It is a capped cylinder centered at the origin with its central axis
 * aligned along the Y-axis. 
 * <p>
 * When a texture is applied to a cylinder, the texture is applied to the
 * caps and the body different. A texture is mapped CCW from the back of the
 * body. The top and bottom caps are mapped such that the texture appears
 * front facing when the caps are rotated 90 degrees toward the viewer.
 * <p>
 * By default all primitives with the same parameters share their
 * geometry (e.g., you can have 50 shperes in your scene, but the
 * geometry is stored only once). A change to one primitive will
 * effect all shared nodes.  Another implication of this
 * implementation is that the capabilities of the geometry are shared,
 * and once one of the shared nodes is live, the capabilities cannot
 * be set.  Use the GEOMETRY_NOT_SHARED flag if you do not wish to
 * share geometry among primitives with the same parameters.
 */

public class Cylinder extends Primitive{
    float radius, height;
    int xdivisions, ydivisions;

    static final int MID_REZ_DIV_X = 15;
    static final int MID_REZ_DIV_Y = 1;

    /**
     * Designates the body of the cylinder.  Used by <code>getShape</code>.
     *
     * @see Cylinder#getShape
     */
    public static final int BODY = 0;

    /**
     * Designates the top end-cap of the cylinder.
     * Used by <code>getShape</code>.
     *
     * @see Cylinder#getShape
     */
    public static final int TOP = 1;

    /**
     * Designates the bottom end-cap of the cylinder.
     * Used by <code>getShape</code>.
     *
     * @see Cylinder#getShape
     */
    public static final int BOTTOM = 2;

    /**  
     *   Constructs a default cylinder of radius of 1.0 and height
     *   of 2.0. Normals are generated by default, texture 
     *   coordinates are not. Resolution defaults to 15 divisions 
     *   along X axis and  1 along the Y axis.
     */
    public Cylinder() {
	this(1.0f, 2.0f, GENERATE_NORMALS, MID_REZ_DIV_X, MID_REZ_DIV_Y, null);
    }

    /**
     *   Constructs a default cylinder of a given radius and height.
     *   Normals are generated by default, texture coordinates are not.
     *   @param radius Radius
     *   @param height Height
     */
    public Cylinder (float radius, float height) {
	this(radius, height, GENERATE_NORMALS, MID_REZ_DIV_X, MID_REZ_DIV_Y,
	    null);
    }

    /**
     *   Constructs a default cylinder of a given radius, height, and
     *   appearance. Normals are generated by default, texture 
     *   coordinates are not.
     *   @param radius Radius
     *   @param height Height
     *   @param ap Appearance
     */
    public Cylinder (float radius, float height, Appearance ap)
    {
	this(radius, height, GENERATE_NORMALS, MID_REZ_DIV_X, MID_REZ_DIV_Y,
	    ap);
    }

    /**
     *
     *   Constructs a default cylinder of a given radius, height, 
     *   primitive flags and appearance.
     *   @param radius Radius
     *   @param height Height
     *   @param primflags Flags
     *   @param ap Appearance
     */
    public Cylinder (float radius, float height, int primflags, Appearance ap)
    {
	this(radius, height, primflags, MID_REZ_DIV_X, MID_REZ_DIV_Y, ap);
    }

    /**
     *  Obtains the Shape3D node associated with a given part of the cylinder.
     *  This allows users to modify the appearance or geometry
     *  of individual parts. 
     * @param partId The part to return (BODY, TOP, or BOTTOM).
     * @return The Shape3D object associated with the partID.  If an
     * invalid partId is passed in, null is returned.
     */
    public Shape3D getShape(int partId){
	if (partId > BOTTOM || partId < BODY) return null;
	return (Shape3D)getChild(partId);
    }

    /** Sets appearance of the cylinder. This will set each part of the
     *  cylinder (TOP,BOTTOM,BODY) to the same appearance. To set each 
     *  part's appearance separately, use getShape(partId) to get the
     *  individual shape and call shape.setAppearance(ap). 
     */
    public void setAppearance(Appearance ap) {
	((Shape3D)getChild(BODY)).setAppearance(ap);
	((Shape3D)getChild(TOP)).setAppearance(ap);
	((Shape3D)getChild(BOTTOM)).setAppearance(ap);
    }

    /**
     * Gets the appearance of the specified part of the cylinder.
     *
     * @param partId identifier for a given subpart of the cylinder
     *
     * @return The appearance object associated with the partID.  If an
     * invalid partId is passed in, null is returned.
     *
     * @since Java 3D 1.2.1
     */
    public Appearance getAppearance(int partId) {
	if (partId > BOTTOM || partId < BODY) return null;
	return getShape(partId).getAppearance();
    }

    
    /**  
     *   Constructs a customized cylinder of a given radius, height,
     *   resolution (X and Y dimensions), and appearance. The 
     *   resolution is defined in terms of number of subdivisions
     *   along the object's X axis (width) and Y axis (height). More divisions
     *   lead to more finely tesselated objects. 
     *   @param radius Radius
     *   @param height Height
     *   @param xdivision Number of divisions along X direction.
     *   @param ydivision Number of divisions along height of cylinder.
     *   @param primflags Primitive flags.
     *   @param ap Appearance
     */
    public Cylinder(float radius, float height, int primflags, 
		    int xdivision, int ydivision, Appearance ap) {
      super();

      this.radius = radius;
      this.height = height;
      this.xdivisions = xdivision;
      this.ydivisions = ydivision;
      flags = primflags;
      boolean outside = (flags & GENERATE_NORMALS_INWARD) == 0;
      // Create many body of the cylinder.
      Quadrics q = new Quadrics();
      GeomBuffer gbuf = null;
      Shape3D shape[] = new Shape3D[3];

      GeomBuffer cache = getCachedGeometry(Primitive.CYLINDER,
					      (float)BODY, radius, height,
					      xdivision, ydivision, primflags);
      if (cache != null){
// 	  System.out.println("using cached geometry");
	shape[BODY] = new Shape3D(cache.getComputedGeometry());
	numVerts += cache.getNumVerts();
	numTris += cache.getNumTris();
      }
      else {
	  gbuf = q.cylinder((double)height, (double)radius,
			    xdivision, ydivision, outside);
	  shape[BODY] = new Shape3D(gbuf.getGeom(flags));
	  numVerts += gbuf.getNumVerts();
	  numTris += gbuf.getNumTris();
	  if ((primflags & Primitive.GEOMETRY_NOT_SHARED) == 0)
	      cacheGeometry(Primitive.CYLINDER,
			    (float)BODY, radius, height,
			    xdivision, ydivision, primflags, gbuf);
      }

      if ((flags & ENABLE_APPEARANCE_MODIFY) != 0) {
	  (shape[BODY]).setCapability(Shape3D.ALLOW_APPEARANCE_READ);
	  (shape[BODY]).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      }

      if ((flags & ENABLE_GEOMETRY_PICKING) != 0) {
          (shape[BODY]).setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      }

      this.addChild(shape[BODY]);

      // Create top of cylinder
      cache = getCachedGeometry(Primitive.TOP_DISK, radius, radius,
				height/2.0f, xdivision, xdivision, primflags);
      if (cache != null) {
// 	  System.out.println("using cached top");
	  shape[TOP] = new Shape3D(cache.getComputedGeometry());
	  numVerts += cache.getNumVerts();
	  numTris += cache.getNumTris();
      }
      else {
	  gbuf = q.disk((double)radius, xdivision, height/2.0,
			outside);
	  shape[TOP] = new Shape3D(gbuf.getGeom(flags));
	  numVerts += gbuf.getNumVerts();
	  numTris += gbuf.getNumTris();
	  if ((primflags & Primitive.GEOMETRY_NOT_SHARED) == 0) {
	      cacheGeometry(Primitive.TOP_DISK, radius, radius,
			    height/2.0f, xdivision, xdivision,
			    primflags, gbuf);
	  }
      }

      if ((flags & ENABLE_APPEARANCE_MODIFY) != 0) {
	  (shape[TOP]).setCapability(Shape3D.ALLOW_APPEARANCE_READ);
	  (shape[TOP]).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      }

      if ((flags & ENABLE_GEOMETRY_PICKING) != 0) {
          (shape[TOP]).setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      }
      
      this.addChild(shape[TOP]);

      // Create bottom
      cache = getCachedGeometry(Primitive.BOTTOM_DISK, radius, radius,
				-height/2.0f, xdivision, xdivision,
				primflags);
      if (cache != null) {
// 	  System.out.println("using cached bottom");
	  shape[BOTTOM] = new Shape3D(cache.getComputedGeometry());
	  numVerts += cache.getNumVerts();
	  numTris += cache.getNumTris();
      }
      else {
	  gbuf = q.disk((double)radius, xdivision, -height/2.0, !outside);
	  shape[BOTTOM] = new Shape3D(gbuf.getGeom(flags));
	  numVerts += gbuf.getNumVerts();
	  numTris += gbuf.getNumTris();
	  if ((primflags & Primitive.GEOMETRY_NOT_SHARED) == 0) {
	      cacheGeometry(Primitive.BOTTOM_DISK, radius, radius,
			    -height/2.0f, xdivision, xdivision,
			    primflags, gbuf);
	  }
      }

      if ((flags & ENABLE_APPEARANCE_MODIFY) != 0) {
	  (shape[BOTTOM]).setCapability(Shape3D.ALLOW_APPEARANCE_READ);
	  (shape[BOTTOM]).setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
      }

      if ((flags & ENABLE_GEOMETRY_PICKING) != 0) {
          (shape[BOTTOM]).setCapability(Shape3D.ALLOW_GEOMETRY_READ);
      }

      this.addChild(shape[BOTTOM]);

      // Set Appearance
      if (ap == null){
	setAppearance();
      }
      else setAppearance(ap);
    }

    /**
     * Used to create a new instance of the node.  This routine is called
     * by <code>cloneTree</code> to duplicate the current node.
     * <code>cloneNode</code> should be overridden by any user subclassed
     * objects.  All subclasses must have their <code>cloneNode</code>
     * method consist of the following lines:
     * <P><blockquote><pre>
     *     public Node cloneNode(boolean forceDuplicate) {
     *         UserSubClass usc = new UserSubClass();
     *         usc.duplicateNode(this, forceDuplicate);
     *         return usc;
     *     }
     * </pre></blockquote>
     * @param forceDuplicate when set to <code>true</code>, causes the
     *  <code>duplicateOnCloneTree</code> flag to be ignored.  When
     *  <code>false</code>, the value of each node's
     *  <code>duplicateOnCloneTree</code> variable determines whether
     *  NodeComponent data is duplicated or copied.
     *
     * @see Node#cloneTree
     * @see Node#duplicateNode
     * @see NodeComponent#setDuplicateOnCloneTree
     */
    public Node cloneNode(boolean forceDuplicate) {
        Cylinder c = new Cylinder(radius, height, flags, xdivisions,
                                  ydivisions, getAppearance());
        c.duplicateNode(this, forceDuplicate);
        return c;
    }

    /**
     * Copies all node information from <code>originalNode</code> into
     * the current node.  This method is called from the
     * <code>cloneNode</code> method which is, in turn, called by the
     * <code>cloneTree</code> method.
     * <P>
     * For any <i>NodeComponent</i> objects
     * contained by the object being duplicated, each <i>NodeComponent</i>
     * object's <code>duplicateOnCloneTree</code> value is used to determine
     * whether the <i>NodeComponent</i> should be duplicated in the new node
     * or if just a reference to the current node should be placed in the
     * new node.  This flag can be overridden by setting the
     * <code>forceDuplicate</code> parameter in the <code>cloneTree</code>
     * method to <code>true</code>.
     *
     * @param originalNode the original node to duplicate.
     * @param forceDuplicate when set to <code>true</code>, causes the
     *  <code>duplicateOnCloneTree</code> flag to be ignored.  When
     *  <code>false</code>, the value of each node's
     *  <code>duplicateOnCloneTree</code> variable determines whether
     *  NodeComponent data is duplicated or copied.
     *
     * @see Node#cloneTree
     * @see Node#cloneNode
     * @see NodeComponent#setDuplicateOnCloneTree
     */
    public void duplicateNode(Node originalNode, boolean forceDuplicate) {
        super.duplicateNode(originalNode, forceDuplicate);
    }

    /**
     * Returns the radius of the cylinder
     *
     * @since Java 3D 1.2.1
     */
    public float getRadius() {
	return radius;
    }

    /**
     * Returns the height of the cylinder
     *
     * @since Java 3D 1.2.1
     */
    public float getHeight() {
	return height;
    }

    /**
     * Returns the number divisions along the X direction
     *
     * @since Java 3D 1.2.1
     */
    public int getXdivisions() {
	return xdivisions;
    }

    /**
     * Returns the number of divisions along the height of the cylinder
     *
     * @since Java 3D 1.2.1
     */
    public int getYdivisions() {
	return ydivisions;
    }

}
