package com.hotelsys.cartable.androidapp.Models;

/**
 * Created by Mohammad on 12/16/2017.
 */

public class RequestLog {
    private int id,request_id,no;
    private String action;
    private Profile worker,employee;
    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequest_id() {
        return request_id;
    }

    public void setRequest_id(int request_id) {
        this.request_id = request_id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Profile getWorker() {
        return worker;
    }

    public void setWorker(Profile worker) {
        this.worker = worker;
    }

    public Profile getEmployee() {
        return employee;
    }

    public void setEmployee(Profile employee) {
        this.employee = employee;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
