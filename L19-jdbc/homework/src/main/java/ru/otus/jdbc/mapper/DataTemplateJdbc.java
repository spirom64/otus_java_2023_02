package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData<T> entitySQLMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData<T> entitySQLMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return entitySQLMetaData.getEntityClassMetaData().getEntityInstance(rs);
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        EntityClassMetaData<T> entityClassMetaData = entitySQLMetaData.getEntityClassMetaData();
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
            var entityList = new ArrayList<T>();
            try {
                while (rs.next()) {
                    entityList.add(entityClassMetaData.getEntityInstance(rs));
                }
                return entityList;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            }
        }).orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        EntityClassMetaData<T> entityClassMetaData = entitySQLMetaData.getEntityClassMetaData();
        try {
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(),
                    entityClassMetaData.getEntityFieldsWithoutIdValues(entity));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        EntityClassMetaData<T> entityClassMetaData = entitySQLMetaData.getEntityClassMetaData();
        List<Object> params = entityClassMetaData.getEntityFieldsWithoutIdValues(entity);
        params.add(entityClassMetaData.getEntityIdFieldValue(entity));

        try {
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(),
                    params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }
}
