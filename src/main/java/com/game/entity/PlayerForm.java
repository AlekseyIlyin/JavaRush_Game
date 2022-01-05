package com.game.entity;

import java.util.Calendar;
import java.util.Date;

public class PlayerForm {

    private String name;
    private String title;
    private Race race;
    private Profession profession;
    private Long birthday;
    private Boolean banned;
    private Integer experience;
    private Integer birthday_year;

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Race getRace() {
        return race;
    }

    public Profession getProfession() {
        return profession;
    }

    public Long getBirthday() {
        return birthday;
    }

    public Boolean getBanned() {
        return banned;
    }

    public Integer getExperience() {
        return experience;
    }

    private boolean isValidBirthdayYear(boolean defaultIfNullBirthday) {
        if (this.birthday != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(this.birthday));
            this.birthday_year = cal.get(Calendar.YEAR);
            return this.birthday_year >= 2000 && this.birthday_year <= 3000;
        } else {
            return defaultIfNullBirthday;
        }
    }

    public boolean isValidDataForCreate() {
        return
            (this.name != null && this.name.length() <= 12)    &&
            (this.title != null && this.title.length() <= 30)   &&
            this.race != null    &&
            this.profession != null &&
            isValidBirthdayYear(false) &&
            (this.experience != null && (this.experience >= 0 && this.experience <= 10_000_000));
    }

    public boolean isValidData() {
        return
            isValidBirthdayYear(true) &&
            (this.experience == null || (this.experience >= 0 && this.experience <= 10_000_000))   &&
            (this.birthday == null || this.birthday >= 0);
    }

    public Player getPlayerEntity() {
        Player player = new Player();
        player.setName(this.name);
        player.setTitle(this.title);
        player.setRace(this.race);
        player.setProfession(this.profession);
        player.setBirthday(this.birthday);
        player.setBanned(this.banned != null && this.banned);
        player.setExperience(this.experience);
        return player;
    }
}
