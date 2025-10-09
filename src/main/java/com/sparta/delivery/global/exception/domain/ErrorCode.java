package com.sparta.delivery.global.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 사용 예시이므로 추가로 작성 해주시면 찡긋


    // 400
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청 데이터가 올바르지 않습니다. 입력 데이터를 확인해 주세요."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "해당 카테고리를 찾지 못했습니다." ),
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 이미지를 찾을 수 없습니다."),
    IMAGE_SAME_INDEX(HttpStatus.BAD_REQUEST, "같은 인덱스 번호로 변경할 수 없습니다."),
    S3_FOLDER_NO_FILE(HttpStatus.NOT_FOUND, "S3 폴더에 파일이 존재하지 않습니다."),
    // 401
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요한 요청입니다. 로그인 해주세요."),

    //500
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드에 실패했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
