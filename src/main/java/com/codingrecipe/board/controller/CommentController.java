package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.CommentDTO;
import com.codingrecipe.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/{boardSort}/save")
    public ResponseEntity save(@ModelAttribute CommentDTO commentDTO) {
        System.out.println("commentDTO = " + commentDTO);

        Long saveResult = commentService.save(commentDTO);
        if (saveResult != null) {
            List<CommentDTO> commentDTOList = commentService.findAll(commentDTO.getBoardId());
            return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("해당 게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{boardSort}/delete")
    public ResponseEntity delete(@RequestParam("boardId")Long boardId, @RequestParam("commentId")Long commentId,
                                 @RequestParam("password")String password) {
        CommentDTO commentDTO = commentService.findCommentById(boardId, commentId);

        if (commentDTO.getCommentPassword().equals(password)) {
            commentService.delete(commentId);
            List<CommentDTO> commentDTOList = commentService.findAll(boardId);
            if(commentDTOList != null) {
                return new ResponseEntity<>(commentDTOList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("해당 게시글이 존재하지 않습니다", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
