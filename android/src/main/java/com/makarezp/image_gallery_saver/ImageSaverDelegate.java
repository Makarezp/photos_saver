// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.makarezp.image_gallery_saver;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import java.io.IOException;

import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;


public class ImageSaverDelegate
        implements
        PluginRegistry.RequestPermissionsResultListener {

    static final int REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION = 2344;

    private final Activity activity;
    private final PermissionManager permissionManager;

    interface PermissionManager {
        boolean isPermissionGranted(String permissionName);

        void askForPermission(String permissionName, int requestCode);
    }

    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;

    public ImageSaverDelegate(
            final Activity activity) {
        this(
                activity,
                null,
                null,
                new PermissionManager() {
                    @Override
                    public boolean isPermissionGranted(String permissionName) {
                        return ActivityCompat.checkSelfPermission(activity, permissionName)
                                == PackageManager.PERMISSION_GRANTED;
                    }

                    @Override
                    public void askForPermission(String permissionName, int requestCode) {
                        ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
                    }
                });

    }

    /**
     * This constructor is used exclusively for testing; it can be used to provide mocks to final
     * fields of this class. Otherwise those fields would have to be mutable and visible.
     */
    @VisibleForTesting
    ImageSaverDelegate(
            Activity activity,
            MethodChannel.Result result,
            MethodCall methodCall,
            PermissionManager permissionManager) {
        this.activity = activity;
        this.pendingResult = result;
        this.methodCall = methodCall;
        this.permissionManager = permissionManager;
    }


    public void saveImageToGallery(MethodCall methodCall, MethodChannel.Result result) {

        setPendingMethodCallAndResult(methodCall, result);

        if (!permissionManager.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionManager.askForPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION);
            return;
        }
        byte[] fileData = methodCall.argument("fileData");


        String filePath = null;
        try {
            filePath = CapturePhotoUtils.insertImage(activity.getContentResolver(), fileData, "Camera", "123");
        } catch (IOException e) {
            e.printStackTrace();
            finishWithError("error while saving file", e.getMessage());
        }

        finishWithSuccess(filePath);

    }

    @Override
    public boolean onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted =
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case REQUEST_EXTERNAL_IMAGE_STORAGE_PERMISSION:
                if (permissionGranted) {
                    saveImageToGallery(methodCall, pendingResult);
                }
                break;
            default:
                return false;
        }

        if (!permissionGranted) {
            finishWithSuccess(null);
        }

        return true;
    }

    private void setPendingMethodCallAndResult(
            MethodCall methodCall, MethodChannel.Result result) {
        this.methodCall = methodCall;
        pendingResult = result;
    }

    private void finishWithSuccess(String imagePath) {
        pendingResult.success(imagePath);
        clearMethodCallAndResult();
    }

    private void finishWithAlreadyActiveError() {
        finishWithError("already_active", "Image picker is already active");
    }

    private void finishWithError(String errorCode, String errorMessage) {
        pendingResult.error(errorCode, errorMessage, null);
        clearMethodCallAndResult();
    }

    private void clearMethodCallAndResult() {
        methodCall = null;
        pendingResult = null;
    }
}
