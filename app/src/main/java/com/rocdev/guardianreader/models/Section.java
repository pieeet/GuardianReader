package com.rocdev.guardianreader.models;


import com.rocdev.guardianreader.R;
import com.rocdev.guardianreader.database.Contract;

/**
 * Created by piet on 22-05-17.
 *
 */

public enum Section  {

    HEADLINES_AUS(
            "https://content.guardianapis.com/au",
            R.string.pref_title_headlines_aus,
            R.id.nav_headlines_aus,
            "pref_section_headlines_aus",
            R.drawable.ic_public_black_18dp),
    HEADLINES_UK(
            "https://content.guardianapis.com/uk",
            R.string.pref_title_headlines_uk,
            R.id.nav_headlines_uk,
            "pref_section_headlines_uk",
            R.drawable.ic_public_black_18dp),
    HEADLINES_US(
            "https://content.guardianapis.com/us",
            R.string.pref_title_headlines_us,
            R.id.nav_headlines_us,
            "pref_section_headlines_us",
            R.drawable.ic_public_black_18dp),
    HEADLINES_INT(
            "https://content.guardianapis.com/international",
            R.string.pref_title_headlines_world,
            R.id.nav_headlines_int,
            "pref_section_headlines_int",
            R.drawable.ic_public_black_18dp),
    ART_AND_DESIGN(
            "https://content.guardianapis.com/artanddesign",
            R.string.pref_title_art_design,
            R.id.nav_art_and_design,
            "pref_section_art_design",
            R.drawable.ic_bookmark_border_black_18dp),
    BOOKS(
            "https://content.guardianapis.com/books",
            R.string.pref_title_books,
            R.id.nav_books,
            "pref_section_books",
            R.drawable.ic_bookmark_border_black_18dp),
    BUSINESS(
            "https://content.guardianapis.com/business",
            R.string.pref_title_business,
            R.id.nav_business,
            "pref_section_business",
            R.drawable.ic_bookmark_border_black_18dp),
    CULTURE(
            "https://content.guardianapis.com/culture",
            R.string.pref_title_culture,
            R.id.nav_culture,
            "pref_section_culture",
            R.drawable.ic_bookmark_border_black_18dp),
    EDUCATION(
            "https://content.guardianapis.com/education",
            R.string.pref_title_education,
            R.id.nav_education,
            "pref_section_education",
            R.drawable.ic_bookmark_border_black_18dp),
    ENVIRONMENT(
            "https://content.guardianapis.com/environment",
            R.string.pref_title_environment,
            R.id.nav_environment,
            "pref_section_environment",
            R.drawable.ic_bookmark_border_black_18dp),
    FILM(
            "https://content.guardianapis.com/film",
            R.string.pref_title_film,
            R.id.nav_film,
            "pref_section_film",
            R.drawable.ic_bookmark_border_black_18dp),
    FOOTBALL(
            "https://content.guardianapis.com/football",
            R.string.pref_title_football,
            R.id.nav_football,
            "pref_section_football",
            R.drawable.ic_bookmark_border_black_18dp),
    LAW(
            "https://content.guardianapis.com/law",
            R.string.pref_title_law,
            R.id.nav_law,
            "pref_section_law",
            R.drawable.ic_bookmark_border_black_18dp),
    LIFE_AND_STYLE(
            "https://content.guardianapis.com/lifeandstyle",
            R.string.pref_title_life_style,
            R.id.nav_life_and_style,
            "pref_section_life_style",
            R.drawable.ic_bookmark_border_black_18dp),
    MEDIA("https://content.guardianapis.com/media",
            R.string.pref_title_media,
            R.id.nav_media,
            "pref_section_media",
            R.drawable.ic_bookmark_border_black_18dp),
    MONEY(
            "https://content.guardianapis.com/money",
            R.string.pref_title_money,
            R.id.nav_money,
            "pref_section_money",
            R.drawable.ic_bookmark_border_black_18dp),
    MUSIC(
            "https://content.guardianapis.com/music",
            R.string.pref_title_music,
            R.id.nav_music,
            "pref_section_music",
            R.drawable.ic_bookmark_border_black_18dp),
    NEWS_AUS(
            "https://content.guardianapis.com/australia-news",
            R.string.pref_title_news_australia,
            R.id.nav_news_australia,
            "pref_section_news_australia",
            R.drawable.ic_bookmark_border_black_18dp),
    NEWS_UK(
            "https://content.guardianapis.com/uk-news",
            R.string.pref_title_news_uk,
            R.id.nav_news_uk,
            "pref_section_news_uk",
            R.drawable.ic_bookmark_border_black_18dp),
    NEWS_US(
            "https://content.guardianapis.com/us-news",
            R.string.pref_title_news_us,
            R.id.nav_news_us,
            "pref_section_news_us",
            R.drawable.ic_bookmark_border_black_18dp),
    NEWS_WORLD(
            "https://content.guardianapis.com/world",
            R.string.pref_title_news_world,
            R.id.nav_news_world,
            "pref_section_news_world",
            R.drawable.ic_bookmark_border_black_18dp),
    OPINION(
            "https://content.guardianapis.com/commentisfree",
            R.string.pref_title_opinion,
            R.id.nav_opinion,
            "pref_section_opinion",
            R.drawable.ic_bookmark_border_black_18dp),
    POLITICS(
            "https://content.guardianapis.com/politics",
            R.string.pref_title_politics,
            R.id.nav_politics,
            "pref_section_politics",
            R.drawable.ic_bookmark_border_black_18dp),
    SCIENCE(
            "https://content.guardianapis.com/science",
            R.string.pref_title_science,
            R.id.nav_science,
            "pref_section_science",
            R.drawable.ic_bookmark_border_black_18dp),
    SOCIETY(
            "https://content.guardianapis.com/society",
            R.string.pref_title_society,
            R.id.nav_society,
            "pref_section_society",
            R.drawable.ic_bookmark_border_black_18dp),
    SPORT(
            "https://content.guardianapis.com/sport",
            R.string.pref_title_sport,
            R.id.nav_sport,
            "pref_section_sport",
            R.drawable.ic_bookmark_border_black_18dp),
    STAGE(
            "https://content.guardianapis.com/stage",
            R.string.pref_title_stage,
            R.id.nav_stage,
            "pref_section_stage",
            R.drawable.ic_bookmark_border_black_18dp),
    TECH(
            "https://content.guardianapis.com/technology",
            R.string.pref_title_tech,
            R.id.nav_tech,
            "pref_section_tech",
            R.drawable.ic_bookmark_border_black_18dp),
    TRAVEL(
            "https://content.guardianapis.com/travel",
            R.string.pref_title_travel,
            R.id.nav_travel,
            "pref_section_travel",
            R.drawable.ic_bookmark_border_black_18dp),
    TV_RADIO("https://content.guardianapis.com/tv-and-radio",
            R.string.pref_title_tv_radio,
            R.id.nav_tv_and_radio,
            "pref_section_tv_radio",
            R.drawable.ic_bookmark_border_black_18dp),
    WEATHER(
            "https://content.guardianapis.com/weather",
            R.string.pref_title_weather,
            R.id.nav_weather,
            "pref_section_weather",
            R.drawable.ic_bookmark_border_black_18dp),

    SAVED(Contract.ArticleEntry.CONTENT_URI.toString(),
            R.string.pref_title_saved,
            R.id.nav_saved_articles,
            "",
            R.drawable.ic_archive_black_18dp),

    // Not in sections list
    SEARCH(
            "https://content.guardianapis.com/search",
            R.string.pref_title_search,
                    -1,
                    "",
                    -1);

    private String url = null;
    private int title = -1;
    private int idNav = -1;
    private String prefKey = null;
    private int icon;

    Section(String url, int title, int idNav, String prefKey, int icon) {
        this.url = url;
        this.title = title;
        this.idNav = idNav;
        this.prefKey = prefKey;
        this.icon = icon;
    }



    public String getUrl() {
        return url;
    }

    public int getTitle() {
        return title;
    }

    public int getIdNav() {
        return idNav;
    }

    public String getPrefKey() {
        return prefKey;
    }

    public int getIcon() {
        return icon;
    }







}
