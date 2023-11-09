package ru.otus.crm.model;

import ru.otus.crm.annotations.Column;
import ru.otus.crm.annotations.Id;
import ru.otus.crm.annotations.Table;

@Table(tableName = "client")
public class Client {
    @Id
    @Column(columnName = "id")
    private Long id;

    @Column(columnName = "name")
    private String name;

    public Client() {
    }

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
