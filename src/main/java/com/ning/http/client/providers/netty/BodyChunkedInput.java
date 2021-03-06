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
package com.ning.http.client.providers.netty;

import com.ning.http.client.Body;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.stream.ChunkedInput;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Adapts a {@link Body} to Netty's {@link ChunkedInput}.
 */
class BodyChunkedInput
        implements ChunkedInput {

    private final Body body;

    private final int chunkSize = 1024 * 8;

    private ByteBuffer nextChunk;

    private static final ByteBuffer EOF = ByteBuffer.allocate(0);

    public BodyChunkedInput(Body body) {
        if (body == null) {
            throw new IllegalArgumentException("no body specified");
        }
        this.body = body;
    }

    private ByteBuffer peekNextChuck()
            throws IOException {
        if (nextChunk == null) {
            ByteBuffer buffer = ByteBuffer.allocate(chunkSize);
            if (body.read(buffer) < 0) {
                nextChunk = EOF;
            } else {
                buffer.flip();
                nextChunk = buffer;
            }
        }
        return nextChunk;
    }

    public boolean hasNextChunk()
            throws Exception {
        return !isEndOfInput();
    }

    public Object nextChunk()
            throws Exception {
        ByteBuffer buffer = peekNextChuck();
        if (buffer == EOF) {
            return null;
        }
        nextChunk = null;
        return ChannelBuffers.wrappedBuffer(buffer);
    }

    public boolean isEndOfInput()
            throws Exception {
        return peekNextChuck() == EOF;
    }

    public void close()
            throws Exception {
        body.close();
    }

}
