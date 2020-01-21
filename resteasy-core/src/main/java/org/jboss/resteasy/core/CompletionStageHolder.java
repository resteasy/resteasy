package org.jboss.resteasy.core;

import java.util.concurrent.CompletionStage;

/**
 * Need to distinguish from a param or property that is actually a CompletionStage from
 * an async injection
 *
 */
public class CompletionStageHolder {
    private final CompletionStage stage;

    public CompletionStageHolder(final CompletionStage stage) {
        this.stage = stage;
    }

    public CompletionStage getStage() {
        return stage;
    }
}
