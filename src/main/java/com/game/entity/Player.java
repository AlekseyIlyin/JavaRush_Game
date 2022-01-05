package com.game.entity;

import com.game.component.LongToDateConverter;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 12)
    private String name;

    @Column(name = "title", length = 30)
    private String title;

    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    private Race race;

    @Column(name = "profession")
    @Enumerated(EnumType.STRING)
    private Profession profession;

    @Column(name = "birthday")
    @Convert(converter = LongToDateConverter.class)
    private Long birthday;

    @Column(name = "banned")
    private Boolean banned;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "level")
    private Integer level;

    @Column(name = "untilNextLevel")
    private Integer untilNextLevel;

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Long getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player that = (Player) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(title, that.title) &&
                race == that.race &&
                profession == that.profession &&
                Objects.equals(birthday, that.birthday) &&
                Objects.equals(banned, that.banned) &&
                Objects.equals(experience, that.experience) &&
                Objects.equals(level, that.level) &&
                Objects.equals(untilNextLevel, that.untilNextLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, race, profession, birthday, banned, experience, level, untilNextLevel);
    }

    @Override
    public String toString() {
        LocalDate birthday = Instant.ofEpochMilli(this.birthday).atZone(ZoneId.systemDefault()).toLocalDate();
        return "Player{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday=" + birthday +
                '}';
    }

}