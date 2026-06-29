package com.example.distributedid.common;

public interface IdGenerator {

    long generate();

    default String generateAsString() {
        return String.valueOf(generate());
    }

}