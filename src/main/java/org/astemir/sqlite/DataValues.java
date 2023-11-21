package org.astemir.sqlite;

import org.astemir.uniblend.misc.Pair;

import java.util.LinkedList;

public class DataValues {

    private LinkedList<DataRow> rows = new LinkedList<>();

    public void addRow(DataRow row){
        rows.add(row);
    }

    public boolean isEmpty(){
        return rows.isEmpty();
    }

    public DataRow getFirst(){
        if (!rows.isEmpty()){
            return rows.get(0);
        }
        return null;
    }

    public DataRow getFirstWhere(String key,Object value){
        return getFirstWhere(Pair.of(key,value));
    }

    public LinkedList<DataRow> getWhere(String key,Object value){
        return getWhere(Pair.of(key,value));
    }
    public DataRow getFirstWhere(Pair<String, Object>... conditions){
        LinkedList<DataRow> result = getWhere(conditions);
        if (!result.isEmpty()) {
            return result.get(0);
        }else{
            return null;
        }
    }

    public LinkedList<DataRow> getWhere(Pair<String, Object>... conditions){
        LinkedList<DataRow> result = new LinkedList<>();
        for (DataRow row : rows) {
            for (Pair<String, Object> condition : conditions) {
                boolean satisfied = true;
                if (row.contains(condition.getKey())){
                    if (!row.getRaw(condition.getKey()).equals(condition.getValue())){
                        satisfied  =false;
                    }
                }else{
                    satisfied = false;
                }
                if (satisfied){
                    result.add(row);
                }
            }
        }
        return result;
    }

    public LinkedList<DataRow> getRows() {
        return rows;
    }
}
