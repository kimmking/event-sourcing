/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 mmap (tools4j), Marco Terzer
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
package org.tools4j.mmap.io;

import java.io.Closeable;
import java.util.Objects;

/**
 * A mapped region of a file.
 */
public final class MappedRegion implements Closeable {

    private static final int MB = 1024 * 1024;
    private static final int MIN_FILE_SIZE   = 4 * MB;
    private static final int MAX_FILE_GROWTH = 64 * MB;

    public enum Mode {
        READ_ONLY,
        READ_WRITE;
    }

    private final MappedFile file;
    private final Mode mode;
    private long position;
    private long address;
    private long fileSize;

    public MappedRegion(final MappedFile file, final Mode mode) {
        this.file = Objects.requireNonNull(file);
        this.mode = Objects.requireNonNull(mode);
        this.position = -1;
        this.address = 0;
    }

    public MappedFile getFile() {
        return file;
    }

    public long getSize() {
        return file.getRegionSize();
    }

    public MappedRegion map(final long position) {
        if (this.position >= 0) {
            throw new IllegalStateException("Already mapped to position " + this.position);
        }
//        final long t0 = System.nanoTime();
//        try {
            ensureFileLength(position + file.getRegionSize());
            this.address = RegionMapper.map(file.getFileChannel(), mode == Mode.READ_ONLY, position, file.getRegionSize());
            this.position = position;
//        } finally {
//            final long dt = System.nanoTime() - t0;
//            if (dt > 100000) {
//                System.out.println("map: dt=" + dt / 1000f + " micros");
//            }
//        }
        return this;
    }

    public MappedRegion mapSameAs(final MappedRegion other) {
        if (this.position >= 0) {
            throw new IllegalStateException("Already mapped to position " + this.position);
        }
        this.address = other.address;
        this.position = other.position;
        return this;
    }

    public MappedRegion unmap() {
        if (position < 0) {
            throw new IllegalStateException("Region is not mapped");
        }
        RegionMapper.unmap(file.getFileChannel(), address, file.getRegionSize());
        position = -1;
        address = 0;
        return this;
    }

    public long unmapExternal() {
        if (position < 0) {
            throw new IllegalStateException("Region is not mapped");
        }
        final long addr = address;
        position = -1;
        address = 0;
        return addr;
    }

    public long getPosition() {
        return position;
    }

    public long getPosition(final long offset) {
        return position + offset;
    }

    public long getAddress() {
        ensureMapped();
        return address;
    }

    public long getAddress(final long offset) {
        return ensureMapped().getAddress() + offset;
    }

    public boolean isMapped() {
        return position >= 0;
    }

    public MappedRegion ensureMapped() {
        if (position >= 0) {
            return this;
        }
        throw new RuntimeException("Region is not mapped");
    }

    @Override
    public void close() {
        if (position >= 0) {
            unmap();
        }
    }

    private void ensureFileLength(final long minLen) {
        if (fileSize < minLen) {
            final long len = file.getFileLength();
            if (len < minLen) {
                final long newLen;
                if (minLen < MIN_FILE_SIZE) {
                    newLen = MIN_FILE_SIZE;
                } else {
                    if (len < MAX_FILE_GROWTH) {
                        newLen = Math.max(minLen, 2 * len);
                    } else {
                        newLen = Math.max(minLen, len + MAX_FILE_GROWTH);
                    }
                }
                file.setFileLength(newLen);
                fileSize = newLen;
            } else {
                fileSize = len;
            }
        }
    }

}
