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
package org.tools4j.eventsourcing.store;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.agrona.DirectBuffer;

import org.tools4j.eventsourcing.event.Event;
import org.tools4j.eventsourcing.event.Header;

public interface InputQueue {
    Appender appender();
    Poller poller();
    long size();

    interface Appender {

        void append(int sourceId, long sourceSeqNo, short subtypeId, int userData, final long eventTimeNanosSinceEpoch,
                    DirectBuffer payload, int offset, int length);

        default void append(int sourceId, long sourceSeqNo, short subtypeId,
                            final long eventTimeNanosSinceEpoch, DirectBuffer payload) {
            append(sourceId,sourceSeqNo, subtypeId, Header.DEFAULT_USER_DATA, eventTimeNanosSinceEpoch,
                    payload, 0, payload.capacity());
        }
    }

    interface Poller {

        Poller nextIndex(long index);

        default boolean poll(final Consumer<? super Event> consumer) {
            return poll(header -> true, consumer);
        }
        boolean poll(Predicate<? super Header> condition, Consumer<? super Event> consumer);
    }
}
