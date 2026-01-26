package dev.balakmran.streamweaver.models;

import java.io.Serializable;

public record Event(
        String id,
        String message,
        String timestamp
) implements Serializable {}
