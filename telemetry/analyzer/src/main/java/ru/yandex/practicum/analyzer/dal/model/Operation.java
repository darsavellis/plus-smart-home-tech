package ru.yandex.practicum.analyzer.dal.model;

public interface Operation {
    boolean apply(Integer left, Integer right);
}
