#include "com_mdlive_embedkit_global_MDLiveConfig.h"

/*
 * Class:     com_mdlive_embedkit_global_MDLiveConfig
 * Method:    getProdApiKeyFromNative
 * Signature: ()Ljava/lang/String;
 */
   JNIEXPORT jstring JNICALL Java_com_mdlive_embedkit_global_MDLiveConfig_getProdApiKeyFromNative
     (JNIEnv * env, jobject obj)
     {
           return (*env)->NewStringUTF(env, "9e511cd537c72a11338b");
     }

   /*
    * Class:     com_mdlive_embedkit_global_MDLiveConfig
    * Method:    getProdSecretKeyFromNative
    * Signature: ()Ljava/lang/String;
    */
   JNIEXPORT jstring JNICALL Java_com_mdlive_embedkit_global_MDLiveConfig_getProdSecretKeyFromNative
     (JNIEnv * env, jobject obj)
     {
         return (*env)->NewStringUTF(env, "e7302efc779e724040d");
     }
