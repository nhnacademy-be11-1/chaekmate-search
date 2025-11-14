package shop.chaekmate.search.common;

import shop.chaekmate.search.dto.BookInfoRequest;

public class EmbeddingTextBuilder {

    public static String toText(BookInfoRequest bookInfoRequest) {
        StringBuilder sb = new StringBuilder();
        if (bookInfoRequest.getTitle() != null)
            sb.append("제목: ").append(bookInfoRequest.getTitle()).append("\n");

        if (bookInfoRequest.getAuthor() != null)
            sb.append("저자: ").append(bookInfoRequest.getAuthor()).append("\n");

        if (bookInfoRequest.getPrice() != null)
            sb.append("가격: ").append(bookInfoRequest.getPrice()).append("원\n");

        if (bookInfoRequest.getDescription() != null)
            sb.append("설명: ").append(bookInfoRequest.getDescription()).append("\n");

        if (bookInfoRequest.getCategories() != null && !bookInfoRequest.getCategories().isEmpty())
            sb.append("카테고리: ").append(String.join(", ", bookInfoRequest.getCategories())).append("\n");

        if (bookInfoRequest.getTags() != null && !bookInfoRequest.getTags().isEmpty())
            sb.append("태그: ").append(String.join(", ", bookInfoRequest.getTags())).append("\n");

        if (bookInfoRequest.getReviewSummary() != null)
            sb.append("리뷰 요약: ").append(bookInfoRequest.getReviewSummary()).append("\n");

        if (bookInfoRequest.getRating() != null)
            sb.append("평점: ").append(bookInfoRequest.getRating()).append("\n");

        if (bookInfoRequest.getReviewCnt() != null)
            sb.append("리뷰 개수: ").append(bookInfoRequest.getReviewCnt()).append("개\n");
        return sb.toString().trim();
    }

}
