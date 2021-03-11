package co.aikar.timings.internal;

import co.aikar.timings.Timing;

public interface InternalTiming extends Timing {

    /**
     * Used internally to get the actual backing Handler in the case of delegated Handlers
     *
     * @return TimingHandler
     */
    TimingHandler getTimingHandler();

}
