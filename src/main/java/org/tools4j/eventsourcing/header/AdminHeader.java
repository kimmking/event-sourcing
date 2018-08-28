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
package org.tools4j.eventsourcing.header;

import org.tools4j.eventsourcing.event.Header;
import org.tools4j.eventsourcing.event.Type;
import org.tools4j.eventsourcing.event.Version;

public class AdminHeader implements Header {

    private final Type type;
    private byte version;
    private long sourceSeqNo;
    private long eventTimeNanosSinceEpoch;
    private int userData;

    public AdminHeader(final Type type) {
        if (!type.isAdmin()) {
            throw new IllegalArgumentException("Not an 'Admin' type: " + type);
        }
        this.type = type;
    }

    @Override
    public byte version() {
        return version;
    }

    @Override
    public Type type() {
        return type;
    }

    @Override
    public short templateId() {
        return ADMIN_TEMPLATE_ID;
    }

    @Override
    public int inputSourceId() {
        return ADMIN_SOURCE_ID;
    }

    @Override
    public long sourceSeqNo() {
        return sourceSeqNo;
    }

    @Override
    public long eventTimeNanosSinceEpoch() {
        return eventTimeNanosSinceEpoch;
    }

    @Override
    public int userData() {
        return userData;
    }

    @Override
    public int payloadLength() {
        return 0;
    }

    public AdminHeader version(final Version version) {
        return version(version.code());
    }

    public AdminHeader version(final byte version) {
        this.version = version;
        return this;
    }

    public AdminHeader sourceSeqNo(final long sourceSeqNo) {
        this.sourceSeqNo = validateSourceSeqNo(sourceSeqNo);
        return this;
    }

    public AdminHeader eventTimeNanosSinceEpoch(final long eventTimeNanosSinceEpoch) {
        this.eventTimeNanosSinceEpoch = eventTimeNanosSinceEpoch;
        return this;
    }

    public AdminHeader userData(final int userData) {
        this.userData = userData;
        return this;
    }

    public AdminHeader init(final Header header) {
        if (type() != header.type()) {
            throw new IllegalArgumentException("Invalid type, expected=" + type() + " but found " + header.type());
        }
        return this
                .version(header.version())
                .sourceSeqNo(header.sourceSeqNo())
                .eventTimeNanosSinceEpoch(header.eventTimeNanosSinceEpoch())
                .userData(header.userData());
    }

    static int validateInputSourceId(final int inputSourceId) {
        if (inputSourceId != ADMIN_SOURCE_ID) {
            return inputSourceId;
        }
        throw new IllegalArgumentException("Input source ID " + inputSourceId + " is reserved");
    }

    static long validateSourceSeqNo(final long sourceSeqNo) {
        if (sourceSeqNo >= 0) {
            return sourceSeqNo;
        }
        throw new IllegalArgumentException("Source sequene number cannot be negative: " + sourceSeqNo);
    }

    static short validateTemplated(final short templateId) {
        if (templateId != ADMIN_TEMPLATE_ID) {
            return templateId;
        }
        throw new IllegalArgumentException("Template ID " + templateId + " is reserved");
    }

    static int validatePayloadLength(final int payloadLength) {
        if (payloadLength >= 0) {
            return payloadLength;
        }
        throw new IllegalArgumentException("Payload length cannot be negative: " + payloadLength);
    }
}
