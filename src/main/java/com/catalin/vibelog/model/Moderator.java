package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="moderators")
public class Moderator extends User{

    @Column (name="reports_reviewed", nullable = false)
    private int reportsReviewed=0;

    public Moderator() {
    }
}
