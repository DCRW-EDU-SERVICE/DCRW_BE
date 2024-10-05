package com.example.DCRW.service.board;

import com.example.DCRW.dto.board.*;
import com.example.DCRW.entity.*;
import com.example.DCRW.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final PostRepository postRepository;
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    // 전체
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public PostDetailDto detailPost(int postId) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));

            // 파일 정보를 FileDto 리스트로 변환
            List<FileDto> fileList = convertToFileDtoList(post.getFileList());

            // 게시글 상세 정보에 파일 리스트 포함
            PostDetailDto postDetailDto = buildPostDetailDto(post, fileList);

            return postDetailDto;

        } catch (Exception e) {
            throw new RuntimeException("게시글 상세 조회에 오류가 발생했습니다 " + e.getMessage());
        }
    }

    // 게시글 등록
    @Override
    @Transactional
    public PostDetailDto addPost(String username, List<MultipartFile> files, PostAddDto postAddDto) {
        try{
            // 포스트 엔티티 생성
            Post post = Post.builder()
                    .title(postAddDto.getTitle())
                    .content(postAddDto.getContent())
                    .users(usersRepository.findById(username)
                            .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다")))
                    .board(boardRepository.findById(postAddDto.getBoardId())
                            .orElseThrow(() -> new IllegalArgumentException("게시판이 존재하지 않습니다")))
                    .category(categoryRepository.findById(postAddDto.getCategory())
                            .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다")))
                    .postDate(LocalDateTime.now())
                    .build();

            Post savedPost  = postRepository.save(post);

            // 파일 업로드 및 저장
            List<FileDto> fileList = new ArrayList<>();
            if (files != null && !files.isEmpty()) {
                List<File> uploadedFiles = fileService.uploadFiles(savedPost.getPostId(), files, "posts");
                if (savedPost.getFileList() == null) {
                    savedPost.setFileList(new ArrayList<>()); // 초기화
                }
                savedPost.getFileList().addAll(uploadedFiles);

                // 파일 정보를 FileDto 리스트로 변환
                fileList = convertToFileDtoList(post.getFileList());
            }

            // 게시글 상세 정보에 파일 리스트 포함
            return buildPostDetailDto(post, fileList);

        } catch (Exception e) {
            throw new RuntimeException("게시글 업로드에 실패했습니다: " + e.getMessage());
        }

    }


    // 게시글 삭제
    @Override
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
    @Override
    @Transactional
    public PostDetailDto updatePost(int postId, PostUpdateDto postUpdateDto, List<MultipartFile> filesToAdd, String username) {
        try {
            // 게시글 조회
            Post post = postRepository.findByIdAndUsers(postId, username)
                    .orElseThrow(() -> new IllegalArgumentException("게시글, 사용자 잘못된 입력"));

            // 게시글 제목 및 내용 업데이트
            postUpdateDto.getTitle().ifPresent(post::setTitle);
            postUpdateDto.getContent().ifPresent(post::setContent);
            postRepository.save(post); // 변경된 엔티티 저장

            // 파일 삭제 요청 처리
            handleFileDeletions(postUpdateDto.getFileDelete());

            // 파일 추가 요청 처리
            if (filesToAdd != null && !filesToAdd.isEmpty()) {
                fileService.uploadFiles(postId, filesToAdd, "posts");
            }

            // 파일 정보를 FileDto 리스트로 변환
            List<FileDto> fileList = convertToFileDtoList(post.getFileList());

            // 게시글 상세 정보에 파일 리스트 포함
            return buildPostDetailDto(post, fileList);

        } catch (Exception e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private void handleFileDeletions(List<Integer> fileDeleteIds) throws IOException {
        if (fileDeleteIds != null) {
            List<File> deleteList = new ArrayList<>();
            for (int fileId : fileDeleteIds) {
                File file = fileRepository.findById(fileId)
                        .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없어요"));
                deleteList.add(file);
                fileRepository.delete(file); // 파일 DB에서 삭제
            }
            fileService.deleteFilesAfterCommit(deleteList); // S3에서 파일 삭제
        }
    }

    // file dto 리스트 셋팅 반환
    private List<FileDto> convertToFileDtoList(List<File> files) {
        return files.stream()
                .map(file -> FileDto.builder()
                        .fileId(file.getFileId())
                        .fileName(file.getFileName())
                        .filePath(file.getFilePath())
                        .fileUrl(file.getFileUrl())
                        .fileType(file.getFileType())
                        .build())
                .collect(Collectors.toList());
    }

    // post detail dto 셋팅 반환
    private PostDetailDto buildPostDetailDto(Post post, List<FileDto> fileList) {
        return PostDetailDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory().getCategoryId())
                .file(fileList)  // 파일 리스트
                .build();
    }


}
