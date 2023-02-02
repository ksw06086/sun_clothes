package mvc.member.service;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.member.persistence.MemberDAO;
import mvc.member.persistence.MemberDAOImpl;
import mvc.member.vo.FAQVO;

public interface MemberService {
	
	// 중복확인 처리
	public void confirmId(HttpServletRequest req, HttpServletResponse res);
	
	// 회원가입 처리
	public void inputPro(HttpServletRequest req, HttpServletResponse res);
	
	// 로그인 처리
	public void loginPro(HttpServletRequest req, HttpServletResponse res);
	
	// 회원탈퇴 처리
	public void deletePro(HttpServletRequest req, HttpServletResponse res);
	
	// 회원정보 수정 상세 페이지
	public void modifyView(HttpServletRequest req, HttpServletResponse res);

	// 환불계좌 수정 처리
	public void refundPro(HttpServletRequest req, HttpServletResponse res);
	
	// 회원정보 수정 처리
	public void modifyPro(HttpServletRequest req, HttpServletResponse res);
	
	// 글목록
	public void boardList(HttpServletRequest req, HttpServletResponse res);
	
	// 글상세페이지
	public void contentForm(HttpServletRequest req, HttpServletResponse res);
	
	// 글 수정 상세 페이지
	public void bmodifyView(HttpServletRequest req, HttpServletResponse res);
	
	// 글 수정 처리 페이지
	public void bmodifyPro(HttpServletRequest req, HttpServletResponse res);
	
	// 글쓰기 페이지
	public void writeForm(HttpServletRequest req, HttpServletResponse res);
	
	// 글쓰기 처리 페이지
	public void writePro(HttpServletRequest req, HttpServletResponse res);
	
	// 공지 글쓰기 처리 페이지
	public void h_noticePro(HttpServletRequest req, HttpServletResponse res);
	
	// 공지 삭제 처리 페이지
	public void h_noticedeletePro(HttpServletRequest req, HttpServletResponse res);
	
	// 공지 View
	public void h_noticeupdateView(HttpServletRequest req, HttpServletResponse res);
	
	// 공지 수정 처리 페이지
	public void h_noticeupdatePro(HttpServletRequest req, HttpServletResponse res);
	
	// 글삭제 처리 페이지
	public void bdeletePro(HttpServletRequest req, HttpServletResponse res);
	
	// FAQ 처리 페이지들
	public void FAQList(HttpServletRequest req, HttpServletResponse res);

	public void h_FAQPro(HttpServletRequest req, HttpServletResponse res);
	
	public void h_FAQupdateView(HttpServletRequest req, HttpServletResponse res);

	public void h_FAQupdatePro(HttpServletRequest req, HttpServletResponse res);
	
	public void h_FAQdeletePro(HttpServletRequest req, HttpServletResponse res);
	
	// QnA 처리 페이지들
	public void QnAList(HttpServletRequest req, HttpServletResponse res);
	
	public void QnAPro(HttpServletRequest req, HttpServletResponse res);
	
	public void QnAupdateView(HttpServletRequest req, HttpServletResponse res);
	
	
	// review
	public void reviewupdateView(HttpServletRequest req, HttpServletResponse res);
	
	
	
	
	
	
	
	// big part
	public void bigpartPro(HttpServletRequest req, HttpServletResponse res);
	
	public void bigpartdelPro(HttpServletRequest req, HttpServletResponse res);

	// medium part
	public void mediumPro(HttpServletRequest req, HttpServletResponse res);
	
	public void mediumpartdelPro(HttpServletRequest req, HttpServletResponse res);

	// color part
	public void colorPro(HttpServletRequest req, HttpServletResponse res);
	
	public void colordelPro(HttpServletRequest req, HttpServletResponse res);

	// size part
	public void sizePro(HttpServletRequest req, HttpServletResponse res);
	
	public void sizedelPro(HttpServletRequest req, HttpServletResponse res);
	

}
