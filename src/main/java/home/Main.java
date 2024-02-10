/*******************************************************************************
 * Copyright 2021-2024 Lenar Shamsutdinov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package home;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.gui.Gui;
import home.utils.LogUtils;

public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    private static final String DEFAULT_APP_NAME = "=VEHICLE_ACCOUNTING=";
    private static final String DEFAULT_APP_VERSION = "UNKNOWN";

    public static String appName;
    public static String appVersion;

    public static void main(String[] args) {
        boolean isStarted = false;
        try {
            startApplication();
            isStarted = true;
            LOG.info("Application {} v{} started successfully.", appName, appVersion);
        } catch (Exception e) {
            LogUtils.logAndShowError(LOG, null, e.getMessage(), "Application start error", e);
        } finally {
            if (!isStarted) {
                System.exit(1);
            }
        }
    }

    private static void startApplication() {
        setUncaughtExceptionProcessing();

        initAppDescription();
        Settings.readSettings();
        Gui.INSTANCE.buildGui();

        Data.initDb();
    }

    private static void setUncaughtExceptionProcessing() {
        var handler = new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LogUtils.logAndShowError(LOG, null, e.getMessage(), "Error", e);
                System.exit(1);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    private static void initAppDescription() {
        BiFunction<String, String, String> getSafeVal = (val, def) -> val != null ? val : def;
        Package pkg = Main.class.getPackage();
        appName = getSafeVal.apply(pkg.getImplementationTitle(), DEFAULT_APP_NAME);
        appVersion = getSafeVal.apply(pkg.getImplementationVersion(), DEFAULT_APP_VERSION);
    }
}
