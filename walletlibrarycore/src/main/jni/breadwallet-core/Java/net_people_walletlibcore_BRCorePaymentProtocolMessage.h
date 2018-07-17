/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_people_walletlibcore_BRCorePaymentProtocolMessage */

#ifndef _Included_net_people_walletlibcore_BRCorePaymentProtocolMessage
#define _Included_net_people_walletlibcore_BRCorePaymentProtocolMessage
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    getMessageTypeValue
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_getMessageTypeValue
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    getMessage
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_getMessage
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    getStatusCode
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_getStatusCode
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    getStatusMessage
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_getStatusMessage
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    getIdentifier
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_getIdentifier
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    createPaymentProtocolMessage
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_createPaymentProtocolMessage
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    createPaymentProtocolMessageFull
 * Signature: (I[BJLjava/lang/String;[B)J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_createPaymentProtocolMessageFull
  (JNIEnv *, jclass, jint, jbyteArray, jlong, jstring, jbyteArray);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    serialize
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_serialize
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolMessage
 * Method:    disposeNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolMessage_disposeNative
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif