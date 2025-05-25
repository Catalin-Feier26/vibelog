//package com.catalin.vibelog.dto.response;
//
//import java.time.LocalDateTime;
//
///**
// * Response DTO for a reblog action.
// *
// * @param id                       the unique ID of the reblog record
// * @param rebloggerUsername        the user who performed the reblog
// * @param originalPostId           the ID of the original post
// * @param originalAuthorUsername   the username of the original post’s author
// * @param rebloggedAt              timestamp when the reblog occurred
// */
//public record ReblogResponseDTO(
//        Long id,
//        String rebloggerUsername,
//        Long originalPostId,
//        String originalAuthorUsername,
//        LocalDateTime rebloggedAt
//) { }