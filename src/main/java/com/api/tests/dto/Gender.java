package com.api.tests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Gender {

    MALE("male"),
    FEMALE("female");


    private final String genderName;
}
