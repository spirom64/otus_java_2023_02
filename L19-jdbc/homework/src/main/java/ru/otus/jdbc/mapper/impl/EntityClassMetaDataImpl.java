package ru.otus.jdbc.mapper.impl;

import ru.otus.crm.annotations.Column;
import ru.otus.crm.annotations.Id;
import ru.otus.crm.annotations.Table;
import ru.otus.jdbc.mapper.EntityClassMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Logger logger = Logger.getLogger(EntityClassMetaDataImpl.class.getName());

    private final Class<T> modelClass;

    public EntityClassMetaDataImpl(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    public String getName() {
        if (modelClass.isAnnotationPresent(Table.class)) {
            return modelClass.getAnnotation(Table.class).tableName();
        }

        return modelClass.getSimpleName().toLowerCase();
    }

    @SuppressWarnings("unchecked")
    public Constructor<T> getConstructor() {
        return (Constructor<T>) Arrays.stream(modelClass.getDeclaredConstructors())
                .min(Comparator.comparing(Constructor::getParameterCount))
                .orElse(null);
    }

    private Object getFieldValue(Field field, Object entity) throws IllegalAccessException {
        if (field.canAccess(entity)) {
            return field.get(entity);
        }

        field.setAccessible(true);
        Object result = field.get(entity);
        field.setAccessible(false);
        return result;
    }

    private void setFieldValue(Field field, Object entity, Object value) throws IllegalAccessException {
        if (field.canAccess(entity)) {
            field.set(entity, value);
            return;
        }

        field.setAccessible(true);
        field.set(entity, value);
        field.setAccessible(false);
    }

    public T getEntityInstance(ResultSet rs) {
        Constructor<T> constructor = getConstructor();
        T instance;

        if (constructor == null) {
            return null;
        }

        try {
            instance = constructor.newInstance();
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return null;
        }

        for (Field field: getAllFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column columnData = field.getAnnotation(Column.class);
                try {
                    setFieldValue(field, instance, rs.getObject(columnData.columnName()));
                } catch (Exception e) {
                    logger.warning(e.getMessage());
                }
            }
        }

        return instance;
    }

    public Field getIdField() {
        for (Field field: modelClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }

        return null;
    }

    public List<Field> getAllFields() {
        return Arrays.stream(modelClass.getDeclaredFields()).toList();
    }

    public List<Field> getFieldsWithoutId() {
        return Arrays.stream(modelClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(Column.class))
                .toList();
    }

    public List<Object> getEntityFieldsWithoutIdValues(T entity) {
        List<Object> result = new ArrayList<>();

        try {
            for (Field field : getFieldsWithoutId()) {
                result.add(getFieldValue(field, entity));
            }
        } catch (Exception e) {
            logger.warning("Unknown exception on retrieving field values: " + e);
        }

        return result;
    }

    public Object getEntityIdFieldValue(T entity) {
        Field idField = getIdField();

        try {
            return getFieldValue(idField, entity);
        } catch (Exception e) {
            logger.warning("Unknown exception on retrieving id field value: " + e);
        }

        return null;
    }
}
