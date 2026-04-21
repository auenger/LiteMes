package com.litemes.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Example entity to validate the project skeleton.
 * Demonstrates the standard entity pattern with soft delete.
 */
@TableName("example_entity")
public class ExampleEntity extends SoftDeleteEntity {

    private String name;
    private String description;

    public ExampleEntity() {
    }

    public ExampleEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
