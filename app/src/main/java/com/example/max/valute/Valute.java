package com.example.max.valute;

/**
 * Created by 1 on 12.05.2015.
 */
public class Valute {
    private int id;
    private String data;
    private String valute;
    private String value;

    public Valute(Integer id, String data, String valute, String value) {
        this.id = id;
        this.data = data;
        this.valute = valute;
        this.value = value;
    }
    public Integer getId(){
        return id;
    }
    public void SetId(Integer id){
        this.id = id;
    }
    public String getData(){
        return data;
    }
    public void SetData(String data){
        this.data = data;
    }
    public String getValute(){
        return valute;
    }
    public void SetValute(String valute){
        this.valute = valute;
    }
    public String getValue(){
        return value;
    }
    public void SetValue(String value){
        this.value = value;
    }
}
