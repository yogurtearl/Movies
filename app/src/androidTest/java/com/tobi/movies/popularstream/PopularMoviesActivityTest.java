package com.tobi.movies.popularstream;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tobi.movies.EspressoDependencies;
import com.tobi.movies.MovieApplication;
import com.tobi.movies.backend.ConfigurableBackend;
import com.tobi.movies.posterdetails.ApiMovieDetails;
import com.tobi.movies.utils.MovieRobot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PopularMoviesActivityTest {

    private static final long MOVIE_ID = 293660L;
    private static final String POSTER_PATH = "deadpool.jpg";
    private static final String MOVIE_TITLE = "Deadpool";
    private static final String MOVIE_DESCRIPTION = "Awesome movie";

    private final ActivityTestRule<PopularMoviesActivity> rule = new ActivityTestRule<PopularMoviesActivity>(PopularMoviesActivity.class) {
        @Override
        protected void beforeActivityLaunched() {
            MovieApplication movieApplication = (MovieApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
            movieApplication.setDependencies(new EspressoDependencies(backend));
        }
    };

    private final ConfigurableBackend backend = new ConfigurableBackend();

    private ApiMoviePoster apiMoviePoster;
    private ApiMovieDetails movieDetails;

    @Before
    public void setUp() throws Exception {
        apiMoviePoster = createApiMoviePoster(MOVIE_ID, POSTER_PATH);
        movieDetails = createMovieDetails(MOVIE_ID, MOVIE_TITLE, MOVIE_DESCRIPTION, POSTER_PATH);
    }

    @Test
    public void shouldShowPoster() throws Exception {
        MovieRobot
                .createRobot(backend)
                .addApiMoviePosterToRemoteDataSource(apiMoviePoster)
                .launchPopularMovies(rule)
                .checkPosterWithPathIsDisplayedAtPosition(0, POSTER_PATH);
    }

    @Test
    public void shouldNavigateToMovieDetails() throws Exception {
        MovieRobot
                .createRobot(backend)
                .addApiMoviePosterToRemoteDataSource(apiMoviePoster)
                .addApiMovieDetailsToRemoteDataSource(movieDetails)
                .launchPopularMovies(rule)
                .selectPosterAtPosition(0)
                .checkMovieTitleIsDisplayed(MOVIE_TITLE)
                .checkMovieDescriptionIsDisplayed(MOVIE_DESCRIPTION);
    }

    private ApiMoviePoster createApiMoviePoster(long movieId, String posterPath) {
        ApiMoviePoster poster = new ApiMoviePoster();
        poster.movieId = movieId;
        poster.posterPath = posterPath;
        return poster;
    }

    private ApiMovieDetails createMovieDetails(long movieId, String movieTitle, String movieOverview, String posterPath) {
        ApiMovieDetails apiMovieDetails = new ApiMovieDetails();
        apiMovieDetails.originalTitle = movieTitle;
        apiMovieDetails.movieId = movieId;
        apiMovieDetails.overview = movieOverview;
        apiMovieDetails.posterPath = posterPath;
        return apiMovieDetails;
    }
}