package org.example.app;

import org.example.framework.annotation.Autowired;
import org.example.framework.annotation.Component;

@Component
public class PostController {

    @Autowired
    private PostService postService;

    public String getPost() {
        return postService.getPost(1L);
    }
}
