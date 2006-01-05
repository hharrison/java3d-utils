/*
 * $RCSfile$
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
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

import java.util.*;

// this class must be synchronized because different threads may try to access
// the freelists
public class UtilMemoryFreelist { // extends AbstractList {

    // never go smaller than the initial capacity
    ArrayList elementData = null;
    int size = 0;
    int currBlockSize = 10;
    Object[] currBlock = null;
    int currBlockIndex = 0;
    int spaceUsed = 0;
    int numBlocks = 0;
    int capacity = 0;
    int minBlockSize = 0;
    boolean justShrunk = false;
    int initcap = 10;
    
    // the minimum size since the last shrink
    int minSize = 0;

    Class c = null;

    public UtilMemoryFreelist(String className) {
	this(className, 10);
    }

    public UtilMemoryFreelist(String className, int initialCapacity) {
	if (initialCapacity < 0) {
	    throw new IllegalArgumentException ("Illegal Capacity: " +
						initialCapacity);
	}
	
	try {
	    c = Class.forName(className);
	}
	catch (Exception e) {
// 	    System.out.println(e);
	}

	initcap = initialCapacity;
	currBlockSize = initialCapacity;
	minBlockSize = currBlockSize;
	elementData = new ArrayList();
	// add the first block of memory to the arraylist
	currBlock = new Object[currBlockSize];
	elementData.add(currBlock);
	numBlocks++;
	capacity += currBlockSize;
    }

    public UtilMemoryFreelist(String className, Collection collection) {
	try {
	    c = Class.forName(className);
	}
	catch (Exception e) {
// 	    System.out.println(e);
	}
	size = collection.size();
	initcap = size;
	currBlockSize = size;
	minBlockSize = currBlockSize;
	elementData = new ArrayList();
	currBlock = new Object[currBlockSize];
	collection.toArray(currBlock);
	elementData.add(currBlock);
	numBlocks++;
	capacity += currBlockSize;
	spaceUsed = size;
    }

    public synchronized int size() {
	return size;
    }


    public synchronized boolean add(Object o) {
	if (justShrunk) {
	    // empty some space out in the current block instead of
	    // adding this message
	    if ((currBlockSize/2) < spaceUsed) {
		size -= (spaceUsed - (currBlockSize/2));
		spaceUsed = (currBlockSize/2);
		Arrays.fill(currBlock, spaceUsed, currBlockSize-1, null);
	    }
	    justShrunk = false;
	    return false;
	}
	else {
	ensureCapacity(size+1);

	// check to see if the whole block is used and if so, reset the
	// current block
// 	System.out.println("spaceUsed = " + spaceUsed + " currBlockSize = " +
// 			   currBlockSize + " currBlockIndex = " +
// 			   currBlockIndex + " currBlock = " + currBlock);
	if ((currBlockIndex == -1) || (spaceUsed >= currBlockSize)) {
	    currBlockIndex++;
	    currBlock = (Object[])elementData.get(currBlockIndex);
	    currBlockSize = currBlock.length;
	    spaceUsed = 0;
	}
	int index = spaceUsed++;
	currBlock[index] = o;
	size++;
	
	return true;
	}
    }

    private synchronized Object removeLastElement() {
//   	System.out.println("removeLastElement: size = " + size);
	int index = --spaceUsed;
// 	System.out.println("index = " + index);
	Object elm = currBlock[index];
	currBlock[index] = null;
	size--;

	// see if this block is empty now, and if it is set the previous
	// block to the current block
	if (spaceUsed == 0) {
	    currBlockIndex--;
	    if (currBlockIndex < 0) {
		currBlock = null;
		currBlockSize = 0;
	    }
	    else {
		currBlock = (Object[])elementData.get(currBlockIndex);
		currBlockSize = currBlock.length;
	    }
	    spaceUsed = currBlockSize;
	}

	return elm;
    }


    public synchronized void shrink() {
//  	System.out.println("shrink size = " + size + " minSize = " +
//  			   minSize);
	if ((minSize > minBlockSize) && (numBlocks > 1)) {
	    justShrunk = true;
	    
//  	    System.out.println("removing a block");
// 	    Runtime r = Runtime.getRuntime();
// 	    r.gc();
// 	    System.out.println("numBlocks = " + numBlocks + " size = " + size);
// 	    System.out.println("free memory before shrink: " + r.freeMemory());
	    
	    // remove the last block
	    Object[] block = (Object[])elementData.remove(numBlocks-1);
	    numBlocks--;
	    capacity -= block.length;

	    // we only need to do this if the block removed was the current
	    // block.  otherwise we just removed a null block.
	    if (numBlocks == currBlockIndex) {
		size -= spaceUsed;
		// set the current block to the last one
		currBlockIndex = numBlocks-1;
		currBlock = (Object[])elementData.get(currBlockIndex);
		currBlockSize = currBlock.length;

		spaceUsed = currBlockSize;
		
	    }
	    
// 	    r.gc();
// 	    System.out.println("free memory after  shrink: " + r.freeMemory());
// 	    System.out.println("numBlocks = " + numBlocks + " size = " + size);
	}
	else {
	    justShrunk = false;
	}
	minSize = size;
    }

    public synchronized void ensureCapacity(int minCapacity) {
// 	System.out.println("ensureCapacity: size = " + size + " capacity: " +
// 			   elementData.length);
// 	System.out.println("minCapacity = " + minCapacity + " capacity = "
// 			   + capacity);
	
	if (minCapacity > capacity) {
// 	    System.out.println("adding a block: numBlocks = " + numBlocks);
	    int lastBlockSize =
		((Object[])elementData.get(numBlocks-1)).length;
	    int prevBlockSize = 0;
	    if (numBlocks > 1) {
		prevBlockSize =
		((Object[])elementData.get(numBlocks-2)).length;
	    }
	    currBlockSize = lastBlockSize + prevBlockSize;
	    currBlock = new Object[currBlockSize];
	    elementData.add(currBlock);
	    numBlocks++;
	    currBlockIndex++;
	    capacity += currBlockSize;
	    // there is nothing used in this block yet
	    spaceUsed = 0;
	}
    }

    synchronized void rangeCheck(int index) {
	if (index >= size || index < 0) {
	    throw new IndexOutOfBoundsException("Index: " + index +
						", Size: " + size);
	}
    }

    public synchronized void clear() {
// 	System.out.println("clear");
	elementData.clear();

	// put an empty block in
	currBlockSize = initcap;
	minBlockSize = currBlockSize;
	currBlock = new Object[currBlockSize];
	elementData.add(currBlock);
	numBlocks = 1;
	capacity = currBlockSize;
	spaceUsed = 0;
	size = 0;
	currBlockIndex = 0;
	justShrunk = false;
    }

    public synchronized Object getObject() {
	if (size > 0) {
	    return removeLastElement();
	}
	else {
	    try {
		return c.newInstance();
	    }
	    catch (Exception e) {
// 		System.out.println("caught exception");
// 		System.out.print(e);
		return null;
	    }
	}
    }
	    
}

