package org.example.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.example.entities.Movie;
import org.example.entities.Show;
import org.example.entities.Theatre;
import org.example.enums.City;
import org.example.service.TheatreService;

public class TheatreController {
    private final TheatreService theatreService;

    public TheatreController() {
        this.theatreService = new TheatreService();
    }

    public void addTheatre(Theatre theatre) {
        theatreService.addTheatre(theatre);
    }

    public Set<Movie> getMovies(City city, LocalDate date) {
        return theatreService.getMovies(city, date);
    }

    public List<Theatre> getTheatres(City city, Movie movie, LocalDate date) {
        return theatreService.getTheatres(city, movie, date);
    }

    public List<Show> getShows(Movie movie, LocalDate date, Theatre theatre) {
        return theatreService.getShows(movie, date, theatre);
    }


}
