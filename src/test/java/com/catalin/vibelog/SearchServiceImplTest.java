package com.catalin.vibelog;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.catalin.vibelog.model.Comment;
import com.catalin.vibelog.model.RegularUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.catalin.vibelog.dto.response.MediaResponseDTO;
import com.catalin.vibelog.dto.response.PostResponse;
import com.catalin.vibelog.dto.response.UserResponseDTO;
import com.catalin.vibelog.model.Post;
import com.catalin.vibelog.model.User;
import com.catalin.vibelog.model.enums.PostStatus;
import com.catalin.vibelog.repository.CommentRepository;
import com.catalin.vibelog.repository.LikeRepository;
import com.catalin.vibelog.repository.PostRepository;
import com.catalin.vibelog.repository.UserRepository;
import com.catalin.vibelog.service.MediaService;
import com.catalin.vibelog.service.implementations.SearchServiceImpl;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private UserRepository userRepo;
    @Mock
    private PostRepository postRepo;
    @Mock
    private LikeRepository likeRepo;
    @Mock
    private CommentRepository commentRepo;
    @Mock
    private MediaService mediaService;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchUsers_ReturnsUserDtoPage() {
        String query = "alice";
        Pageable pageable = PageRequest.of(0, 5);

        User u1 = new RegularUser();
        u1.setId(10L);
        u1.setUsername("alice99");
        u1.setBio("Bio1");
        u1.setProfilePicture("pic1");

        Page<User> userPage = new PageImpl<>(List.of(u1));
        when(userRepo.findByUsernameContainingIgnoreCase(query, pageable)).thenReturn(userPage);

        Page<UserResponseDTO> result = searchService.searchUsers(query, pageable);

        assertEquals(1, result.getTotalElements());
        UserResponseDTO dto = result.getContent().get(0);
        assertEquals(10L,      dto.id());
        assertEquals("alice99", dto.username());
        assertEquals("Bio1",   dto.bio());
        assertEquals("pic1",   dto.profilePicture());
        verify(userRepo).findByUsernameContainingIgnoreCase(query, pageable);
    }

}
