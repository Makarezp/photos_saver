import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';


class ImageGallerySaver {
  static const MethodChannel _channel =
      const MethodChannel('image_gallery_saver');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> saveFile({@required Uint8List fileData}) async {
    assert(fileData != null);

    String filePath = await _channel.invokeMethod(
      'saveFile',
      <String, dynamic>{
        'fileData': fileData,
      },
    );
    debugPrint("saved filePath:" + filePath);
    //process ios return filePath
    if(filePath.startsWith("file://")){
      filePath=filePath.replaceAll("file://", "");
    }
    return  filePath;
  }
}
