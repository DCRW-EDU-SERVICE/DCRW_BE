package com.example.DCRW.service.board;

import com.example.DCRW.dto.board.*;
import com.example.DCRW.dto.user.CustomUserDetails;
import com.example.DCRW.entity.*;
import com.example.DCRW.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    // 전체
    public Page<PostResponseDto> showPost(int boardType, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Board board = boardRepository.findById(boardType)
                    .orElseThrow(() -> new IllegalArgumentException("Board Type이 존재하지 않습니다"));

            return postRepository.showPost(board, pageable);

        } catch (DataAccessException de) {
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        } catch (Exception e) {
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

        } catch (DataAccessException de) {
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        } catch (Exception e) {
            throw new RuntimeException("게시글 조회에 오류가 발생했습니다");
        }
    }

    // 검색어로 검색
    public Page<PostResponseDto> searchQuery(int boardType, String query, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);

            Board board = boardRepository.findById(boardType)
                    .orElseThrow(() -> new IllegalArgumentException("Board Type이 존재하지 않습니다"));

            query = "%" + query + "%";

            return postRepository.searchPostTitle(board, pageable, query);

        } catch (DataAccessException de) {
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        } catch (Exception e) {
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

            query = "%" + query + "%";

            return postRepository.searchPostTitleCategory(board, pageable, category, query);

        } catch (DataAccessException de) {
            de.printStackTrace();
            throw new RuntimeException("데이터베이스 접근 중 오류");
        } catch (Exception e) {
            throw new RuntimeException("게시글 조회에 오류가 발생했습니다");
        }
    }

    // 게시글 상세 조회
    public PostDetailDto detailPost(int postId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));

            // 파일 정보를 FileDto 리스트로 변환 (URL 포함)
            List<FileDto> fileList = post.getFileList().stream()
                    .map(file -> FileDto.builder()
                            .fileId(file.getFileId())
                            .fileName(file.getFileName())
                            .fileUrl("http://localhost:8080/post/" + postId + "/" + fileService.encodeFileName(file.getFileName()))
                            .fileType(file.getFileType())
                            .build())
                    .collect(Collectors.toList());

            // 게시글 상세 정보에 파일 리스트 포함
            PostDetailDto postDetailDto = PostDetailDto.builder()
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(post.getCategory().getCategoryId())
                    .file(fileList)  // 파일 리스트
                    .build();

            return postDetailDto;

        } catch (Exception e) {
            throw new RuntimeException("게시글 상세 조회에 오류가 발생했습니다 " + e.getMessage());
        }
    }


    // 게시글 등록
    @Transactional
    public int addPosts(PostAddDto postAddDto, List<File> files) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            Users users = usersRepository.findById(customUserDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

            Board board = boardRepository.findById(postAddDto.getBoardId())
                    .orElseThrow(() -> new IllegalArgumentException("게시판이 존재하지 않습니다"));

            Category category = categoryRepository.findById(postAddDto.getCategory())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다"));


            Post post = Post.builder()
                    .title(postAddDto.getTitle())
                    .content(postAddDto.getContent())
                    .users(users)
                    .board(board)
                    .category(category)
                    .postDate(LocalDateTime.now())
                    .fileList(files)
                    .build();

            for (File file : files) {
                file.setPost(post);

                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/post/" + post.getPostId(); // static 경로 + postId
                Path filePath = Paths.get(uploadDir, file.getFileName()); // 전체 경로 생성 후 파일 저장

                file.setFilePath(filePath.toString());
            }

            int postId = postRepository.save(post).getPostId();

            return postId;
        } catch (Exception e) {
            throw new RuntimeException("게시글 업로드에 실패했습니다");
        }

    }


    // 게시글 삭제
    @Transactional
    public void removePost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

        List<File> files = post.getFileList();

        // 데이터베이스에서 게시글 삭제
        postRepository.delete(post);

        // 트랜잭션 성공 후 파일을 비동기로 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                try {
                    fileService.deleteFilesAfterCommit(files);  // 비동기 파일 삭제 처리
                } catch (IOException e) {
                    throw new RuntimeException("파일 삭제에 오류가 발생했습니다 " + e.getMessage());
                }
            }
        });
    }

    // 게시글 수정
    @Transactional
    public void updatePost(int postId, PostUpdateDto postUpdateDto, List<MultipartFile> filesToAdd) {

        // 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        try {
            postUpdateDto.getTitle().ifPresent(post::setTitle); // title 값이 있을 때만 업데이트
            postUpdateDto.getContent().ifPresent(post::setContent); // content 값이 있을 때만 업데이트

            postRepository.save(post); // 변경된 엔티티 저장
        } catch (Exception e){
            throw new RuntimeException("제목, 내용 수정에 오류가 발생했습니다. " + e.getMessage());
        }

        try {
            List<File> deleteList = new ArrayList<>();
            // 파일 삭제 요청 있는 경우
            if (postUpdateDto.getFileDelete() != null) {
                for (int fileId : postUpdateDto.getFileDelete()) {
                    // 파일 유무 확인
                    File file = fileRepository.findById(fileId)
                            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없어요"));

                    deleteList.add(file);
                    fileRepository.delete(file); // 파일 db에서 삭제
                }
                fileService.deleteFilesAfterCommit(deleteList);
            }

            // 파일 추가 요청 있는 경우
            List<File> fileList;

            if (filesToAdd != null && !filesToAdd.isEmpty()) {
                // 파일 엔티티 리스트 세팅
                fileList = fileService.settingFile(filesToAdd);

                for(File file : fileList){
                    // post 설정
                    file.setPost(post);

                    String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/post/" + post.getPostId(); // static 경로 + postId
                    Path filePath = Paths.get(uploadDir, file.getFileName()); // 전체 경로 생성 후 파일 저장

                    file.setFilePath(filePath.toString());
                }
                // file 엔티티 저장
                fileRepository.saveAll(fileList);

                // 서버에 파일 저장
                fileService.saveFile(filesToAdd, fileList);
            }
        } catch (Exception e){

            throw new RuntimeException("파일 업로드에 오류가 발생했습니다." + e.getMessage());
        }
    }

}
