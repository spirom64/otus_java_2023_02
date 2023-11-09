package ru.otus.jdbc.mapper.impl;

import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T> {
    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    public EntityClassMetaData<T> getEntityClassMetaData() {
        return entityClassMetaData;
    }

    public String getSelectByIdSql() {
        return "SELECT " + entityClassMetaData.getAllFields()
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "))
                + " FROM " + entityClassMetaData.getName()
                + " WHERE " + entityClassMetaData.getIdField().getName() + " = ?";
    }

    public String getSelectAllSql() {
        return "SELECT * FROM " + entityClassMetaData.getName();
    }

    public String getUpdateSql() {
        return "UPDATE " + entityClassMetaData.getName()
                + " SET " + entityClassMetaData.getFieldsWithoutId()
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(" = ?, "))
                + " = ? WHERE " + entityClassMetaData.getIdField().getName() + " = ?";
    }

    public String getInsertSql() {
        return "INSERT INTO " + entityClassMetaData.getName() + "("
                + entityClassMetaData.getFieldsWithoutId()
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "))
                + ") values (" + entityClassMetaData.getFieldsWithoutId()
                .stream()
                .map(field -> "?")
                .collect(Collectors.joining(", ")) + ")";
    }
}
