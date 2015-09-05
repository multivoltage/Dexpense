package com.tonini.diego.dexpense.model;

/**
 * Created by Diego on 17/04/2015.
 */
public class Category implements ICategory ,Comparable<Category> {

    private int id = -1;
    private String name;

    public Category(String name){
        setName(name);
    }
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Category another) {
        return name.compareTo(another.getName());
    }
}
