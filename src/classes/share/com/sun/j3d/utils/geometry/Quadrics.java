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
import java.math.*;

class Quadrics extends Object {
  
  Quadrics (){ }

  // do not use this to make a cone.  It uses too many triangles.  The top of
  // cone can be a triangle fan array

  GeomBuffer cylinder(double lowr, double highr,
		      double height, int xdivisions, int ydivisions, 
		      boolean outside)
  {
    double r, sign;

    /* 
     *     Cylinder is created by extruding the unit circle along the Z+
     * axis. The number of layers is controlled by ydivisions (which is
     * actually zdivisions). For each consecutive sample points along the
     * unit circle, we also sample along the height of the cylinder. Quads
     * are created along the Z direction. When the unit circle at the base is
     * completed, we also obtain the layers of quads along the height of the
     * cylinder.
     * 
     *      Texture coordinates are created in a straight forward cylindrical
     * mapping of the texture map. The texture is wrapped from the back of
     * the cylinder.
     */

    if (outside)
      sign = 1.0;
    else 
      sign = -1.0;
    
    //Compute the deltas
    double dtheta = 2.0*Math.PI / xdivisions;
    double dr = (highr - lowr) / ydivisions;
    double dz = height / ydivisions;
    double znormal = (lowr - highr) / height;
    double du = 1.0 / xdivisions;
    double dv = 1.0 / ydivisions;

    //Initialize geometry buffer.
    GeomBuffer gbuf = new GeomBuffer(xdivisions*ydivisions*4);

    double s = 0.0, t = 0.0;
    for (int i=0;i<xdivisions;i++) {
      double px, py, qx, qy, z;
      // (px,py) and (qx,qy) are consecutive sample points along the 
      // unit circle. We will create quads along the Z+ direction. 
      // we have to start at the back of the sphere, so add 90 degrees
      px = Math.cos(i*dtheta + Math.PI/2.0); 
      py = Math.sin(i*dtheta + Math.PI/2.0);
      qx = Math.cos((i+1)*dtheta + Math.PI/2.0); 
      qy = Math.sin((i+1)*dtheta + Math.PI/2.0);
      // Initialize z,r,t
      z = -1.0*height/2.0; r = lowr; t = 0.0;

      gbuf.begin( GeomBuffer.QUAD_STRIP );
      // For each consecutive two unit circle points, 
      // we obtain the layers of quads along the Z direction. Number of
      // layers depends on ydivisions.
      for (int j=0; j<=ydivisions; j++,z += dz,r += dr,t += dv){
	if ((j == ydivisions) && (highr == 0)) {
          if (outside) {
            gbuf.normal3d( 0.0, 0.0, znormal*sign );
            gbuf.texCoord2d(s, t);
            gbuf.vertex3d( px*r, py*r, z );
            gbuf.normal3d( 0.0, 0.0, znormal*sign );
            gbuf.texCoord2d(s+du, t);
            gbuf.vertex3d( qx*r, qy*r, z );
          }
          else {
            gbuf.normal3d( 0.0, 0.0, znormal*sign );
            gbuf.texCoord2d(s, t);
            gbuf.vertex3d( qx*r, qy*r, z );
            gbuf.normal3d( 0.0, 0.0, znormal*sign );
            gbuf.texCoord2d(s+du, t);
            gbuf.vertex3d( px*r, py*r, z );
         }
	} else {
	  if (outside) {
	    gbuf.normal3d( px*sign, py*sign, znormal*sign );
	    gbuf.texCoord2d(s, t);
	    gbuf.vertex3d( px*r, py*r, z );
	    gbuf.normal3d( qx*sign, qy*sign, znormal*sign );
	    gbuf.texCoord2d(s+du, t);
	    gbuf.vertex3d( qx*r, qy*r, z );
	  }
	  else {
	    gbuf.normal3d( qx*sign, qy*sign, znormal*sign );
	    gbuf.texCoord2d(s, t);
	    gbuf.vertex3d( qx*r, qy*r, z );
	    gbuf.normal3d( px*sign, py*sign, znormal*sign );
	    gbuf.texCoord2d(s+du, t);
	    gbuf.vertex3d( px*r, py*r, z );
	  }
        }
      }
      gbuf.end();
      s += du;
    }
    return gbuf;
  }

  GeomBuffer disk(double r, int xdivisions, boolean outside)
  {
    double theta, dtheta, sign, sinTheta, cosTheta;
    int i;

    /* 
     *     Disk is created by evaluating points along the unit circle. Let
     * theta be the angle about the Z axis. Then, for each theta, we
     * obtain (cos(theta), sin(theta)) = (x,y) sample points. We create quad
     * strips (or fan strips) from these sample points.
     * 
     *    Texture coordinates of the disk is gotten from a unit circle
     * centered at (0.5, 0.5) in s,t space. Thus, portions of a texture is not
     * used.
     */

    if (outside) 
      sign = 1.0;
    else sign = -1.0;
    
    dtheta = 2.0*Math.PI / xdivisions;
    GeomBuffer gbuf = new GeomBuffer(xdivisions*4);

    //    gbuf.begin(GeomBuffer.QUAD_STRIP);
    gbuf.begin(GeomBuffer.TRIANGLE_FAN);
    // the first point to add is the center of the fan
    gbuf.normal3d( 0.0, 0.0, 1.0*sign );
    gbuf.texCoord2d(0.5,0.5);
    gbuf.vertex3d(0.0, 0.0, 0.0);
    if (outside) {
	for (i = 0;i <= xdivisions;i++) {
 	theta = i * dtheta;
	// add 90 degrees to theta so lines up with the body
 	sinTheta = Math.sin(theta + Math.PI/2.0);
 	cosTheta = Math.cos(theta + Math.PI/2.0);
 	// First point of quad
 	gbuf.normal3d( 0.0, 0.0, 1.0*sign );
 	// Texture coord is centered at (0.5, 0.5) in s,t space.
 	gbuf.texCoord2d(0.5+cosTheta*0.5,0.5+sinTheta*0.5);
	gbuf.vertex3d( r*cosTheta, r*sinTheta , 0.0);

      }
    }
    else {
      for (i=xdivisions;i>=0;i--) {
	theta = i * dtheta;
	// add 90 degrees to theta so lines up with the body
 	sinTheta = Math.sin(theta + Math.PI/2.0);
 	cosTheta = Math.cos(theta + Math.PI/2.0);
 	gbuf.normal3d( 0.0, 0.0, 1.0*sign );
	// if not outside, texture coordinates need to be upside down
	// to conform with VRML spec
//  	gbuf.texCoord2d(0.5-cosTheta*0.5, 0.5+sinTheta*0.5);
 	gbuf.texCoord2d(0.5+cosTheta*0.5, 0.5-sinTheta*0.5);
 	gbuf.vertex3d( r*cosTheta, r*sinTheta, 0.0 );
      }
    }
    
    gbuf.end();
    return gbuf;
  }

  // use this to make the top of the cone.  It uses a triangle fan so there
  // aren't extra triangles

  GeomBuffer coneTop(double coneRadius, double coneHeight, int xdivisions,
		     int ydivisions, boolean outside) {

    double sign;
    double radius = coneRadius/(double)ydivisions;

    double bottom = coneHeight/2.0 - coneHeight/(double)ydivisions;
    double top = coneHeight/2.0;

    if (outside) sign = 1.0;
    else sign = -1.0;

    // compute the deltas
    double dtheta = 2.0 * Math.PI / (double)xdivisions;
    double znormal = radius/(top-bottom);
    double du = 1.0/(double)xdivisions;

    // initialize geometry buffer
    GeomBuffer gbuf = new GeomBuffer(xdivisions + 2);
    gbuf.begin(GeomBuffer.TRIANGLE_FAN);
    // add the tip, which is the center of the fan
    gbuf.normal3d(0.0, 0.0, znormal*sign);
    gbuf.texCoord2d(.5, 1);
    gbuf.vertex3d(0.0, 0.0, top);

    // go around the circle and add the rest of the fan
    double s = 0.0;
    double t = 1.0 - 1.0/(double)ydivisions;
    double px, py;
    if (outside) {
      for (int i = 0; i <= xdivisions; i++) {
	// we have to start at the back of the sphere, so add 90 degrees
	px = Math.cos(i*dtheta + Math.PI/2.0);
	py = Math.sin(i*dtheta + Math.PI/2.0);
	gbuf.normal3d(px*sign, py*sign, znormal*sign);
	gbuf.texCoord2d(s, t);
	gbuf.vertex3d(px*radius, py*radius, bottom);
	s += du;
      }
    }
    else {
      for (int i = xdivisions; i >= 0; i--) {
	px = Math.cos(i*dtheta + Math.PI/2.0);
	py = Math.sin(i*dtheta + Math.PI/2.0);
	gbuf.normal3d(px*sign, py*sign, znormal*sign);
	gbuf.texCoord2d(s, t);
	gbuf.vertex3d(px*radius, py*radius, bottom);
	s += du;
      }
    }
    gbuf.end();
    return gbuf;
  }

  // use this to make the body of the cone.  similar to cylinder, only allows
  // you to leave off the top.

  GeomBuffer coneBody(double coneRadius, double height, 
		      int xdivisions, int ydivisions, 
		      boolean outside)
  {
    double r, sign;

    double bottom = -height/2.0;
    double topRadius = coneRadius/(double)ydivisions;

    /* 
     *     The cone body is created by extruding the unit circle along the Z+
     * axis. The number of layers is controlled by ydivisions (which is
     * actually zdivisions). For each consecutive sample points along the
     * unit circle, we also sample along the height of the cone. Quads
     * are created along the Z direction. When the unit circle at the base is
     * completed, we also obtain the layers of quads along the height of the
     * cylinder.
     * 
     *      Texture coordinates are created in a straight forward cylindrical
     * mapping of the texture map. The texture is wrapped from the back of
     * the cylinder.
     */

    if (outside)
      sign = 1.0;
    else 
      sign = -1.0;
    
    //Compute the deltas
    double dtheta = 2.0*Math.PI / xdivisions;
    double dr = -coneRadius / ydivisions;
    double dz = height / ydivisions;
    double znormal = coneRadius / height;
    double du = 1.0 / xdivisions;
    double dv = 1.0 / ydivisions;

    //Initialize geometry buffer.
    GeomBuffer gbuf = new GeomBuffer(xdivisions*ydivisions*4);

    double s = 0.0, t = 0.0;
    for (int i=0;i<xdivisions;i++) {
      double px, py, qx, qy, z;
      // (px,py) and (qx,qy) are consecutive sample points along the 
      // unit circle. We will create quads along the Z+ direction. 
      // we have to start at the back of the sphere, so add 90 degrees
      px = Math.cos(i*dtheta + Math.PI/2.0); 
      py = Math.sin(i*dtheta + Math.PI/2.0);
      qx = Math.cos((i+1)*dtheta + Math.PI/2.0); 
      qy = Math.sin((i+1)*dtheta + Math.PI/2.0);
      // Initialize z,r,t
      //z = -1.0*height/2.0; 
      r = coneRadius; t = 0.0;
      z = bottom;

      gbuf.begin( GeomBuffer.QUAD_STRIP );
      // For each consecutive two unit circle points, 
      // we obtain the layers of quads along the Z direction. Number of
      // layers depends on ydivisions.
      for (int j=0; j<=ydivisions-1; j++,z += dz,r += dr,t += dv){
	if (outside) {
	  gbuf.normal3d( px*sign, py*sign, znormal*sign );
	  gbuf.texCoord2d(s, t);
	  gbuf.vertex3d( px*r, py*r, z );
	  gbuf.normal3d( qx*sign, qy*sign, znormal*sign );
	  gbuf.texCoord2d(s+du, t);
	  gbuf.vertex3d( qx*r, qy*r, z );
	}
	else {
	  gbuf.normal3d( qx*sign, qy*sign, znormal*sign );
	  gbuf.texCoord2d(s, t);
	  gbuf.vertex3d( qx*r, qy*r, z );
	  gbuf.normal3d( px*sign, py*sign, znormal*sign );
	  gbuf.texCoord2d(s+du, t);
	  gbuf.vertex3d( px*r, py*r, z );
	}
      }
      gbuf.end();
      s += du;
    }
    return gbuf;
  }

    // new disk code to remove transforms in the primitive code
    GeomBuffer disk(double r, int xdiv, double y, boolean outside) {
	
	double theta, dtheta, sign, sinTheta, cosTheta;
	
	if (outside) sign = 1.0;
	else sign = -1.0;
	
	dtheta = 2.0*Math.PI / xdiv;
	
	GeomBuffer gbuf = new GeomBuffer(xdiv+2);

	gbuf.begin(GeomBuffer.TRIANGLE_FAN);
	gbuf.normal3d(0.0, 1.0*sign, 0.0);
	gbuf.texCoord2d(0.5, 0.5);
	gbuf.vertex3d(0.0, y, 0.0);

	// create the disk by evaluating points along the unit circle.
	// theta is the angle around the y-axis. Then we obtain
	// (cos(theta), sin(theta)) = (x,z) sample points.  The y value
	// was passed in as a parameter.
	// texture coordinates are obtain from the unit circle centered at
	// (.5, .5) in s, t space.  thus portions of the texture are not used.

	if (!outside) {
	    for (int i = 0; i <= xdiv; i++) {
		theta = i * dtheta;
		// add 90 degrees to theta so lines up wtih the body
		sinTheta = Math.sin(theta - Math.PI/2.0);
		cosTheta = Math.cos(theta - Math.PI/2.0);
		gbuf.normal3d(0.0, 1.0*sign, 0.0);
		gbuf.texCoord2d(0.5+cosTheta*0.5, 0.5+sinTheta*0.5);
		gbuf.vertex3d(r*cosTheta, y, r*sinTheta);
	    }
	}
	else {
	    for (int i = xdiv; i >= 0; i--) {
		theta = i * dtheta;
		// add 90 degrees to theta so lines up with the body
		sinTheta = Math.sin(theta - Math.PI/2.0);
		cosTheta = Math.cos(theta - Math.PI/2.0);
		gbuf.normal3d(0.0, 1.0*sign, 0.0);
		gbuf.texCoord2d(0.5+cosTheta*0.5, 0.5-sinTheta*0.5);
		gbuf.vertex3d(cosTheta*r, y, sinTheta*r);
	    }
	}
	
	gbuf.end();
	return gbuf;
     }


    // new cylinder to remove transforms in the cylinder code and to optimize
    // by using triangle strip
    GeomBuffer cylinder(double height, double radius,
			int xdiv, int ydiv, boolean outside) {

	double sign;

	if (outside) sign = 1.0;
	else sign = -1.0;

	// compute the deltas
	double dtheta = 2.0*Math.PI / (double)xdiv;
	double dy = height / (double)ydiv;
	double du = 1.0/(double)xdiv;
	double dv = 1.0/(double)ydiv;

	GeomBuffer gbuf = new GeomBuffer(ydiv*2*(xdiv+1));
	
	double s = 0.0, t = 0.0;
	double px, pz, qx, qz;
	double py = -height/2.0;
	double qy;

// 	int c;
//   	if (outside) c = ydiv*2*(xdiv+1) - 1;
//   	else c = 0;

	gbuf.begin(GeomBuffer.QUAD_STRIP);

	for (int i = 0; i < ydiv; i++) {
	    qy = py+dy;
	    if (outside) {
		px = Math.cos(xdiv*dtheta - Math.PI/2.0);
		pz = Math.sin(xdiv*dtheta - Math.PI/2.0);
		qx = Math.cos((xdiv-1)*dtheta - Math.PI/2.0);
		qz = Math.sin((xdiv-1)*dtheta - Math.PI/2.0);

		// vert 2
		gbuf.normal3d(px*sign, 0.0, pz*sign);
		gbuf.texCoord2d(s, t+dv);
		gbuf.vertex3d(px*radius, qy, pz*radius);
		
		// vert 1
		gbuf.normal3d(px*sign, 0.0, pz*sign);
		gbuf.texCoord2d(s, t);
		gbuf.vertex3d(px*radius, py, pz*radius);
		
		// vert 4
		gbuf.normal3d(qx*sign, 0.0, qz*sign);
		gbuf.texCoord2d(s+du, t+dv);
		gbuf.vertex3d(qx*radius, qy, qz*radius);
		
		// vert 3
		gbuf.normal3d(qx*sign, 0.0, qz*sign);
		gbuf.texCoord2d(s+du, t);
		gbuf.vertex3d(qx*radius, py, qz*radius);
		
		s += (du*2.0);
		
 		for (int j = xdiv-2; j >=0; j--) {
		    px = Math.cos(j*dtheta - Math.PI/2.0);
		    pz = Math.sin(j*dtheta - Math.PI/2.0);

		    // vert 6
		    gbuf.normal3d(px*sign, 0.0, pz*sign);
		    gbuf.texCoord2d(s, t+dv);
		    gbuf.vertex3d(px*radius, qy, pz*radius);
		    
		    // vert 5
		    gbuf.normal3d(px*sign, 0.0, pz*sign);
		    gbuf.texCoord2d(s, t);
		    gbuf.vertex3d(px*radius, py, pz*radius);		    

		    s += du;
		}
		
	    }
	    else {
// 		c = 0;
		px = Math.cos(-Math.PI/2.0);
		pz = Math.sin(-Math.PI/2.0);
		qx = Math.cos(dtheta - Math.PI/2.0);
		qz = Math.sin(dtheta - Math.PI/2.0);

		gbuf.normal3d(px*sign, 0.0, pz*sign);
		gbuf.texCoord2d(s, t+dv);
		gbuf.vertex3d(px*radius, qy, pz*radius);
		
		// vert 1
		gbuf.normal3d(px*sign, 0.0, pz*sign);
		gbuf.texCoord2d(s, t);
		gbuf.vertex3d(px*radius, py, pz*radius);
		
		gbuf.normal3d(qx*sign, 0.0, qz*sign);
		gbuf.texCoord2d(s+du, t+dv);
		gbuf.vertex3d(qx*radius, qy, qz*radius);

		gbuf.normal3d(qx*sign, 0.0, qz*sign);
		gbuf.texCoord2d(s+du, t);
		gbuf.vertex3d(qx*radius, py, qz*radius);
		
		s += (du*2.0);
		
 		for (int j = 2; j <= xdiv; j++) {
		    px = Math.cos(j*dtheta - Math.PI/2.0);
		    pz = Math.sin(j*dtheta - Math.PI/2.0);

		    gbuf.normal3d(px*sign, 0.0, pz*sign);
		    gbuf.texCoord2d(s, t+dv);
		    gbuf.vertex3d(px*radius, qy, pz*radius);
		    
		    gbuf.normal3d(px*sign, 0.0, pz*sign);
		    gbuf.texCoord2d(s, t);
		    gbuf.vertex3d(px*radius, py, pz*radius);
		    
		    s += du;
		}

	    }
	    s = 0.0;
	    t += dv;
	    py += dy;
	}
	
	gbuf.end();
	
	return gbuf;
    }

    // new coneBody method to remove transform in the Cone primitive
    // and to optimize by using triangle strip
    GeomBuffer coneBody(double bottom, double top, double bottomR, double topR,
			int xdiv, int ydiv, double dv, boolean outside) {

	double r, sign;

	if (outside) sign = 1.0;
	else sign = -1.0;

	// compute the deltas
	double dtheta = 2.0*Math.PI/(double)xdiv;
	double dr = (topR-bottomR)/(double)ydiv;
	double height = top-bottom;
	double dy = height/(double)ydiv;
	double ynormal = (bottomR-topR)/height;
	double du = 1.0/(double)xdiv;
// 	double dv = 1.0/(double)(ydiv+1);

	GeomBuffer gbuf = new GeomBuffer(ydiv*2*(xdiv+1));

	double s = 0.0, t = 0.0;
	double px, pz, qx, qz;
	double py = bottom;
	double qy;
	r = bottomR;

	gbuf.begin(GeomBuffer.QUAD_STRIP);

	for (int i = 0; i < ydiv; i++) {
	    qy = py+dy;
	    if (outside) {
		px = Math.cos(xdiv*dtheta - Math.PI/2.0);
		pz = Math.sin(xdiv*dtheta - Math.PI/2.0);
		qx = Math.cos((xdiv-1)*dtheta - Math.PI/2.0);
		qz = Math.sin((xdiv-1)*dtheta - Math.PI/2.0);

		// vert2
		gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		gbuf.texCoord2d(s, t+dv);
		gbuf.vertex3d(px*(r+dr), qy, pz*(r+dr));

		// vert1
		gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		gbuf.texCoord2d(s, t);
		gbuf.vertex3d(px*r, py, pz*r);

		// vert4
		gbuf.normal3d(qx*sign, ynormal*sign, qz*sign);
		gbuf.texCoord2d(s+du, t+dv);
		gbuf.vertex3d(qx*(r+dr), qy, qz*(r+dr));

		// vert3
		gbuf.normal3d(qx*sign, ynormal*sign, qz*sign);
		gbuf.texCoord2d(s+du, t);
		gbuf.vertex3d(qx*r, py, qz*r);

		s += (du*2.0);

		for (int j = xdiv-2; j >= 0; j--) {
		    px = Math.cos(j*dtheta - Math.PI/2.0);
		    pz = Math.sin(j*dtheta - Math.PI/2.0);

		    // vert 6
		    gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		    gbuf.texCoord2d(s, t+dv);
		    gbuf.vertex3d(px*(r+dr), qy, pz*(r+dr));

		    // vert 5
		    gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		    gbuf.texCoord2d(s, t);
		    gbuf.vertex3d(px*r, py, pz*r);

		    s += du;
		}
	    }
	    else {
		px = Math.cos(-Math.PI/2.0);
		pz = Math.sin(-Math.PI/2.0);
		qx = Math.cos(dtheta - Math.PI/2.0);
		qz = Math.sin(dtheta - Math.PI/2.0);

		// vert1
		gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		gbuf.texCoord2d(s, t+dv);
		gbuf.vertex3d(px*(r+dr), qy, pz*(r+dr));

		gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		gbuf.texCoord2d(s, t);
		gbuf.vertex3d(px*r, py, pz*r);

		gbuf.normal3d(qx*sign, ynormal*sign, qz*sign);
		gbuf.texCoord2d(s+du, t+dv);
		gbuf.vertex3d(qx*(r+dr), qy, qz*(r+dr));

		gbuf.normal3d(qx*sign, ynormal*sign, qz*sign);
		gbuf.texCoord2d(s+du, t);
		gbuf.vertex3d(qx*r, py, qz*r);

		s += (du*2.0);

		for (int j = 2; j <= xdiv; j++) {
		    px = Math.cos(j*dtheta - Math.PI/2.0);
		    pz = Math.sin(j*dtheta - Math.PI/2.0);

		    gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		    gbuf.texCoord2d(s, t+dv);
		    gbuf.vertex3d(px*(r+dr), qy, pz*(r+dr));

		    gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		    gbuf.texCoord2d(s, t);
		    gbuf.vertex3d(px*r, py, pz*r);

		    s += du;
		}
	    }
	    s = 0.0;
	    t += dv;
	    py += dy;
	    r += dr;
	}
	gbuf.end();

	return gbuf;
    }

    // new coneTop method to remove transforms in the cone code
    GeomBuffer coneTop(double bottom, double radius, double height,
		       int xdiv,double t, boolean outside) {

	double sign;

	if (outside) sign = 1.0;
	else sign = -1.0;

	// compute the deltas
	double dtheta = 2.0*Math.PI/(double)xdiv;
	double ynormal = radius/height;
	double du = 1.0/(double)xdiv;
	double top = bottom + height;

	// initialize the geometry buffer
	GeomBuffer gbuf = new GeomBuffer(xdiv + 2);
	gbuf.begin(GeomBuffer.TRIANGLE_FAN);

	// add the tip, which is the center of the fan
	gbuf.normal3d(0.0, ynormal*sign, 0.0);
	gbuf.texCoord2d(.5, 1.0);
	gbuf.vertex3d(0.0, top, 0.0);

	// go around the circle and add the rest of the fan
	double s = 0.0;
	double px, pz;
	if (outside) {
// 	    for (int i = 0; i <= xdiv; i++) {
	    for (int i = xdiv; i >= 0; i--) {
		px = Math.cos(i*dtheta - Math.PI/2.0);
		pz = Math.sin(i*dtheta - Math.PI/2.0);
		gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		gbuf.texCoord2d(s, t);
		gbuf.vertex3d(px*radius, bottom, pz*radius);

		s += du;
	    }
	}
	else {
// 	    for (int i = xdiv; i >= 0; i--) {
	    for (int i = 0; i <= xdiv; i++) {
		px = Math.cos(i*dtheta - Math.PI/2.0);
		pz = Math.sin(i*dtheta - Math.PI/2.0);
		gbuf.normal3d(px*sign, ynormal*sign, pz*sign);
		gbuf.texCoord2d(s, t);
		gbuf.vertex3d(px*radius, bottom, pz*radius);
		s += du;
	    }
	}
	gbuf.end();
	return gbuf;
    }
}

