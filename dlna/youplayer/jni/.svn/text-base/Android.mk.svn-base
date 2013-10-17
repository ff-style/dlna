# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)


LOCAL_LDLIBS += -llog -ldl

TARGET_PLATFORM := android-8
LOCAL_MODULE    := wifi_setting
LOCAL_ARM_MODE := arm

LOCAL_CFLAGS := -DANDROID -DFONE_PLATFORM_ANDROID \
-Dplayer_LOG_ON -g 

LOCAL_SRC_FILES := \
	src/imyplayer-jni.c \
	src/player_log.c \
	src/macrodisk.c \
	src/md5.c \
  
LOCAL_C_INCLUDES := \
				$(LOCAL_PATH)/inc	\

include $(BUILD_SHARED_LIBRARY)
