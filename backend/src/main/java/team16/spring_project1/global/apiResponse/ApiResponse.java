package team16.spring_project1.global.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApiResponse<Content> {
    private boolean success;
    private String message;
    private Content content;

    public static class ApiResponseBuilder<Content> {
        private boolean success;
        private String message;
        private Content content;

        public ApiResponseBuilder<Content> success(boolean success) {
            this.success = success;
            return this;
        }

        public ApiResponseBuilder<Content> message(String message) {
            this.message = message;
            return this;
        }

        public ApiResponseBuilder<Content> content(Content content) {
            this.content = content;
            return this;
        }

        public ApiResponse<Content> build() {

            if (content == null) {
                content = createEmptyContent();
            }

            return new ApiResponse<>(success, message, content);
        }
    }

    public static <Content> ApiResponseBuilder<Content> builder() {
        return new ApiResponseBuilder<>();
    }

    public static <Content> ApiResponse<Content> success(Content content) {
        return ApiResponse.<Content>builder()
                .success(true)
                .message("요청이 성공했습니다.")
                .content(getContent(content))
                .build();
    }

    public static <Content> ApiResponse<Content> success(String message) {
        return ApiResponse.<Content>builder()
                .success(true)
                .message(message)
                .content(createEmptyContent())
                .build();
    }

    public static <Content> ApiResponse<Content> success(String message, Content content) {
        return ApiResponse.<Content>builder()
                .success(true)
                .message(message)
                .content(getContent(content))
                .build();
    }

    // 실패 응답 (정적 팩토리 메서드)
    public static <Content> ApiResponse<Content> failure() {
        return ApiResponse.<Content>builder()
                .success(false)
                .message("요청에 실패하였습니다.")
                .content(createEmptyContent())
                .build();
    }

    public static <Content> ApiResponse<Content> failure(String message) {
        return ApiResponse.<Content>builder()
                .success(false)
                .message(message)
                .content(createEmptyContent())
                .build();
    }

    public static <Content> ApiResponse<Content> failure(String message, Content content) {
        return ApiResponse.<Content>builder()
                .success(false)
                .message(message)
                .content(getContent(content))
                .build();
    }

    private static <Content> Content getContent(Content content) {
        return content == null ? createEmptyContent() : content;
    }

    @SuppressWarnings("unchecked")
    private static <Content> Content createEmptyContent() {
        return (Content) Collections.emptyList();
    }
}
