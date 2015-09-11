LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_SRC_FILES := \
	Android.mk \
	empty.c \
	main.c \

include $(BUILD_SHARED_LIBRARY)
