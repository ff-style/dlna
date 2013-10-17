# armeabi/armeabi-v7a
ABI ?= armeabi
UNIVERSAL_VER ?= no

_ABI ?= $(ABI)
ABI := $(strip $(_ABI))
_FPU ?= $(FPU)
FPU := $(strip $(_FPU))
_TUNE ?= $(TUNE)
TUNE := $(strip $(_TUNE))

_UNIVERSAL_VER ?= $(UNIVERSAL_VER)
UNIVERSAL_VER := $(strip $(_UNIVERSAL_VER))

ifeq ($(ABI),)
    ABI := armeabi-v7a
endif
ifeq ($(FPU),)
    FPU := vfp
endif
ifeq ($(TUNE),)
    TUNE := cortex-a8
endif

APP_ABI := $(ABI)

ifeq ($(NDK_DEBUG),1)
APP_OPTIM := debug
OPT_CFLAGS :=
else
APP_OPTIM := release
OPT_CFLAGS := -O3 -mlong-calls -fstrict-aliasing -fprefetch-loop-arrays -ffast-math
endif

ifeq ($(APP_ABI),armeabi-v7a)
    ifeq ($(FPU),neon)
        OPT_CFLAGS += -mfpu=neon -mtune=$(TUNE) -ftree-vectorize -mvectorize-with-neon-quad
        BUILD_WITH_NEON := 1
    else
        OPT_CFLAGS += -msoft-float -mtune=$(TUNE) -ftree-vectorize
        BUILD_WITH_NEON := 0
    endif
else
    OPT_CFLAGS += -march=armv6j -mtune=arm1136j-s -msoft-float
    BUILD_WITH_NEON := 0
endif

ifeq ($(BUILD_NEON_OBJS),yes)
	BUILD_WITH_NEON := 1
else
	BUILD_WITH_NEON := 0
endif

ifeq ($(BULID_FFMPEG1_0),yes)
	VERSION_FFMPEG := 1
else
	VERSION_FFMPEG := 0
endif

OPT_CPPFLAGS := $(OPT_CLFAGS)

APP_CFLAGS := $(APP_CFLAGS) $(OPT_CFLAGS) -DANDROID -DFONE_PLATFORM_ANDROID 
#APP_CFLAGS += -DFONE_LOG_ON -g
APP_CPPFLAGS := $(APP_CPPFLAGS) $(OPT_CPPFLAGS) 



