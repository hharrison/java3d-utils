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

/*
 * Portions of this code were derived from work done by the Blackdown
 * group (www.blackdown.org), who did the initial Linux implementation
 * of the Java 3D API.
 */

#include "com_sun_j3d_utils_timer_J3DTimer.h"

#define NSEC_PER_SEC ((jlong)1000000000)

#ifdef __linux__
#include <sys/time.h>
#include <time.h>
#include <unistd.h>
#endif

#ifdef SOLARIS
#include <time.h>
#include <sys/systeminfo.h>
#include <string.h>
#ifndef CLOCK_HIGHRES
#define CLOCK_HIGHRES 4			/* Solaris 7 does not define this */
#endif					/* constant. When run on Solaris 7 */
#endif					/* CLOCK_HIGHRES is not used. */

#ifdef WIN32
#include <Windows.h>
#include <math.h>
static double timerScale = -1.0;
#endif

/*
 * Class:     com_sun_j3d_utils_timer_J3DTimer
 * Method:    getNativeTimer
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_j3d_utils_timer_J3DTimer_getNativeTimer(JNIEnv *env,
						     jclass clazz)
{
    jlong timerNsec;

#ifdef SOLARIS
    /*
    struct timespec tp;
    clock_gettime( CLOCK_HIGHRES, &tp );

    return (jlong)tp.tv_nsec + (jlong)tp.tv_sec * NSEC_PER_SEC;
    */

    timerNsec = (jlong)gethrtime();
#endif /* SOLARIS */

#ifdef WIN32
    LARGE_INTEGER time;
    LARGE_INTEGER freq;

    if (timerScale < 0.0) {
	QueryPerformanceFrequency( &freq );
	if (freq.QuadPart <= 0) {
	    timerScale = 0.0;
	}
	else {
	    timerScale = (double) NSEC_PER_SEC / (double)freq.QuadPart;
	}
    }

    QueryPerformanceCounter(&time);
    timerNsec = (jlong)((double)time.QuadPart * timerScale);

#endif /* WIN32 */

#ifdef __linux__
    struct timeval t;

    gettimeofday(&t, 0);
    timerNsec = ((jlong)t.tv_sec) * NSEC_PER_SEC + ((jlong)t.tv_usec) * ((jlong)1000);
#endif /* __linux__ */

    return timerNsec;
}


/*
 * Class:     com_sun_j3d_utils_timer_J3DTimer
 * Method:    getNativeTimerResolution
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL
Java_com_sun_j3d_utils_timer_J3DTimer_getNativeTimerResolution(JNIEnv *env,
							       jclass clazz)
{
    jlong res;

#ifdef SOLARIS
    char buf[4];

    sysinfo( SI_RELEASE, &buf[0], 4 );

    if (strcmp( "5.7", &buf[0] )==0) {
	/* Hard-coded for Solaris 7, since clock_getres isn't available */
	res = (jlong)3;
    } else {
	struct timespec tp;
	clock_getres( CLOCK_HIGHRES, &tp );
	res = (jlong)tp.tv_nsec;
    }
#endif /* SOLARIS */

#ifdef WIN32
    LARGE_INTEGER freq;
    QueryPerformanceFrequency( &freq );

    if ((jlong)freq.QuadPart <= 0)
	res = 0;
    else {
	res = (NSEC_PER_SEC + (jlong)freq.QuadPart - 1) / ((jlong)freq.QuadPart);

	if (res < 1) {
	    res = 1;
	}
    }
#endif

#ifdef __linux__
    /* Hard-coded at 1 microsecond -- the resolution of gettimeofday */
    res = 1000;
#endif /* __linux__ */

    return res;
}
