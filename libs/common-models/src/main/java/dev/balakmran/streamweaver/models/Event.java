package dev.balakmran.streamweaver.models;

import java.io.Serializable;
import java.time.Instant;

public record Event(
        String id,
        String message,
        Instant timestamp
) implements Serializable {}
