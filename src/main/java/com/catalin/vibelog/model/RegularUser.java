package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * A regular user of the Vibelog platform.
 * <p>
 * Inherits common user properties and adds user-specific preferences,
 * such as theme customization.
 */
@Getter
@Setter
@Entity
@Table(name = "regular_users")
public class RegularUser extends User {

    /**
     * The name of the UI theme chosen by the user.
     * Defaults to "default" if not set.
     */
    @Column(name = "theme", nullable = false)
    private String theme = "default";

    /**
     * Constructs a new RegularUser with default settings.
     */
    public RegularUser() {
    }
}
