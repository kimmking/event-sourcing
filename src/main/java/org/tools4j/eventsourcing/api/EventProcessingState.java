/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 tools4j, Marco Terzer, Anton Anufriev
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
package org.tools4j.eventsourcing.api;

import org.agrona.collections.LongLongConsumer;

/**
 * Event Processing State provides event details at different stages of processing of the event.
 * There are the following stages when event processing state can be observed:
 * - current upstream event is being processed
 * - completed the upstream event
 * - current downstream events is processed
 * - completed downstream events
 */
public interface EventProcessingState {
    int NOT_INITIALISED = -1;

    /**
     * @return Position of the event in the queue. Starts from 0 and increments by 1 (for each event).
     */
    long id();

    /**
     * @return Source of the event.
     */
    int source();

    /**
     * @return sequence within the source.
     */
    long sourceSeq();

    /**
     * @param source given source
     * @return sourceSeq of the event from the given source processed so far.
     */
    long sourceSeq(int source);

    /**
     * @return time of the event in nanos
     */
    long eventTimeNanos();

    /**
     * @return system time when the event is ingested at current stage.
     */
    long ingestionTimeNanos();

    /**
     * For each source entry containing last sourceSeq.
     * @param consumer to run
     */
    void forEachSourceEntry(LongLongConsumer consumer);

    default boolean isAheadOf(final EventProcessingState another) {
        return sourceSeq() > 0 && another.sourceSeq() > 0 &&
                sourceSeq() > another.sourceSeq(source());
    }

    default boolean isEqualTo(final EventProcessingState another) {
        return sourceSeq() == another.sourceSeq() &&
                source() == another.source();
    }

    default boolean isNotEqualTo(final EventProcessingState another) {
        return !isEqualTo(another);
    }

}
