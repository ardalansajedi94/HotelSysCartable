package com.hotelsys.cartable.androidapp.Server;

/**
 * Created by Mohammad on 12/9/2017.
 */

public class ServerRequest {
    private int device_type,worker_id,status_id;
    private String username,password,token,device_id,title,content,action,comment,response;

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setWorker_id(int worker_id) {
        this.worker_id = worker_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
