package org.astemir.sqlite;

public class DatabaseField {

    private DatabaseFieldType type;
    private String fieldName;
    private int length = -1;
    private boolean notNull = false;
    private boolean primaryKey = false;
    private boolean autoIncrement = false;

    public DatabaseField(DatabaseFieldType type, String fieldName){
        this.type = type;
        this.fieldName = fieldName;
    }

    public static DatabaseField init(DatabaseFieldType type, String fieldName){
        return new DatabaseField(type,fieldName);
    }

    public DatabaseField length(int length){
        this.length = length;
        return this;
    }

    public DatabaseField notNull(){
        notNull = true;
        return this;
    }

    public DatabaseField primaryKey(){
        this.primaryKey = true;
        return this;
    }

    public DatabaseField autoIncrement(){
        this.autoIncrement = true;
        return this;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(fieldName+" ");
        builder.append(type.toString()+" ");
        if (primaryKey){
            builder.append("PRIMARY KEY"+" ");
        }
        if (autoIncrement){
            builder.append("AUTOINCREMENT"+" ");
        }
        if (notNull){
            builder.append("NOT NULL"+" ");
        }
        return builder.toString();
    }

    public static DatabaseField fieldInt(String name){
        return new DatabaseField(DatabaseFieldType.INTEGER,name);
    }
    public static DatabaseField fieldBoolean(String name){
        return new DatabaseField(DatabaseFieldType.BOOLEAN,name);
    }
    public static DatabaseField fieldString(String name,int length){
        return new DatabaseField(DatabaseFieldType.VARCHAR,name).length(length);
    }

    public static DatabaseField fieldString(String name){
        return new DatabaseField(DatabaseFieldType.VARCHAR,name);
    }

    public static DatabaseField fieldId(String name){
        return fieldInt(name).primaryKey().autoIncrement().notNull();
    }
}
