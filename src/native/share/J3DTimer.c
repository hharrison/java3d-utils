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

/*
 * Portions of this code were derived from work done by the Blackdown
 * group (www.blackdown.org), who did the initial Linux implementation
 * of the Java 3D API.
 */

#include "com_sun_j3d_utils_timer_J3DTimer.h"

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
    jlong pcRes = -1;
#endif

/*
 * Class:     com_sun_j3d_utils_timer_J3DTimer
 * Method:    getNativeTimer
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_j3d_utils_timer_J3DTimer_getNativeTimer
  (JNIEnv *env, jclass clazz) {

#ifdef SOLARIS
  /*struct timespec tp;*/
  /*clock_gettime( CLOCK_HIGHRES, &tp );*/

  /*return (jlong)tp.tv_nsec + (jlong)tp.tv_sec*1000000000;*/

  return (jlong)gethrtime();
#endif

#ifdef WIN32
  LARGE_INTEGER time;

  QueryPerformanceCounter( &time );

  if (pcRes==-1)
      pcRes = Java_com_sun_j3d_utils_timer_J3DTimer_getNativeTimerResolution(NULL, NULL );

  return (jlong)time.QuadPart*pcRes;
#endif
#ifdef __linux__

  struct timeval t;
  gettimeofday(&t, 0);
  return ((jlong)t.tv_sec) * 1000000000LL + ((jlong)t.tv_usec) * 1000;

#endif

}

/*
 * Class:     com_sun_j3d_utils_timer_J3DTimer
 * Method:    getNativeTimerResolution
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_j3d_utils_timer_J3DTimer_getNativeTimerResolution
  (JNIEnv *env, jclass clazz) {

#ifdef SOLARIS

  char buf[4];
  jlong res=0;

  sysinfo( SI_RELEASE, &buf[0], 4 );

  if (strcmp( "5.7", &buf[0] )==0) {
      res = (jlong)3;
  } else {
      struct timespec tp;
      clock_getres( CLOCK_HIGHRES, &tp );
      res = (jlong)tp.tv_nsec;
  }

  return res;
#endif

#ifdef WIN32
  LARGE_INTEGER freq;
  QueryPerformanceFrequency( &freq );

  if ( (jlong)freq.QuadPart==0 )
    return 1;

  return (jlong) ceil(1000000000/((jlong)freq.QuadPart));
#endif

#ifdef __linux__

  return ((jlong)1000000000) /sysconf (_SC_CLK_TCK);

 #endif

}
