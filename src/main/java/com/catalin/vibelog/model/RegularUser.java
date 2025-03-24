package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="regular_users")
public class RegularUser extends User{

    @Column(name="post_count", nullable = false)
    private int postCount;

    public RegularUser() {
    }
}
