package com.hotelsys.cartable.androidapp.Models;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mohammad on 12/11/2017.
 */

public class CartableItem {
    private int id,assigner_id,worker_id,count,has_response,status_id,guest_id;
    private String title,content,comment,request_type,created_at,detail;
    private Profile worker,assigner;
    private status status;
    private Guest guest;
    private CartableRequest request;
    private ArrayList<Image> images;

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssigner_id() {
        return assigner_id;
    }

    public void setAssigner_id(int assigner_id) {
        this.assigner_id = assigner_id;
    }

    public int getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(int worker_id) {
        this.worker_id = worker_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHas_response() {
        return has_response;
    }

    public void setHas_response(int has_response) {
        this.has_response = has_response;
    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getGuest_id() {
        return guest_id;
    }

    public void setGuest_id(int guest_id) {
        this.guest_id = guest_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public Profile getWorker() {
        return worker;
    }

    public void setWorker(Profile worker) {
        this.worker = worker;
    }

    public com.hotelsys.cartable.androidapp.Models.status getStatus() {
        return status;
    }

    public void setStatus(com.hotelsys.cartable.androidapp.Models.status status) {
        this.status = status;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public CartableRequest getRequest() {
        return request;
    }

    public void setRequest(CartableRequest request) {
        this.request = request;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Profile getAssigner() {
        return assigner;
    }

    public void setAssigner(Profile assigner) {
        this.assigner = assigner;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
