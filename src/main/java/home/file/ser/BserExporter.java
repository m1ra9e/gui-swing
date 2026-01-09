/*******************************************************************************
 * Copyright 2021-2026 Lenar Shamsutdinov
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
package home.file.ser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import home.Storage;
import home.file.IExporter;
import home.utils.LogUtils;

public final class BserExporter implements IExporter {

    private static final Logger LOG = LoggerFactory.getLogger(BserExporter.class);

    @Override
    public String exportAllDataObjsToString() {
        try (var byteArrOutputStream = new ByteArrayOutputStream();
                var objOutputStream = new ObjectOutputStream(byteArrOutputStream)) {
            objOutputStream.writeObject(Storage.INSTANCE.getAll());
            return Base64.getEncoder().encodeToString(byteArrOutputStream.toByteArray());
        } catch (IOException e) {
            throw LogUtils.logAndCreateIllegalStateException("BSER export converter error", LOG, e);
        }
    }
}