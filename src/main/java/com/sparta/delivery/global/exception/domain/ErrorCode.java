package com.sparta.delivery.global.exception.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 사용 예시이므로 추가로 작성 해주시면 찡긋


    // 400
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청 데이터가 올바르지 않습니다. 입력 데이터를 확인해 주세요."),
    PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "새 비밀번호와 재확인 비밀번호가 일치하지 않습니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "해당 카테고리를 찾지 못했습니다." ),
    //image
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 이미지를 찾을 수 없습니다."),
    IMAGE_SAME_INDEX(HttpStatus.BAD_REQUEST, "같은 인덱스 번호로 변경할 수 없습니다."),
    IMAGE_MAX_COUNT(HttpStatus.BAD_REQUEST, "이미지는 10개를 초과할 수 없습니다."),
    NOT_SEQUENTIAL_INDEX(HttpStatus.BAD_REQUEST,"이미지 인덱스가 맞지 않습니다"),
    MISMATCHED_IMAGE_COUNT(HttpStatus.BAD_REQUEST,"받아온 이미지 개수와 인덱스 개수가 맞지 않습니다." ),
    NO_USE_CATEGORY(HttpStatus.BAD_REQUEST, "해당 카테고리는 지원하지 않는 컨텐츠입니다."),
    BLACKLIST_TOKEN(HttpStatus.BAD_REQUEST, "블랙리스트 토큰으로 시도한 접근입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "기존과 동일한 비밀번호입니다."),

    //tosspay
        //tosspay
    ZERO_AMOUNT_PAYMENT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "0원 결제는 허용되지 않습니다."),
    INVALID_MENU_STATUS(HttpStatus.BAD_REQUEST, "판매 중인 메뉴가 아닙니다."),
    DIFFERENT_RESTAURANT(HttpStatus.BAD_REQUEST, "장바구니에 다른 음식점 메뉴가 존재합니다."),
    INVALID_ORDER_ACCESS(HttpStatus.BAD_REQUEST, "사용자와 주문의 정보가 일치하지 않습니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "조리가 시작된 주문은 취소할 수 없습니다."),
    OWNER_ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "완료된 주문은 취소할 수 없습니다."),

    // 401
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요한 요청입니다. 로그인 해주세요."),
    // 비번 변경 시
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다. 다시 시도 해주세요."),
    ROLE_AUTHORIZATION_REQUIRED(HttpStatus.UNAUTHORIZED, "권한이 필요한 요청입니다."),
    NOT_OWNER(HttpStatus.UNAUTHORIZED,"해당 식당의 주인만이 이용 가능합니다." ),
    NOT_REVIEWER(HttpStatus.UNAUTHORIZED,"해당 리뷰의 작성자만이 이용 가능합니다." ),

    // JWT) 관련
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다."),

    //500
    //image
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드에 실패했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다."),


    // 403
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),


    NOT_ADMIN(HttpStatus.UNAUTHORIZED,"관리자만이 이용 가능합니다." ),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 주문이 존재하지 않습니다."),
    NOT_FIND_INQUIRY(HttpStatus.NOT_FOUND, "문의 정보가 존재하지 않습니다."),
    AI_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 ai사용 기록이 존재하지 않습니다."),
    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "삭제할 별점이 존재하지 않습니다"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰가 존재하지 않습니다."),
    PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "부모 댓글을 찾을 수 없습니다."),

    PARENT_REVIEW_MISMATCH(HttpStatus.BAD_REQUEST, "부모 댓글은 동일 리뷰에 속해야 합니다."),

    //409
    NICKNAME_ALREADY_EXISTS(HttpStatus.NOT_FOUND, "해당 닉네임은 이미 존재합니다."),
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 식당입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다"),
//    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 리뷰입니다." ),
    S3_FOLDER_NO_FILE(HttpStatus.NOT_FOUND, "S3 폴더에 파일이 존재하지 않습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 장바구니 메뉴입니다."),
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메뉴입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "리프레시 토큰이 존재하지 않습니다."),

    // 409 Conflict
//    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 닉네임은 이미 존재합니다."),
    BUSINESS_CODE_EXISTS(HttpStatus.CONFLICT, "존재하는 사업자 번호 입니다."),
    CATEGORY_NAME_EXISTS(HttpStatus.CONFLICT, "존재하는 카테고리 이름 입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "존재하는 이메일 입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
