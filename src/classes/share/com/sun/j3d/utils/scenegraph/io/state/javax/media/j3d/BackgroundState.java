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

package com.sun.j3d.utils.scenegraph.io.state.javax.media.j3d;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import com.sun.j3d.utils.scenegraph.io.retained.Controller;
import com.sun.j3d.utils.scenegraph.io.retained.SymbolTableData;
import javax.media.j3d.SceneGraphObject;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.Appearance;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Color3f;

public class BackgroundState extends LeafState {
    
    private int image;
    private int boundingLeaf;
    private int geometry;
    
    public BackgroundState(SymbolTableData symbol,Controller control) {
        super(symbol, control);
        
        if (node!=null) {
            boundingLeaf = control.getSymbolTable().addReference( ((Background)node).getApplicationBoundingLeaf());
            geometry =  control.getSymbolTable().addReference( ((Background)node).getGeometry());
            image = control.getSymbolTable().addReference( ((Background)node).getImage());
        }
    }
    
    public void writeObject( DataOutput out ) throws IOException {
        super.writeObject( out );
        
        out.writeInt( boundingLeaf );
        out.writeInt( geometry );
        out.writeInt( image );
        control.writeBounds( out, ((Background)node).getApplicationBounds() );
        Color3f clr = new Color3f();
        ((Background)node).getColor(clr);
        control.writeColor3f( out, clr );
	out.writeInt( ((Background)node).getImageScaleMode() );
    }
    
    public void readObject( DataInput in ) throws IOException {
        super.readObject( in );
        
        boundingLeaf = in.readInt();
        geometry = in.readInt();
        image = in.readInt();
        
        ((Background)node).setApplicationBounds( control.readBounds(in));
        ((Background)node).setColor( control.readColor3f(in));
        
	((Background)node).setImageScaleMode( in.readInt() );
    }

    /**
     * Called when this component reference count is incremented.
     * Allows this component to update the reference count of any components
     * that it references.
     */
    public void addSubReference() {
	// geometry and boundingLeaf not node components
        control.getSymbolTable().incNodeComponentRefCount( image );
    }
    
    public void buildGraph() {
        ((Background)node).setApplicationBoundingLeaf( (BoundingLeaf)control.getSymbolTable().getJ3dNode( boundingLeaf ));
        ((Background)node).setGeometry( (BranchGroup)control.getSymbolTable().getJ3dNode( geometry ));
        ((Background)node).setImage( (ImageComponent2D)control.getSymbolTable().getJ3dNode( image ));
        super.buildGraph();     // Must be last call in method
 
    }
    
    protected SceneGraphObject createNode() {
        return new Background();
    }
    
}

