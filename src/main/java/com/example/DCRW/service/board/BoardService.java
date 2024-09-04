package com.example.DCRW.service.board;

import com.example.DCRW.dto.board.PostDetailDto;
import com.example.DCRW.dto.board.PostResponseDto;
import com.example.DCRW.entity.Board;
import com.example.DCRW.entity.Category;
import com.example.DCRW.entity.Post;
import com.example.DCRW.repository.BoardRepository;
import com.example.DCRW.repository.CategoryRepository;
import com.example.DCRW.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;

    // 전체
    public Page<PostResponseDto> showPost(int boardType, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Board board = boardRepository.findById(boardType)
                    .orElseThrow(() -> new IllegalArgumentException("Board Type이 존재하지 않습니다"));

            return postRepository.showPost(board, pageable);

        } catch (DataAccessException de){
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        }
        catch (Exception e){
            throw new RuntimeException("게시글 조회에 오류가 발생했습니다");
        }
    }

    // 카테고리 검색
    public Page<PostResponseDto> searchCategory(int boardType, int categoryId, int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);

            Board board = boardRepository.findById(boardType)
                    .orElseThrow(() -> new IllegalArgumentException("Board Type이 존재하지 않습니다"));

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("category type이 존재하지 않습니다"));

            return postRepository.searchPostCategory(board, pageable, category);

        } catch (DataAccessException de){
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        }
        catch (Exception e){
            throw new RuntimeException("게시글 조회에 오류가 발생했습니다");
        }
    }

    // 검색어로 검색
    public Page<PostResponseDto> searchQuery(int boardType, String query, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Board board = boardRepository.findById(boardType)
                    .orElseThrow(() -> new IllegalArgumentException("Board Type이 존재하지 않습니다"));

            query = "%"+query+"%";

            return postRepository.searchPostTitle(board, pageable, query);

        } catch (DataAccessException de){
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        }
        catch (Exception e){
            throw new RuntimeException("게시글 조회에 오류가 발생했습니다");
        }
    }

    // 카테고리, 검색어로 검색
    public Page<PostResponseDto> searchByCategoryAndQuery(int boardType, int categoryId, String query, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Board board = boardRepository.findById(boardType)
                    .orElseThrow(() -> new IllegalArgumentException("Board Type이 존재하지 않습니다"));

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("category type이 존재하지 않습니다"));

            query = "%"+query+"%";

            return postRepository.searchPostTitleCategory(board, pageable, category, query);

        } catch (DataAccessException de){
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        }
        catch (Exception e){
            throw new RuntimeException("게시글 조회에 오류가 발생했습니다");
        }
    }

    // 게시글 상세 조회
    public PostDetailDto detailPost(int postId) {
        try{
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));

            PostDetailDto postDetailDto = PostDetailDto.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(post.getCategory().getCategoryId())
                    .file(post.getFileList())
                    .build();

            return postDetailDto;

        } catch (Exception e){
            throw new RuntimeException("게시글 상세 조회에 오류가 발생했습니다");
        }
    }
}
