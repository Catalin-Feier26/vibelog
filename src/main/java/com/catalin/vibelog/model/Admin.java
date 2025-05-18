package com.catalin.vibelog.model;

import com.catalin.vibelog.model.enums.AdminLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * An administrator user with elevated system privileges.
 * <p>
 * Admins can perform user management, view analytics, and
 * carry out system-wide operations based on their level.
 */
@Entity
@Getter
@Setter
@Table(name = "admins")
public class Admin extends User {

    /**
     * The privilege level of this administrator,
     * determining the scope of actions allowed.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_privilege_level", nullable = false)
    private AdminLevel adminLevel = AdminLevel.FULL;

    /**
     * Constructs a new Admin with default FULL privilege level.
     */
    public Admin() {
    }
}
