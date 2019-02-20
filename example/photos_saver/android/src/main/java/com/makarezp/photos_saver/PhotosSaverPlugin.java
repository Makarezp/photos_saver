package com.makarezp.photos_saver;

import androidx.annotation.VisibleForTesting;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class PhotosSaverPlugin implements MethodCallHandler {

    private final PhotoSaverDelegate delegate;
    private final Registrar registrar;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "photos_saver");
        final PhotoSaverDelegate delegate =
                new PhotoSaverDelegate(registrar.activity());
        registrar.addRequestPermissionsResultListener(delegate);

        final PhotosSaverPlugin instance = new PhotosSaverPlugin(registrar, delegate);
        channel.setMethodCallHandler(instance);
    }

    @VisibleForTesting
    PhotosSaverPlugin(Registrar registrar, PhotoSaverDelegate delegate) {
        this.registrar = registrar;
        this.delegate = delegate;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (registrar.activity() == null) {
            result.error("no_activity", "image_picker plugin requires a foreground activity.", null);
            return;
        }
        if (call.method.equals("saveFile")) {
            delegate.saveImageToGallery(call, result);
        } else {
            result.notImplemented();
        }
    }
}
