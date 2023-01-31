package com.codingrecipe.board.controller;

import com.codingrecipe.board.dto.BoardDTO;
import com.codingrecipe.board.dto.CommentDTO;
import com.codingrecipe.board.service.BoardService;
import com.codingrecipe.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping("/{boardSort}/save")
    public String saveForm(@PathVariable String boardSort, Model model) {
        model.addAttribute("boardSort", boardSort);
        return "save";
    }

    @PostMapping("/{boardSort}/save")
    public String save(@ModelAttribute BoardDTO boardDTO) throws IOException {
        System.out.println("boardDTO = " + boardDTO);
        boardService.save(boardDTO);
        return "redirect:/board/" + boardDTO.getBoardSort();
    }

//    @GetMapping("/")
//    public String findAll(Model model) {
//        // DB에서 전체 게시글 데이터를 가져와서 list.html에 보여준다.
//        List<BoardDTO> boardDTOList = boardService.findAll();
//        model.addAttribute("boardList", boardDTOList);
//        return "index";
//    }

    @GetMapping("/{boardSort}/{id}")
    public String findById(@PathVariable String boardSort, @PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable) {
        /*
            해당 게시글의 조회수를 하나 올리고
            게시글 데이터를 가져와서 detail.html에 출력
         */
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        /* 댓글 목록 가져오기 */
        List<CommentDTO> commentDTOList = commentService.findAll(id);
        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());
        return "detail";
    }

    @GetMapping("/{boardSort}/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "update";
    }

    @PostMapping("/{boardSort}/update")
    public String update(@ModelAttribute BoardDTO boardDTO, Model model) {
        BoardDTO board = boardService.update(boardDTO);
        model.addAttribute("board", board);
        return "redirect:/board/" + boardDTO.getBoardSort() + "/" + boardDTO.getId();
    }

    @GetMapping("/{boardSort}/delete/{id}")
    public String delete(@PathVariable Long id, @PathVariable String boardSort) {
        boardService.delete(id);
        return "redirect:/board/" + boardSort + "/";
    }

    @GetMapping("/{boardSort}")
    public String sortPaging(@PageableDefault(page = 1) Pageable pageable, @PathVariable String boardSort, Model model) {

        int pageLimit = 3;
        Page<BoardDTO> boardList = boardService.sortPaging(pageable, boardSort, pageLimit);

//        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1; // 1 4 7 10 ~~
//        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages() == 0 ? -1 : boardList.getTotalPages();
        int totalPages = boardList.getTotalPages();
        int currentPage = pageable.getPageNumber();
        int startPage = currentPage - 3 < 1 ? 1 : currentPage - 2;
        int endPage = startPage + 4 > totalPages ? totalPages : startPage + 4;

        System.out.println("startPage = " + startPage);
        System.out.println("endPage = " + endPage);
        System.out.println("currentPage = " + currentPage);

        model.addAttribute("boardList", boardList);
        model.addAttribute("boardSort", boardSort);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("pageLimit", pageLimit);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", boardList.getTotalElements());

        return "paging";
    }
}










