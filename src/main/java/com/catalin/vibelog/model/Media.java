package com.catalin.vibelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType type;

    @Column(nullable = false)
    private String format;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String url;

    @ManyToMany(mappedBy = "mediaList")
    private List<Post> posts = new ArrayList<>();


    public Media() {
    }

}
