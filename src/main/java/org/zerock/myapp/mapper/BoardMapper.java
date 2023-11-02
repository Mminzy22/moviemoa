package org.zerock.myapp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.zerock.myapp.domain.BoardKategoriesVO;
import org.zerock.myapp.domain.BoardReplyVO;
import org.zerock.myapp.domain.BoardVO;

import java.util.List;


@Mapper
public interface BoardMapper {

    // 이벤트 목록 조회: 페이지네이션과 진행 여부에 따른 이벤트 목록 조회
    public abstract List<BoardVO> findBoardList();

    // 특정 게시글 조회
    public abstract BoardVO findBoard(@Param("id")Long id);

    // 특정 게시글 조회시 조회수 증가
    public abstract Integer viewCountUp(@Param("id")Long id);

    // 사용자가 해당 게시글에 좋아요를 눌렀는지 확인
    public abstract Integer likescheck(@Param("boardId")Long boardId, @Param("memberId")Long memberId);

    // 이미 좋아요를 눌렀다면, 좋아요 취소
    public abstract Integer cancelLikes(@Param("boardId")Long boardId, @Param("memberId")Long memberId);

    // 이미 좋아요를 눌렀다면, 좋아요 취소
    public abstract Integer LikesCountDown(@Param("boardId")Long boardId);

    // 좋아요를 누르지 않았다면, 좋아요 추가
    public abstract Integer addLikes(@Param("boardId")Long boardId, @Param("memberId")Long memberId);

    // 좋아요를 누르지 않았다면, 좋아요 추가
    public abstract Integer LikesCountUp(@Param("boardId")Long boardId);

    // 좋아요 수 조회
    public abstract Integer getLikeCount(@Param("boardId")Long boardId);

    // 게시글에 해당하는 댓글 목록 조회
    public abstract List<BoardReplyVO> findBoardReplyList(@Param("id")Long id);

    // 특정 댓글 조회
    public abstract BoardReplyVO findBoardReply(@Param("id")Long id);

    // 댓글 작성
    public abstract Integer insertReply(@Param("content")String content, @Param("memberId")Long memberId, @Param("id")Long id);

    // 댓글 수정
    public abstract Integer updateBoardReply(@Param("id")Long id, @Param("content") String content);

} // end interface
