/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 tools4j.org (Marco Terzer, Anton Anufriev)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.tools4j.eventsourcing.event;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.tools4j.eventsourcing.header.DefaultHeader;

public class DefaultEvent implements Event {

    private final DefaultHeader header = new DefaultHeader();
    private final DirectBuffer payload = new UnsafeBuffer(0, 0);

    public DefaultEvent wrap(final DirectBuffer source,
                             final int offset,
                             final int length) {
        final int payloadLength = header.wrap(source, offset).payloadLength();
        payload.wrap(offset + Header.BYTE_LENGTH, payloadLength);
        assert Header.BYTE_LENGTH + payloadLength <= length;
        return this;
    }

    public DefaultEvent wrap(final Header header,
                             final DirectBuffer payload,
                             final int payloadOffset) {
        final int payloadLength = this.header.wrap(header).payloadLength();
        this.payload.wrap(payload, payloadOffset, payloadLength);
        return this;
    }

    public DefaultEvent wrap(final Header noPayloadHeader) {
        final int payloadLength = this.header.wrap(header).payloadLength();
        this.payload.wrap(0, 0);
        assert payloadLength == 0;
        return this;
    }

    public DefaultEvent unwrap() {
        header.unwrap();
        payload.wrap(0, 0);
        return this;
    }

    @Override
    public Header header() {
        return header;
    }

    @Override
    public DirectBuffer payload() {
        return payload;
    }
}
