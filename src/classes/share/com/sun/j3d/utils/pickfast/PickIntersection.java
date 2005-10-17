/*
 * $RCSfile$
 *
 * Copyright (c) 2005 Sun Microsystems, Inc. All rights reserved.
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

package com.sun.j3d.utils.pickfast;

import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * Holds information about an intersection of a PickShape with a Node 
 * as part of a PickInfo.IntersectionInfo. Information about
 * the intersected geometry, intersected primitive, intersection point, and 
 * closest vertex can be inquired.  
 * <p>
 * The intersected primitive indicates which primitive out of the GeometryArray
 * was intersected (where the primitive is a point, line, triangle or quad, 
 * not a
 * <code>com.sun.j3d.utils.geometry.Primitive)</code>.  
 * For example, the intersection would indicate which triangle out of a 
 * triangle strip was intersected.
 * The methods which return primitive data will have one value if the primitive 
 * is
 * a point, two values if the primitive is a line, three values if the primitive
 * is a triangle and four values if the primitive is quad.
 * <p>
 * The primitive's VWorld coordinates are saved when then intersection is 
 * calculated.  The local coordinates, normal, color and texture coordinates
 * for the primitive can also be inquired if they are present and readable.
 * <p>
 * The intersection point is the location on the primitive which intersects the
 * pick shape closest to the center of the pick shape. The intersection point's
 * location in VWorld coordinates is saved when the intersection is calculated.
 * The local coordinates, normal, color and texture coordiantes of at the
 * intersection can be interpolated if they are present and readable.
 * <p>
 * The closest vertex is the vertex of the primitive closest to the intersection
 * point.  The vertex index, VWorld coordinates and local coordinates of the 
 * closest vertex can be inquired.  The normal, color and texture coordinate
 * of the closest vertex can be inquired from the geometry array:
 * <p><blockquote><pre>
 *      Vector3f getNormal(PickIntersection pi, int vertexIndex) {
 *          int index;
 *          Vector3d normal = new Vector3f();
 *          GeometryArray ga = pickIntersection.getGeometryArray();
 *          if (pickIntersection.geometryIsIndexed()) {
 *              index = ga.getNormalIndex(vertexIndex);
 *          } else {
 *              index = vertexIndex;
 *          }
 *          ga.getNormal(index, normal);
 *          return normal;
 *      }
 * </pre></blockquote>
 * <p>
 * The color, normal
 * and texture coordinate information for the intersected primitive and the 
 * intersection point
 * can be inquired
 * the geometry includes them and the corresponding READ capibility bits are 
 * set.
 */

public class PickIntersection {

    
    /* ===================   METHODS  ======================= */

    /** Constructor 
	@param intersectionInfo The IntersectionInfo this intersection is part of.
    */
    //PickIntersection (PickResult pr, GeometryArray geomArr) {
    public PickIntersection (PickInfo.IntersectionInfo intersectionInfo) {
	
    }
    
    /** Returns true if the geometry is indexed 
     
    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public boolean geometryIsIndexed() {
	throw new UnsupportedOperationException();
    }

    /** Get coordinates of closest vertex (local) 
	@return the coordinates of the vertex closest to the intersection point

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Point3d getClosestVertexCoordinates () {
	throw new UnsupportedOperationException();
    }

    /** Get coordinates of closest vertex (world) 
	@return the coordinates of the vertex closest to the intersection point

	* This method is currently not supported.
	* @exception UnsupportedOperationException this method is not supported

    */
    public Point3d getClosestVertexCoordinatesVW () {
	throw new UnsupportedOperationException();
    }

    /** Get index of closest vertex 
	@return the index of the closest vertex

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public int getClosestVertexIndex () { 
	throw new UnsupportedOperationException();
    }

    /** 
	Get the distance from the PickShape start point to the intersection point
	@return the distance to the intersection point, if available.

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public double getDistance () {
	throw new UnsupportedOperationException();
    }


    /**
       Returns the color of the intersection point. Returns null if the geometry
       does not contain colors.  If the geometry was defined with
       GeometryArray.COLOR_3, the 'w' component of the color will initialized to 
       1.0
       @return color at the intersection point.  

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Color4f getPointColor() {
	throw new UnsupportedOperationException();
    }


    /**
       Returns the coordinates of the intersection point (local coordinates),
       if available.
       @return coordinates of the intersection point

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Point3d getPointCoordinates() {
	throw new UnsupportedOperationException();
    }

    /**
       Returns the coordinates of the intersection point (world coordinates), 
       if available.
       @return coordinates of the point

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Point3d getPointCoordinatesVW() {
	throw new UnsupportedOperationException();
    }

    /**
       Returns the normal of the intersection point. Returns null if the geometry
       does not contain normals.
       @return normal at the intersection point.  

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Vector3f getPointNormal() {
	throw new UnsupportedOperationException();
    }

    /**
       Returns the texture coordinate of the intersection point at the specifed 
       index in the specified texture coordinate set.
       Returns null if the geometry
       does not contain texture coordinates.  If the geometry was defined with
       GeometryArray.TEXTURE_COORDINATE_3, the 'z' component of the texture
       coordinate will initialized to 0.0
       @return texture coordinate at the intersection point.  

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public TexCoord3f getPointTextureCoordinate(int index) {
	throw new UnsupportedOperationException();
    }

    /** 
	Get the color indices for the intersected primitive.  For a non-indexed
	primitive, this will be the same as the primitive vertex indices
	If the geometry array does not contain colors this will return null.
	@return an array indices

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public int[] getPrimitiveColorIndices () {
	throw new UnsupportedOperationException();
    }

    /** 
	Get the colors of the intersected primitive.  This will return null if
	the primitive does not contain colors.  If the geometry was defined
	using GeometryArray.COLOR_3, the 'w' value of the color will be set to 1.0.
	@return an array of Point3d's for the primitive that was intersected

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Color4f[] getPrimitiveColors () {
	throw new UnsupportedOperationException();
    }

    /** 
	Get the coordinates indices for the intersected primitive.  For a non-indexed
	primitive, this will be the same as the primitive vertex indices
	@return an array indices

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public int[] getPrimitiveCoordinateIndices () {
	throw new UnsupportedOperationException();
    }

    /** 
	Get the local coordinates intersected primitive 
	@return an array of Point3d's for the primitive that was intersected

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Point3d[] getPrimitiveCoordinates () {
	throw new UnsupportedOperationException();    
    }    

    /** 
	Get VWorld coordinates of the intersected primitive 
	@return an array of Point3d's for the primitive that was picked

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Point3d[] getPrimitiveCoordinatesVW () {
	throw new UnsupportedOperationException();
    }
    
    /** 
	Get the normal indices for the intersected primitive.  For a non-indexed
	primitive, this will be the same as the primitive vertex indices
	If the geometry array does not contain normals this will return null
	@return an array indices

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public int[] getPrimitiveNormalIndices () {
	throw new UnsupportedOperationException();
    }

    /** 
	Get the normals of the intersected primitive.  This will return null if
	the primitive does not contain normals.
	@return an array of Point3d's for the primitive that was intersected

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public Vector3f[] getPrimitiveNormals () {
	throw new UnsupportedOperationException();
    }


    /** 
	Get the texture coordinate indices for the intersected primitive at the specifed 
	index in the specified texture coordinate set.  For a   non-indexed
	primitive, this will be the same as the primitive vertex indices
	If the geometry array does not contain texture coordinates, this will 
	return null.
	@return an array indices

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public int[] getPrimitiveTexCoordIndices (int index) {
	throw new UnsupportedOperationException();
    }

    /** 
	Get the texture coordinates of the intersected primitive at the specifed 
	index in the specified texture coordinate set.
	null if the primitive does not contain texture coordinates.  
	If the geometry was defined
	using GeometryArray.TEXTURE_COORDINATE_2, the 'z' value of the texture
	coordinate will be set to 0.0.
	@return an array of TexCoord3f's for the primitive that was intersected

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public TexCoord3f[] getPrimitiveTexCoords (int index) {
	throw new UnsupportedOperationException();
    }

    /** 
	Get vertex indices of the intersected primitive
	@return an array which contains the list of indices

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public int [] getPrimitiveVertexIndices () {
	throw new UnsupportedOperationException();
    }

    /**
       String representation of this object

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public String toString () {
	throw new UnsupportedOperationException();
    }


    /**
       Gets the IntersectionInfo this intersection is part of.

    * This method is currently not supported.
    * @exception UnsupportedOperationException this method is not supported

    */
    public PickInfo.IntersectionInfo getIntersectionInfo() {
	throw new UnsupportedOperationException();
    }

    
} // PickIntersection









