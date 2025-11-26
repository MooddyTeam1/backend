package com.moa.backend.domain.reward.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 한글 설명: 리워드 정보고시 카테고리별 상세 필드 DTO.
 * - 카테고리에 따라 다른 구조의 필드를 포함합니다.
 * - 해당 카테고리일 때만 의미가 있는 필드입니다.
 * - 모든 필드는 선택적(Optional)입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardCategorySpecificDisclosureRequestDTO {

    // ==================== 의류 (CLOTHING) ====================
    // 한글 설명: 제품 소재(섬유 조성/혼용률, %, 기능성 여부)
    private String material;
    // 한글 설명: 색상
    private String color;
    // 한글 설명: 치수
    private String size;
    // 한글 설명: 세탁방법
    private String washingMethod;
    // 한글 설명: 취급 시 주의사항
    private String careInstructions;

    // ==================== 구두/신발 (FOOTWEAR) ====================
    // 한글 설명: 제품 주 소재
    private String mainMaterial;
    // 한글 설명: 안감 (운동화)
    private String innerMaterial;
    // 한글 설명: 겉감 (운동화)
    private String outerMaterial;
    // 한글 설명: 발길이(mm)
    private String footLength;
    // 한글 설명: 굽 높이(cm)
    private String heelHeight;
    // 한글 설명: 해외 사이즈 표기 시 국내 사이즈 병행
    private String sizeComparison;

    // ==================== 가방 (BAG) ====================
    // 한글 설명: 종류 (백팩, 숄더백, 크로스백 등)
    private String type;
    // 한글 설명: 크기 및 중량 (가로, 세로, 끈 길이, 무게)
    private String dimensions;

    // ==================== 패션잡화 (FASHION_ACCESSORIES) ====================
    // 패션잡화는 type, material, dimensions, careInstructions를 공통으로 사용

    // ==================== 침구류/커튼 (BEDDING) ====================
    // 한글 설명: 제품 소재(섬유 조성/혼용률, 충전재 포함)
    // material 필드 재사용
    // 한글 설명: 제품 구성 (이불커버, 베개커버, 쿠션커버 수량 등)
    private String composition;

    // ==================== 가구 (FURNITURE) ====================
    // 한글 설명: 품명 (4인용 소파, 6인 식탁 등)
    private String productName;
    // 한글 설명: 주요 소재(등받이, 상판, 프레임, 바닥 등)
    // mainMaterial 필드 재사용

    // ==================== 주방용품 (KITCHENWARE) ====================
    // 한글 설명: 품명 및 모델명
    // productName 필드 재사용
    // 한글 설명: 재질
    // material 필드 재사용
    // 한글 설명: 동일 모델 출시년월
    // releaseDate는 common에 있지만 주방용품에서도 사용
    // 한글 설명: 식품위생법에 따른 수입기구·용기 신고 문구
    private String importDeclarationText;

    // ==================== 화장품 (COSMETICS) ====================
    // 한글 설명: 용량 또는 중량(ml, g)
    private String volume;
    // 한글 설명: 제품 주요 사양(피부 타입, 색상/호수 등)
    private String specifications;
    // 한글 설명: 사용기한 또는 개봉 후 사용 기간
    // expirationDate는 common에 있지만 화장품에서도 사용
    // 한글 설명: 개봉 후 사용기간 표기 시 제조연월일도 표기
    // manufacturingDate는 common에 있지만 화장품에서도 사용
    // 한글 설명: 사용방법
    private String usageMethod;
    // 한글 설명: 전성분 전체
    private String ingredients;
    // 한글 설명: 기능성 화장품 여부
    private Boolean functionalCosmetic;
    // 한글 설명: 식약처 심사 필유무
    private Boolean kfdaApproval;
    // 한글 설명: 사용 시 주의사항
    // precautions는 common에 있지만 화장품에서도 사용

    // ==================== 귀금속/보석/시계 (JEWELRY) ====================
    // 한글 설명: 소재/순도/밴드 재질(시계)
    // material 필드 재사용
    // 한글 설명: 중량
    private String weight;
    // 한글 설명: 제조국 및 원산지/가공지 정보
    private String originInfo;
    // 한글 설명: 치수(호수 + mm 등)
    // size 필드 재사용
    // 한글 설명: 착용 시 주의사항
    // careInstructions 필드 재사용
    // 한글 설명: 주요 사양/등급, 기능, 방수 등
    // specifications 필드 재사용
    // 한글 설명: 보증서 제공 여부
    private Boolean warrantyProvided;

    // ==================== 식품 (FOOD) ====================
    // 한글 설명: 포장 단위별 용량(중량)/수량/크기
    // packagingInfo로 사용 (아래에 정의)
    // 한글 설명: 생산자(수입품이면 수입자 포함)
    private String producer;
    // 한글 설명: 제조국/원산지
    // originInfo 필드 재사용
    // 한글 설명: 제조연월일(또는 포장일/생산연도)
    // manufacturingDate는 common에 있지만 식품에서도 사용
    // 한글 설명: 유통기한/품질유지기한
    // expirationDate는 common에 있지만 식품에서도 사용
    // 한글 설명: 관련 법상 표시 사항
    private String legalDisclosures;
    // 한글 설명: 상품 구성
    // composition 필드 재사용
    // 한글 설명: 보관방법/취급방법
    private String storageMethod;
    // 한글 설명: 포장 단위별 용량(중량)/수량/크기
    private String packagingInfo;

    // ==================== 건강기능식품 (HEALTH_FOOD) ====================
    // 한글 설명: 식품의 유형
    private String foodType;
    // 한글 설명: 제조업소 명칭 및 소재지
    private String manufacturerInfo;
    // 한글 설명: 제조연월일
    // manufacturingDate는 common에 있지만 건강기능식품에서도 사용
    // 한글 설명: 유통기한/품질유지기한
    // expirationDate는 common에 있지만 건강기능식품에서도 사용
    // 한글 설명: 포장단위별 용량(중량), 수량
    // packagingInfo 필드 재사용
    // 한글 설명: 원재료명 및 함량(원산지 포함)
    // ingredients 필드 재사용
    // 한글 설명: 영양정보
    private String nutritionInfo;
    // 한글 설명: 기능정보
    private String functionInfo;
    // 한글 설명: 섭취량, 섭취방법, 주의사항, 부작용 가능성
    private String intakeInfo;
    // 한글 설명: 유전자변형 건강기능식품 여부
    private Boolean gmoInfo;
    // 한글 설명: 표시·광고 사전심의필 유무 및 심의번호
    private String reviewInfo;
    // 한글 설명: 수입품: 건강기능식품에 관한 법률에 따른 수입신고를 필함
    // importDeclarationText 필드 재사용

    // ==================== 가공식품 (PROCESSED_FOOD) ====================
    // 한글 설명: 식품의 유형
    // foodType 필드 재사용
    // 한글 설명: 생산자 및 소재지(수입품이면 수입자, 제조국 포함)
    // producerInfo로 사용 (아래에 정의)
    // 한글 설명: 생산자 및 소재지(수입품이면 수입자, 제조국 포함)
    private String producerInfo;
    // 한글 설명: 제조연월일
    // manufacturingDate는 common에 있지만 가공식품에서도 사용
    // 한글 설명: 유통기한/품질유지기한
    // expirationDate는 common에 있지만 가공식품에서도 사용
    // 한글 설명: 포장단위별 용량(중량), 수량
    // packagingInfo 필드 재사용
    // 한글 설명: 원재료명 및 함량(원산지 포함)
    // ingredients 필드 재사용
    // 한글 설명: 영양성분
    // nutritionInfo 필드 재사용
    // 한글 설명: 유전자변형 식품 여부
    // gmoInfo 필드 재사용
    // 한글 설명: 영유아식/체중조절식 등 특수용도 식품의 광고심의 및 부작용 관련 표시
    private String specialFoodInfo;
    // 한글 설명: 수입품: 식품위생법에 따른 수입신고를 필함
    // importDeclarationText 필드 재사용

    // ==================== 영유아용품 (BABY_PRODUCTS) ====================
    // 한글 설명: 품명 및 모델명
    // productName 필드 재사용
    // 한글 설명: KC 인증종류
    private String kcCertificationType;
    // 한글 설명: KC 인증번호
    // kcCertificationNumber는 common에 있지만 영유아용품에서도 사용
    // 한글 설명: 크기, 중량
    // dimensions 필드 재사용
    // 한글 설명: 색상
    // color 필드 재사용
    // 한글 설명: 재질(섬유 혼용률 포함)
    // material 필드 재사용
    // 한글 설명: 사용 연령 또는 체중 범위
    private String ageRange;
    // 한글 설명: 동일 모델 출시년월
    // releaseDate는 common에 있지만 영유아용품에서도 사용
    // 한글 설명: 취급방법 및 주의사항, 안전표시
    // careInstructions 필드 재사용

    // ==================== 서적 (BOOK) ====================
    // 한글 설명: 도서명
    private String bookTitle;
    // 한글 설명: 저자
    private String author;
    // 한글 설명: 출판사
    private String publisher;
    // 한글 설명: 크기(가로 × 세로 × 두께/전자책은 파일 용량)
    // dimensions 필드 재사용
    // 한글 설명: 쪽수(전자책 제외 가능)
    private String pageCount;
    // 한글 설명: 제품 구성(낱권/세트/부록 CD 등)
    // composition 필드 재사용
    // 한글 설명: 출간일 또는 출간 예정일
    private String publishDate;
    // 한글 설명: 목차 또는 책 소개
    private String tableOfContents;
    // 한글 설명: 아동용 학습 교재일 경우 사용 연령
    // ageRange 필드 재사용

    // ==================== 디지털콘텐츠 (DIGITAL_CONTENT) ====================
    // 한글 설명: 제작자 또는 공급자
    // producer 필드 재사용
    // 한글 설명: 이용조건, 이용기간
    private String usageConditions;
    // 한글 설명: 상품 제공 방식(다운로드, 스트리밍, CD 등)
    private String deliveryMethod;
    // 한글 설명: 최소 시스템 사양, 필수 소프트웨어
    private String systemRequirements;
    // 한글 설명: 청약철회·계약 해제/해지에 따른 효과
    private String cancellationEffect;

    // ==================== 기타 (OTHER) ====================
    // 한글 설명: 기타 카테고리별 자유 필드 (Map 형태로 저장)
    private Map<String, Object> other;
}
