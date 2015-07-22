LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_SRC_FILES := \
	C:\Users\Emilio\MDlive_Git_Projects\EmbedKit_Android\MDLiveAndroid\app\src\main\jni\Android.mk \
	C:\Users\Emilio\MDlive_Git_Projects\EmbedKit_Android\MDLiveAndroid\app\src\main\jni\empty.c \
	C:\Users\Emilio\MDlive_Git_Projects\EmbedKit_Android\MDLiveAndroid\app\src\main\jni\main.c \

LOCAL_C_INCLUDES += C:\Users\Emilio\MDlive_Git_Projects\EmbedKit_Android\MDLiveAndroid\app\src\main\jni
LOCAL_C_INCLUDES += C:\Users\Emilio\MDlive_Git_Projects\EmbedKit_Android\MDLiveAndroid\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
