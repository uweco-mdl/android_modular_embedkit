#include "com_mdlive_embedkit_global_MDLiveConfig.h"

/*
 * Class:     com_mdlive_embedkit_global_MDLiveConfig
 * Method:    getProdApiKeyFromNative
 * Signature: ()Ljava/lang/String;
 */
  JNIEXPORT jstring JNICALL Java_com_mdlive_embedkit_global_MDLiveConfig_getProdApiKeyFromNative
     (JNIEnv * env, jobject obj)
     {
           return (*env)->NewStringUTF(env, "b74d0fb9a114904c009b");
     }

   /*
    * Class:     com_mdlive_embedkit_global_MDLiveConfig
    * Method:    getProdSecretKeyFromNative
    * Signature: ()Ljava/lang/String;
    */
  JNIEXPORT jstring JNICALL Java_com_mdlive_embedkit_global_MDLiveConfig_getProdSecretKeyFromNative
     (JNIEnv * env, jobject obj)
     {
         return (*env)->NewStringUTF(env, "89c8d3ea88501e8e62a");
     }
