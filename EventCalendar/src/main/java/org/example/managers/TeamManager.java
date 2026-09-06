package org.example.managers;

import org.example.dao.TeamDao;
import org.example.models.Team;
import org.example.models.User;

import java.util.List;

public class TeamManager {

    private TeamDao teamDao;
    private UserManager userManager;

    public TeamManager(UserManager userManager) {
        this.userManager = userManager;
        this.teamDao = new TeamDao();
    }

    public Team createTeam(String teamName, List<String> userNames) {
        validateUser(userNames);
        Team team = new Team(teamName);
        for (String userName : userNames) {
            User user = userManager.getUser(userName);
            userManager.setTeam(user, team);
            team.addTeamMember(user);
        }
        teamDao.createTeam(team);
        return team;
    }

    public Team getTeam(String teamName) {
        return teamDao.getTeam(teamName);
    }

    private void validateUser(List<String> userNames) {
        for (String userName : userNames) {
            boolean userAlreadyPartOfTeam = userManager.checkIfUserPartOfAnyTeam(userName);
            if (userAlreadyPartOfTeam) {
                throw new RuntimeException("User " + userName + " already part of another team");
            }
        }
    }

}
