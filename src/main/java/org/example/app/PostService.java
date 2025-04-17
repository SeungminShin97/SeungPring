package org.example.app;

import org.example.framework.annotation.Component;

@Component
public class PostService {

    public String getPost(long id) {
        return "Post id = " + id;
    }
}
