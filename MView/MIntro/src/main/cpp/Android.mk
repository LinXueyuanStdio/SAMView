MY_LOCAL_PATH := $(call my-dir)
LOCAL_PATH := $(MY_LOCAL_PATH)

include $(CLEAR_VARS)

LOCAL_PRELINK_MODULE := false

LOCAL_MODULE 	:= intro.30
LOCAL_CFLAGS 	:= -w -std=c11 -Os -DNULL=0 -DSOCKLEN_T=socklen_t -DLOCALE_NOT_USED -D_LARGEFILE_SOURCE=1
LOCAL_CFLAGS 	+= -Drestrict='' -D__EMX__ -DOPUS_BUILD -DFIXED_POINT -DUSE_ALLOCA -DHAVE_LRINT -DHAVE_LRINTF -fno-math-errno
LOCAL_CFLAGS 	+= -DANDROID_NDK -DDISABLE_IMPORTGL -fno-strict-aliasing -fprefetch-loop-arrays -DAVOID_TABLES -DANDROID_TILE_BASED_DECODE -DANDROID_ARMV6_IDCT -ffast-math -D__STDC_CONSTANT_MACROS
LOCAL_CPPFLAGS 	:= -DBSD=1 -ffast-math -Os -funroll-loops -std=c++14 -DPACKAGE_NAME='"drinkless/org/ton"'
LOCAL_LDLIBS 	:= -ljnigraphics -llog -lz -lEGL -lGLESv2 -landroid

LOCAL_C_INCLUDES    := \
./src/main/cpp/intro

LOCAL_SRC_FILES     += \
./intro/IntroRenderer.c

include $(BUILD_SHARED_LIBRARY)
