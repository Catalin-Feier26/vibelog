//package com.catalin.vibelog.service.implementations;
//
//import com.catalin.vibelog.dto.request.ReblogRequestDTO;
//import com.catalin.vibelog.dto.response.ReblogResponseDTO;
//import com.catalin.vibelog.exception.ReblogAlreadyExistsException;
//import com.catalin.vibelog.exception.ReblogNotFoundException;
//import com.catalin.vibelog.exception.PostNotFoundException;
//import com.catalin.vibelog.exception.UserNotFoundException;
//import com.catalin.vibelog.model.Reblog;
//import com.catalin.vibelog.model.User;
//import com.catalin.vibelog.model.Post;
//import com.catalin.vibelog.repository.ReblogRepository;
//import com.catalin.vibelog.repository.PostRepository;
//import com.catalin.vibelog.service.ReblogService;
//import com.catalin.vibelog.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
///**
// * Default implementation of {@link com.catalin.vibelog.service.ReblogService},
// * managing the lifecycle of Reblog entities.
// */
//@Service
//public class ReblogServiceImpl implements ReblogService {
//
//    private final ReblogRepository reblogRepo;
//    private final UserService userService;
//    private final PostRepository postRepo;
//
//    @Autowired
//    public ReblogServiceImpl(ReblogRepository reblogRepo,
//                             UserService userService,
//                             PostRepository postRepo) {
//        this.reblogRepo  = reblogRepo;
//        this.userService = userService;
//        this.postRepo    = postRepo;
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public ReblogResponseDTO reblog(ReblogRequestDTO request)
//            throws UserNotFoundException, PostNotFoundException, ReblogAlreadyExistsException {
//
//        // 1. Lookup user
//        User reblogger = userService.findByUsername(request.rebloggerUsername());
//        // 2. Lookup post entity
//        Post original = postRepo.findById(request.originalPostId())
//                .orElseThrow(() -> new PostNotFoundException(request.originalPostId()));
//        // 3. Prevent duplicate
//        if (reblogRepo.existsByUserAndOriginalPost(reblogger, original)) {
//            throw new ReblogAlreadyExistsException(reblogger.getUsername(), original.getId());
//        }
//        // 4. Create & persist
//        Reblog entity = new Reblog();
//        entity.setUser(reblogger);
//        entity.setOriginalPost(original);
//        entity.setRebloggedAt(LocalDateTime.now());
//        Reblog saved = reblogRepo.save(entity);
//
//        // 5. Map to DTO
//        return new ReblogResponseDTO(
//                saved.getId(),
//                reblogger.getUsername(),
//                original.getId(),
//                original.getAuthor().getUsername(),
//                saved.getRebloggedAt()
//        );
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public void undoReblog(ReblogRequestDTO request)
//            throws ReblogNotFoundException {
//
//        // Find the reblog to delete:
//        Reblog toDelete = reblogRepo
//                .findByUserUsernameAndOriginalPostId(
//                        request.rebloggerUsername(),
//                        request.originalPostId()
//                ).orElseThrow(() -> new ReblogNotFoundException(request.originalPostId()));
//
//        reblogRepo.delete(toDelete);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    public Page<ReblogResponseDTO> listReblogsByUser(String username, Pageable pageable) {
//        // Fetch paged Reblog entities by username
//        return reblogRepo.findByUserUsername(username, pageable)
//                .map(r -> new ReblogResponseDTO(
//                        r.getId(),
//                        r.getUser().getUsername(),
//                        r.getOriginalPost().getId(),
//                        r.getOriginalPost().getAuthor().getUsername(),
//                        r.getRebloggedAt()
//                ));
//    }
//}
