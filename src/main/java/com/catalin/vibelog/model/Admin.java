package com.catalin.vibelog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="admins")
public class Admin extends User{

    @Column(name="admin_level", nullable = false)
    private String admin_level;

    public Admin() {
    }
}
