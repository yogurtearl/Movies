package com.movies.tobi.popularmovies.popularstream;

import android.support.annotation.NonNull;

import com.movies.tobi.popularmovies.Converter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PopularStreamRepository {

    private final PopularStreamApiDatasource popularStreamApiDatasource;
    private final Scheduler subscribeScheduler;
    private final Scheduler observeScheduler;
    private final Converter<ApiMoviePoster, MoviePoster> posterConverter;

    PopularStreamRepository(PopularStreamApiDatasource popularStreamApiDatasource, Scheduler subscribeScheduler, Scheduler observeScheduler,
                            Converter<ApiMoviePoster, MoviePoster> posterConverter) {
        this.popularStreamApiDatasource = popularStreamApiDatasource;
        this.subscribeScheduler = subscribeScheduler;
        this.observeScheduler = observeScheduler;
        this.posterConverter = posterConverter;
    }

    public PopularStreamRepository(PopularStreamApiDatasource popularStreamApiDatasource) {
        this(popularStreamApiDatasource, Schedulers.io(), AndroidSchedulers.mainThread(), new ApiMoviePosterConverter());
    }

    public Observable<List<MoviePoster>> getPopularPosters() {
        return popularStreamApiDatasource.getPopularPosters()
                .map(toMoviePosters())
                .observeOn(observeScheduler)
                .subscribeOn(subscribeScheduler);
    }

    @NonNull
    private Func1<List<ApiMoviePoster>, List<MoviePoster>> toMoviePosters() {
        return new Func1<List<ApiMoviePoster>, List<MoviePoster>>() {
            @Override
            public List<MoviePoster> call(List<ApiMoviePoster> apiMoviePosters) {
                List<MoviePoster> result = new ArrayList<>(apiMoviePosters.size());

                for (ApiMoviePoster apiMoviePoster : apiMoviePosters) {
                    result.add(toMoviePoster(apiMoviePoster));
                }

                return result;
            }
        };
    }

    private MoviePoster toMoviePoster(ApiMoviePoster apiMoviePoster) {
        return posterConverter.convert(apiMoviePoster);
    }

}
