package mvc.member.persistence;

import java.sql.Date;
import java.util.List;

import mvc.member.vo.BoardVO;
import mvc.member.vo.BrandVO;
import mvc.member.vo.CategoryVO;
import mvc.member.vo.FAQVO;
import mvc.member.vo.MemberVO;
import mvc.member.vo.MyBoardVO;
import mvc.member.vo.PluspayVO;
import mvc.member.vo.QnAVO;
import mvc.member.vo.SalerankVO;
import mvc.member.vo.bigpartVO;
import mvc.member.vo.cartVO;
import mvc.member.vo.clothVO;
import mvc.member.vo.colorVO;
import mvc.member.vo.mediumpartVO;
import mvc.member.vo.noticeVO;
import mvc.member.vo.orderVO;
import mvc.member.vo.replyVO;
import mvc.member.vo.reviewVO;
import mvc.member.vo.sizeVO;
import mvc.member.vo.stockVO;
import mvc.member.vo.wishVO;

public interface MemberDAO<E> {
	// 중복확인 체크
	public int idCheck(String strId, int member);
	
	// 회원가입 처리
	public int insertMember(MemberVO vo, int member);
	
	// 로그인 처리, 회원정보탈퇴시 비밀번호 인증, 회원정보수정시 비밀번호 인증
	public int idPwdCheck(String strid, String strpwd, int member);
	
	// 삭제처리
	public int deleteMember(String strid);
	
	// 회원정보 수정 상세페이지
	public MemberVO getMemberInfo(String strid);
	
	// 회원정보 수정 처리
	public int updateMember(MemberVO vo);
	
	// 게시글 갯수 구하기
	public int getArticleCnt(int nnumber);
	
	// 게시글 목록 조회
	public List<E> getArticleList(int start, int end);
	
	// 게시글 상세 조회, 게시글 수정을 위한 상세페이지
	public E getArticle(int num, int choose);

	// 조회수 증가
	public void addReadCnt(int num, int choose);
	
	// 게시글 수정 - 비밀번호 인증
	public int numPwdCheck(int num, String pwd);

	// 글수정 처리
	public int updateBoard(E vo, int choose);

	// 글작성 처리
	public int insertBoard(E vo, int choose);

	// 글삭제 처리
	public int deleteBoard(int num);

	// 공지 뿌려줄 때 사용하는 메소드
	public List<noticeVO> getNoticeList(int start, int end, Date firstday, Date lastday, String searchText, int searchType);

	// 공지전용 글 삭제 처리
	public int deleteNoticeBoard(String[] checked);

	// 공지전용 검색 처리
	public int getSelectNoticeCnt(Date firstday, Date lastday, String searchText, int searchType);

	// FAQ전용 검색 처리
	public int getSelectFAQCnt(Date firstday, Date lastday, String searchText, int searchType,
			String[] state);

	// FAQ 목록 조회
	public List<FAQVO> getFAQList(int start, int end, Date firstday, Date lastday, String searchText,
			int searchType, String[] state);

	// FAQ 전용 글 삭제 처리
	public int deleteFAQBoard(String[] checked);

	// QnA 목록 조회
	public List<QnAVO> getQnAList(int start, int end, Date firstday, Date lastday, String searchText, int searchType,
			String[] state);

	// QnA전용 검색 처리
	public int getSelectQnACnt(Date firstday, Date lastday, String searchText, int searchType, String[] state, String strid);

	// QnA전용 삭제 처리
	public int deleteQnABoard(String[] checked);

	// review전용 검색 처리
	public int getSelectreviewCnt(Date firstday, Date lastday, String searchText, int searchType, String strid);

	// review 목록 조회
	public List<reviewVO> getreviewList(int start, int end, Date firstday, Date lastday, String searchText,
			int searchType);

	// reply 검색 처리
	public int getSelectreplyCnt(int num);

	// reply 목록 조회
	public List<replyVO> getreplyList(int start, int end, int num);

	// reply 삭제
	public int deletereplyBoard(String[] checked);

	// review 삭제
	public int deletereviewBoard(String[] checked);

	// brand 검색 갯수
	public int getSelectbrandCnt(String searchText, int searchType);

	// brand 갯수
	public int getbrandCnt();

	public List<BrandVO> getbrandList(int start, int end, String searchText, int searchType);

	public int getbrandMaxNum();

	// 브랜드 등록
	public int insertBrand(BrandVO vo);

	// 브랜드 삭제
	public int deletebrand(String[] checked);

	// 브랜드 1개 가져오기
	public BrandVO getbrand(int num);

	// 브랜드 수정 처리
	public int updateBrand(BrandVO vo);

	// 대분류 검색 갯수
	public int getSelectbigpartCnt();

	// 중분류 검색 갯수
	public int getSelectmediumpartCnt(String parameter);

	// product 목록 조회
	public List<clothVO> getproductList(int start, int end, String searchText, int searchType);

	// bigpart 목록 모두 조회
	public List<bigpartVO> getbigpartallList();

	// mediumpart 목록 모두 조회
	public List<mediumpartVO> getmediumallList(int num);

	// brand 목록 모두 조회
	public List<BrandVO> getbrandallList();

	// product 검색 개수
	public int getSelectproductCnt(String searchText, int searchType);

	// product 개수
	public int getproductCnt();

	// color 개수
	public int getcolorCnt();

	// size 개수
	public int getsizeCnt();

	// color 목록 조회
	public List<colorVO> getcolorList();

	// size 목록 조회
	public List<sizeVO> getsizeList();

	// bigpart 등록
	public int insertBigpart(String name);

	// 중분류 등록
	public int insertmediumpart(String name, int bcode);

	// 컬러 등록
	public int insertcolorpart(String name);

	// 사이즈 등록
	public int insertsizepart(String name);

	// 대분류 삭제
	public int deletebigpart(int num);

	// 중분류 삭제
	public int deletemediumpart(int num);

	// 컬러 삭제
	public int deletecolorpart(int num);

	// 사이즈 삭제
	public int deletesizepart(int num);

	// product 등록 처리
	public int insertproduct(clothVO vo);

	// product 수정 폼
	public clothVO getproduct(int num);

	// product 수정 처리
	public int updateproduct(clothVO vo);

	// product mainimage 수정
	public int updatemainfileproduct(clothVO vo);

	// product files 수정
	public int updatefilesproduct(clothVO vo);

	// product withitems 수정
	public int updatewithitemsproduct(clothVO vo);

	// product 삭제
	public int deleteproduct(String[] checked);

	// menuproduct 리스트 갯수 조회
	public int getSelectprdCnt(String name);

	// menuproduct 리스트 조회
	public List<clothVO> getprdList(int start, int end, String name);

	// 상품상세 컬러 개수
	public int getSelectcolorCnt(int num);

	// 상품상세 사이즈 개수
	public int getSelectsizeCnt(int num, int colorcode);

	// 상품상세 컬러 목록
	public List<colorVO> getSelectcolorList(int num);

	// 상품상세 사이즈 목록
	public List<sizeVO> getSelectsizeList(int num, int colorcode);

	// 사이즈 컬러 추가 처리
	public int insertcs(stockVO vo);

	// 재고 목록 불러오기
	public stockVO getcs(int num, int parseInt, int parseInt2);

	// cs 수정 처리
	public int updatecs(stockVO vo);

	// 주문 등록 처리
	public int insertorder(orderVO vo);

	// count 수정하기
	public int updatecs(orderVO vo);

	// count Check
	public int countChk(int prdnum, int colorcode, int sizecode, int count);

	// order 검색 갯수
	public int getSelectOrderCnt(String state, Date firstday, Date lastday, String string);

	// order 전체 갯수
	public int getorderCnt();

	// order 목록 구하기
	public List<orderVO> getorderList(int start, int end, String state, Date firstday, Date lastday, String memId);

	// cart 추가
	public int insertcart(cartVO vo);

	// state 변경
	public int updatestate(int parseInt, int i, int j);

	// cart 검색 갯수
	public int getSelectCartCnt(String string);

	// cart 갯수
	public int getcartCnt();

	// cart 리스트 목록
	public List<orderVO> getcartList(int start, int end, String attribute);

	// 이메일 보내기
	public void sendmail(String email, String key);

	// 주문시에 장바구니도 삭제
	public int deletecart(int parseInt);

	// 장바구니 모두 삭제 아이디 해당
	public int deleteallcart(String attribute);

	// 적립금 plus
	public int updategplus(int parseInt, int prdplus, String memId);

	// plus들 가져오기
	public List<orderVO> getplusList(int start, int end, String attribute);

	// 환불 plus들 가져오기
	public int getrefundplus(String attribute);

	// 주문 상태별 갯수 가져오기
	public int selectstateCnt(String string, String memId);

	// 관리자 전용 주문 상태 변경
	public int h_updatestate(int num, String statename, String id, int prdnum);
	
	// 방문횟수 증가
	public int visit(String strid);

	// 회원목록 리스트
	public List<MemberVO> getMemberlist(int start, int end);

	// 회원 갯수
	public int getmemberCnt();

	// 회원검색 갯수
	public int getSelectmemberCnt(String searchText, int searchType, Date firstday, Date lastday, String month, int pluspay);

	// 회원 관리자메모 수정
	public int updatehostmemoMember(String hostmemo, String strid);

	// 방문자 토탈
	public int[] getclicktotal(String year);

	// 신규멤버
	public int[] getnewmembertotal(String year);

	// 주문 자 토탈
	public int[] getordermembertotal(String year);

	// 주문 건수 토탈
	public int[] getorderCnttotal(String year);

	// 주문 금액 토탈
	public int[] getorderpricetotal(String year);

	// 주문 환불 토탈
	public int[] getorderrefundtotal(String year);

	// 주문 실금액 토탈
	public int[] getorderrealpricetotal(String year);

	// 카테고리별 순위
	public List<CategoryVO> getCategoryrank(String year);

	// 판매순위
	public List<SalerankVO> getSalerank(String year);

	// 적립금 순위
	public List<PluspayVO> getPluspay();

	// 방문 수 올라가기
	public void visitplus(String strid);

	// 달별 갯수
	public int getmonthorderCnttotal();

	// 달별 구매액
	public int getmonthorderrealpricetotal();

	// 오늘날 메인 리스트
	public int getdayorderrealpricetotal();

	public int getdayclicktotal();

	public int selectQnACnt();

	public int selectnewmember();

	public int getdayorderCnttotal();

	public int getdaycancelCnttotal();

	// 7일간 개수
	public int[] selectPayendCnt();

	public int[] selectDeliStartCnt();

	public int[] selectDeliingCnt();

	public int[] selectDeliEndCnt();

	public int[] selectCancelCnt();

	public int[] selectReviewCnt();

	public int[] getweekpricetotal();

	// 내 게시물 목록
	public List<MyBoardVO> myboard(int start, int end, String searchText, int searchType, String strid);

	// 게시물 검색 갯수
	public int getSelectMyBoardCnt(String searchText, int searchType, String strid);

	// 게시물 갯수
	public int getMyBoardCnt(String strid);

	// cart 추가
	public int insertwish(wishVO vo);

	// cart 검색 갯수
	public int getSelectWishCnt(String string);

	// cart 갯수
	public int getWishCnt();

	// cart 리스트 목록
	public List<orderVO> getwishList(int start, int end, String attribute);

	// 주문시에 장바구니도 삭제
	public int deletewish(int parseInt);

	// 장바구니 모두 삭제 아이디 해당
	public int deleteallwish(String attribute);

	// 검색 갯수
	public int getSelectSearchCnt(String searchText, int searchType);

	// 검색 목록 리스트
	public List<clothVO> getSearchList(int start, int end, String searchText, int searchType);

	// 아이디 찾기
	public List<MemberVO> findid(String name, String email, int type);

	// 비밀번호 찾기
	public MemberVO findpwd(String strid, String email, int type);

	// main lists
	public List<clothVO> productlist();	

	
	
	
}
