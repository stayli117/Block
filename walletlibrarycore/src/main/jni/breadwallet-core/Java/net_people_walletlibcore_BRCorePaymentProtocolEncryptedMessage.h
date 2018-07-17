/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage */

#ifndef _Included_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
#define _Included_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getMessage
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getMessage
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getReceiverPublicKeyReference
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getReceiverPublicKeyReference
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getSenderPublicKeyReference
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getSenderPublicKeyReference
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getNonce
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getNonce
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getSignature
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getSignature
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getIdentifier
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getIdentifier
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getStatusCode
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getStatusCode
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    getStatusMessage
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_getStatusMessage
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    createPaymentProtocolEncryptedMessage
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_createPaymentProtocolEncryptedMessage
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    serialize
 * Signature: ()[B
 */
JNIEXPORT jbyteArray JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_serialize
  (JNIEnv *, jobject);

/*
 * Class:     net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage
 * Method:    disposeNative
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_net_people_walletlibcore_BRCorePaymentProtocolEncryptedMessage_disposeNative
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif