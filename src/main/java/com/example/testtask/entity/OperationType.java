package com.example.testtask.entity;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum OperationType {
    @JsonEnumDefaultValue
    INVALID_TYPE,
    DEPOSIT,
    WITHDRAW;
}
