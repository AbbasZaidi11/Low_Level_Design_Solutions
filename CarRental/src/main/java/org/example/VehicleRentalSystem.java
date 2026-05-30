package org.example;

import java.util.ArrayList;
import java.util.List;

public class VehicleRentalSystem {

    List<Store> storeList;
    List<User> userList;

    public VehicleRentalSystem() {
        storeList = new ArrayList<>();
        userList = new ArrayList<>();
    }

    public Store getStore(int storeId) {
        return storeList.stream().filter(store -> store.getStoreId() == storeId).findFirst().get();
    }

    public User getUser(int userId) {
        return userList.get(userId);
    }

    public void addStore(Store store) {
        storeList.add(store);
    }

    public void addUser(User user) {
        userList.add(user);
    }

    public void removeStore(int storeId) {
        storeList.remove(storeId);
    }

    public void removeUser(int userId) {
        userList.remove(userId);
    }


}
