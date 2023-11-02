package org.zerock.myapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.myapp.domain.BoardReplyVO;
import org.zerock.myapp.domain.BoardVO;
import org.zerock.myapp.service.BoardService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor

@RequestMapping("/board/*")
@Controller
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/boards")
    public String boardView(Model model) {
        log.trace("boardView() invoked.");

        List<BoardVO> boardList = this.boardService.findBoardList();

        model.addAttribute("boards", boardList);

        return "/board/boards";
    } // boardView

    @GetMapping("/writeBoard")
    public String writeBoardView() {
        log.trace("writeBoardView() invoked.");

        return "/board/writeBoard";
    } // writeBoardView

    @PostMapping ("/writeBoard")
    public String writeBoard() {
        log.trace("writeBoard() invoked.");

        return "redirect:/board/detailBoard"; // /board/detailBoard?boardNum=***
    } // writeBoard

    @GetMapping("/updateBoard/{id}")
    public String updateBoardView(Model model, @PathVariable("id") Long id) {
        log.trace("updateBoardView() invoked.");

        return "/board/updateBoard";
    } // updateBoardView

    @PostMapping ("/updateBoard")
    public String updateBoard() {
        log.trace("updateBoard() invoked.");

        return "redirect:/board/detailBoard"; // /board/detailBoard?boardNum=***
    } // updateBoard

    @GetMapping("/detailBoard/{id}") //{/detailBoard/boardNum}
    public String detailBoardView(Model model, @PathVariable("id") Long id) {
        log.trace("detailBoardView({}) invoked.", id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            log.info("\t+ 인증된 사용자");
            String username = authentication.getName();
            Long memberId = Long.valueOf(username);
            model.addAttribute("currentUserId", memberId);
        }

        Integer viewCountUp = this.boardService.viewCountUp(id);
        BoardVO board = this.boardService.findBoard(id);
        List<BoardReplyVO> boardReplyList = this.boardService.findBoardReplyList(id);

        model.addAttribute("board", board);
        model.addAttribute("boardReplyList", boardReplyList);

        return "/board/detailBoard";
    } // detailBoardView

    @PostMapping("/like/{boardId}")
    public ResponseEntity<?> toggleLike(@PathVariable Long boardId) {
        log.trace("toggleLike({}) invoked.", boardId);
        // 현재 인증된 사용자의 정보를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 이름(여기서는 사용자 ID로 사용)을 가져옵니다.
        String username = authentication.getName();
        // 사용자 이름(아이디)를 Long 타입으로 변환합니다.
        Long memberId = Long.valueOf(username);

        try {
            // 좋아요 상태 체크 (좋아요 안누른 상태 = false)
            boolean likesCheck = boardService.likescheck(boardId, memberId);
            log.info("\t+ likescheck: {}", likesCheck);

            if (!likesCheck) {
                log.info("\t+ 좋아요 누르지 않음, 좋아요 추가");
                // 좋아요를 누르지 않았다면, 좋아요 추가
                boardService.addLikes(boardId, memberId);
                boardService.LikesCountUp(boardId);
            } else {
                log.info("\t+ 좋아요 이미 누름, 좋아요 취소");
                // 좋아요를 이미 눌렀다면, 좋아요 취소
                boardService.cancelLikes(boardId, memberId);
                boardService.LikesCountDown(boardId);
            }

            // 업데이트된 좋아요 수 조회
            Integer likeCount = boardService.getLikeCount(boardId);

            Map<String, Object> response = new HashMap<>();
            response.put("liked", !likesCheck);
            response.put("likeCount", likeCount);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 예외 처리
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }

    @PostMapping ("/comment")
    public String writereply(Model model, String content, Long memberId, Long id) {
        log.trace("writereply({}, {}, {}) invoked.", content, memberId, id);

        Integer insertReply = this.boardService.insertReply(content, memberId, id);

        log.info("\t+ insertReply: {}", insertReply);

        return "redirect:/board/detailBoard/"+ id;
    } // writereply

    // 댓글 수정 폼을 보여주는 메서드
    @GetMapping("/getReplyContent/{replyId}")
    public ResponseEntity<?> getReplyContentView(@PathVariable("replyId") Long replyId) {
        log.trace("getReplyContent({}) invoked.", replyId);
        try {
            // 댓글 정보 불러와서 리턴하기
            BoardReplyVO boardReply = boardService.findBoardReply(replyId);
            log.info("\t+ boardReply: {}", boardReply);
            return ResponseEntity.ok().body(Collections.singletonMap("reply", boardReply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 내용을 불러오는데 실패했습니다.");
        }
    }

    @PutMapping("/commentEdit")
    public String editComment(Long id, String content) {
        log.trace("editComment({}, {}) invoked.", id, content);

        Integer updated = this.boardService.updateBoardReply(id, content);
        BoardReplyVO boardReply = this.boardService.findBoardReply(id);

        log.info("\t+ updated: {}", updated);

        // 댓글 목록이나 상세보기 페이지 등으로 리다이렉트
        return "redirect:/board/detailBoard/" + boardReply.getBoardId();
    }

    @GetMapping("/reportBoard")
    public String reportBoardView() {
        log.trace("reportBoardView() invoked.");

        return "/board/reportBoard";
    } // reportBoardView

    @PostMapping("/reportBoard")
    public String reportBoardWrite() {
        log.trace("reportBoardWrite() invoked.");

        return "redirect:/board/detailBoard";
    } // reportReplyWrite

    @GetMapping("/reportReply")
    public String reportReplyView() {
        log.trace("reportReplyView() invoked.");

        return "/board/reportReply";
    } // reportReplyView

    @PostMapping("/reportReply")
    public String reportReplyWrite() {
        log.trace("reportReplyWrite() invoked.");

        return "redirect:/board/detailBoard";
    } // reportReplyWrite

} // end class
