package com.rocdev.guardianreader.models;

import com.rocdev.guardianreader.R;

/**
 * Created by piet on 22-05-17.
 *
 */

public enum Section {

    HEADLINES_AUS(
            "https://content.guardianapis.com/au",
            "Australia headlines",
            R.id.nav_headlines_aus),
    HEADLINES_UK(
            "https://content.guardianapis.com/uk",
            "UK headlines",
            R.id.nav_headlines_uk),
    HEADLINES_US(
            "https://content.guardianapis.com/us",
            "US headlines",
            R.id.nav_headlines_us),
    HEADLINES_INT(
            "https://content.guardianapis.com/international",
            "International headlines",
            R.id.nav_headlines_int),
    ART_AND_DESIGN(
            "https://content.guardianapis.com/artanddesign",
            "Art &amp; Design",
            R.id.nav_art_and_design),
    BOOKS(
            "https://content.guardianapis.com/books",
            "Books",
            R.id.nav_books),
    BUSINESS(
            "https://content.guardianapis.com/business",
            "Business",
            R.id.nav_business),
    CULTURE(
            "https://content.guardianapis.com/culture",
            "Culture",
            R.id.nav_culture),
    EDUCATION(
            "https://content.guardianapis.com/education",
            "Education",
            R.id.nav_education),
    FILM(
            "https://content.guardianapis.com/film",
            "Film",
            R.id.nav_film),
    FOOTBALL(
            "https://content.guardianapis.com/football",
            "Football",
            R.id.nav_football),
    LAW(
            "https://content.guardianapis.com/law",
            "Law",
            R.id.nav_law),
    LIFE_AND_STYLE(
            "https://content.guardianapis.com/lifeandstyle",
            "Life &amp; style",
            R.id.nav_life_and_style),
    MEDIA("https://content.guardianapis.com/media",
            "Media",
            R.id.nav_media),
    MONEY(
            "https://content.guardianapis.com/money",
            "Money",
            R.id.nav_money),
    MUSIC(
            "https://content.guardianapis.com/music",
            "Music",
            R.id.nav_music),
    NEWS_AUS(
            "https://content.guardianapis.com/australia-news",
            "News Australia",
            R.id.nav_news_australia),
    NEWS_UK(
            "https://content.guardianapis.com/uk-news",
            "News UK",
            R.id.nav_news_uk),
    NEWS_US(
            "https://content.guardianapis.com/us-news",
            "News US",
            R.id.nav_news_us),
    NEWS_WORLD(
            "https://content.guardianapis.com/world",
            "News World",
            R.id.nav_news_world),
    OPINION(
            "https://content.guardianapis.com/commentisfree",
            "Opinion",
            R.id.nav_opinion),
    POLITICS(
            "https://content.guardianapis.com/politics",
            "Politics",
            R.id.nav_politics),
    SCIENCE(
            "https://content.guardianapis.com/science",
            "Science",
            R.id.nav_science),
    SOCIETY(
            "https://content.guardianapis.com/society",
            "Society",
            R.id.nav_society),
    SPORT(
            "https://content.guardianapis.com/sport",
            "Sport",
            R.id.nav_sport),
    STAGE(
            "https://content.guardianapis.com/stage",
            "Stage",
            R.id.nav_stage),
    TECH(
            "https://content.guardianapis.com/technology",
            "Technology",
            R.id.nav_tech),
    TRAVEL(
            "https://content.guardianapis.com/travel",
            "Travel",
            R.id.nav_travel),
    TV_RADIO("https://content.guardianapis.com/tv-and-radio",
            "TV &amp; Radio",
            R.id.nav_tv_and_radio),
    WEATHER(
            "https://content.guardianapis.com/weather",
            "Weather",
            R.id.nav_weather),
    SEARCH(
            "https://content.guardianapis.com/search",
            "Search",
            -1);


    private String url = null;
    private String title = null;
    private int idNav = -1;

    private Section(String url, String title, int idNav) {
        this.url = url;
        this.title = title;
        this.idNav = idNav;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public int getIdNav() {
        return idNav;
    }
}