package com.hotelsys.cartable.androidapp.Server;

import com.hotelsys.cartable.androidapp.Models.BlogItem;
import com.hotelsys.cartable.androidapp.Models.CartableItem;
import com.hotelsys.cartable.androidapp.Models.Category;
import com.hotelsys.cartable.androidapp.Models.Profile;
import com.hotelsys.cartable.androidapp.Models.RequestAction;
import com.hotelsys.cartable.androidapp.Models.RequestLog;
import com.hotelsys.cartable.androidapp.Models.Role;

import java.util.ArrayList;

/**
 * Created by Mohammad on 12/9/2017.
 */

public class ServerResponse {
    private String message,jwt;
    private Profile profile;
    private ArrayList<CartableItem> tasks;
    private ArrayList<Role> roles;
    private ArrayList<Profile> users;
    private CartableItem request;
    private ArrayList<RequestAction> actions;
    private ArrayList<RequestLog> logs;
    private ArrayList<Category> categories;
    private ArrayList<BlogItem> HotelNews;
    private ArrayList<BlogItem> Instructions;
    private BlogItem TheNews;
    private BlogItem Instruction;

    public ArrayList<BlogItem> getHotelNews() {
        return HotelNews;
    }

    public ArrayList<BlogItem> getInstructions() {
        return Instructions;
    }

    public BlogItem getTheNews() {
        return TheNews;
    }

    public BlogItem getInstruction() {
        return Instruction;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public ArrayList<RequestLog> getLogs() {
        return logs;
    }

    public ArrayList<RequestAction> getActions() {
        return actions;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public ArrayList<Profile> getUsers() {
        return users;
    }

    public String getMessage() {
        return message;
    }

    public String getJwt() {
        return jwt;
    }

    public Profile getProfile() {
        return profile;
    }

    public ArrayList<CartableItem> getTasks() {
        return tasks;
    }

    public CartableItem getRequest() {
        return request;
    }
}
