package com.example.instagram.services;

import com.example.instagram.DAOs.Post;

import java.util.ArrayList;
import java.util.List;

public class TransitPost {
    public static Post post = new Post();
    public static List<Post> postsToChangeFromOtherPage = new ArrayList<>();
    public static List<Post> postsToDeleteFromOtherPage = new ArrayList<>();
}
