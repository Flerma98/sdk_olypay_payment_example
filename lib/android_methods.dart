import 'package:flutter/services.dart';

abstract class AndroidMethods {
  static const String channelKotlin = "flutter_app/kotlin";
  static const MethodChannel methodChannel = MethodChannel(channelKotlin);

  static Future<String> makePayOliPay(
      {required final double mount, required final String reference}) async {
    try {
      return await methodChannel.invokeMethod(
          "make-pay-oli-pay-sdk", {"mount": mount, "reference": reference});
    } catch (error) {
      if (error is PlatformException) {
        throw error.message ?? "?";
      }
      throw error.toString();
    }
  }
}
