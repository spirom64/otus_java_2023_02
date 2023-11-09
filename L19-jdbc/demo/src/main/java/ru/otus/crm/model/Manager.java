package ru.otus.crm.model;

import ru.otus.crm.annotations.Column;
import ru.otus.crm.annotations.Id;
import ru.otus.crm.annotations.Table;

@Table(tableName = "manager")
public class Manager {
    @Id
    @Column(columnName = "no")
    private Long no;

    @Column(columnName = "label")
    private String label;

    @Column(columnName = "param1")
    private String param1;

    public Manager() {
    }

    public Manager(String label) {
        this.label = label;
    }

    public Manager(Long no, String label, String param1) {
        this.no = no;
        this.label = label;
        this.param1 = param1;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "no=" + no +
                ", label='" + label + '\'' +
                '}';
    }
}
