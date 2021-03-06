import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:photos_saver/photos_saver.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  Uint8List _imageData;
  var _scaffoldKey = new GlobalKey<ScaffoldState>();

  @override
  void initState() {
    super.initState();
    loadImage();
  }

  Future<void> loadImage() async {
    var imageData =
    await rootBundle.load("assets/images/landscape.jpg").then((byteData) {
      return byteData.buffer.asUint8List();
    });

    if (!mounted) return;

    setState(() {
      _imageData = imageData;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        key: _scaffoldKey,
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              _imageData != null
                  ? Image.memory(_imageData,
                  fit: BoxFit.cover, width: double.infinity)
                  : Text("Loading"),
              SizedBox(height: 16),
              RaisedButton(
                onPressed: () async {
                  String filePath =
                  await PhotosSaver.saveFile(fileData: _imageData);
                  _scaffoldKey.currentState.showSnackBar(SnackBar(
                      duration: Duration(seconds: 5),
                      content: Text("Created image file at $filePath")));
                },
                child: Text("Add this image to gallery"),
              )
            ],
          ),
        ),
      ),
    );
  }
}
