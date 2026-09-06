package org.example.managers;

import org.example.dao.UserDao;
import org.example.models.Event;
import org.example.models.Team;
import org.example.models.TimeSlot;
import org.example.models.User;

public class UserManager {

    private UserDao userDao;

    public UserManager() {
        this.userDao = new UserDao();
    }

    public User createUser(String username, TimeSlot timeSlot) {
        User newUser = new User(username, timeSlot);
        userDao.addUser(newUser);
        return newUser;
    }

    public User getUser(String userName) {
        return userDao.getUser(userName);
    }

    public boolean checkIfUserPartOfAnyTeam(String userName) {
        User user = userDao.getUser(userName);
        return user.getTeam().isPresent();
    }

    public void setTeam(User user, Team team) {
        userDao.setTeam(user, team);
    }

    public void addEvent(User user, Event event) {
        userDao.addEvent(user, event);
    }

}
