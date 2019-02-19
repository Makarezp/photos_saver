import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:image_gallery_saver/image_gallery_saver.dart';

void main() {
  const MethodChannel channel = MethodChannel('image_gallery_saver');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await ImageGallerySaver.platformVersion, '42');
  });
}
