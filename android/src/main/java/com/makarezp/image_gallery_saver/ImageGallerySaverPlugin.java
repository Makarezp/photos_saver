package com.makarezp.image_gallery_saver;

import androidx.annotation.VisibleForTesting;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * ImageGallerySaverPlugin
 */
public class ImageGallerySaverPlugin implements MethodCallHandler {

    private final ImageSaverDelegate delegate;
    private final PluginRegistry.Registrar registrar;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "image_gallery_saver");
        final ImageSaverDelegate delegate =
                new ImageSaverDelegate(registrar.activity());
        registrar.addRequestPermissionsResultListener(delegate);

        final ImageGallerySaverPlugin instance = new ImageGallerySaverPlugin(registrar, delegate);
        channel.setMethodCallHandler(instance);
    }

    @VisibleForTesting
    ImageGallerySaverPlugin(PluginRegistry.Registrar registrar, ImageSaverDelegate delegate) {
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
