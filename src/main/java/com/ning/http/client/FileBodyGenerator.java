/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package com.ning.http.client;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Creates a request body from the contents of a file.
 */
public class FileBodyGenerator
        implements BodyGenerator {

    private final File file;

    public FileBodyGenerator(File file) {
        if (file == null) {
            throw new IllegalArgumentException("no file specified");
        }
        this.file = file;
    }

    public RandomAccessBody createBody()
            throws IOException {
        return new FileBody(file);
    }

    protected static class FileBody
            implements RandomAccessBody {

        private final RandomAccessFile file;

        private final FileChannel channel;

        private final long length;

        public FileBody(File file)
                throws IOException {
            this.file = new RandomAccessFile(file, "r");
            channel = this.file.getChannel();
            length = this.file.length();
        }

        public long getContentLength() {
            return length;
        }

        public long read(ByteBuffer buffer)
                throws IOException {
            return channel.read(buffer);
        }

        public long transferTo(long position, long count, WritableByteChannel target)
                throws IOException {
            return channel.transferTo(position, count, target);
        }

        public void close()
                throws IOException {
            file.close();
        }

    }

}
