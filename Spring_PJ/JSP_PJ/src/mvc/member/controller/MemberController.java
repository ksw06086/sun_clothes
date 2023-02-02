package mvc.member.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.member.service.MemberServiceImpl;

@WebServlet("*.do")
public class MemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 플젝명/*.do 이름에 슬레시(/)있으면 안된다.
	public MemberController() {
		super();
	}

	// 1단계. HTTP 요청 받음
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		action(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}

	public void action(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 한글 안깨지게 처리
		req.setCharacterEncoding("UTF-8");

		String viewPage = "";
		String uri = req.getRequestURI(); // /JSP_mvcMember/main.do
		String contextPath = req.getContextPath(); // /JSP_mvcMember
		String url = uri.substring(contextPath.length()); // uri.substring(beginIndex); => /main.do
		MemberServiceImpl service = new MemberServiceImpl();

		// 2단계. 요청분석
		// 첫 메인페이지
		if (url.equals("/main.do") || url.equals("/*.do")) {
			System.out.println("/main.do");
			if (req.getSession().getAttribute("memCnt") == null) {
				service.prdList(req, res);
				viewPage = "/suncloth/main.jsp";
			} else if ((Integer) req.getSession().getAttribute("memCnt") == 0) {
				service.prdList(req, res);
				viewPage = "/suncloth/main.jsp";
			} else if ((Integer) req.getSession().getAttribute("memCnt") == 1) {
				service.hostmain(req, res);
				viewPage = "/host/hostmain.jsp";
			}
		} else if (url.equals("/mymain.do")) {
			System.out.println("/inputForm.do");

			viewPage = "/suncloth/main.jsp";
		} else if (url.equals("/inputForm.do")) { // 회원가입 폼페이지
			System.out.println("/inputForm.do");

			viewPage = "/suncloth/join_us.jsp";

			// 중복확인 서브페이지(자바스크립트에서 호출, 아이디가 중복된 경우)
		} else if (url.equals("/h_inputForm.do")) {
			System.out.println("/h_inputForm.do");

			viewPage = "/suncloth/h_join_us.jsp";
		} else if (url.equals("/confirmId.do")) {
			System.out.println("/confirmId.do");
			service.confirmId(req, res);

			viewPage = "/suncloth/confirmId.jsp";
			// 이메일 전송
		} else if (url.equals("/emailsend.do")) {
			System.out.println("/emailsend.do");
			// 스프링에서는 어노테이션으로 처리한다.
			service.emailsend(req, res);

			viewPage = "/suncloth/emailsend.jsp";
			// 회원가입 처리
		} else if (url.equals("/inputPro.do")) {
			System.out.println("/inputPro.do");
			// 스프링에서는 어노테이션으로 처리한다.
			service.inputPro(req, res);

			viewPage = "/suncloth/inputPro.jsp";
		} else if(url.equals("/findid.do")) { // 아이디 찾기
			System.out.println("/findid.do"); 

			viewPage = "/suncloth/findid.jsp";
		} else if(url.equals("/findpwd.do")) { // 비번찾기
			System.out.println("/findpwd.do"); 

			viewPage = "/suncloth/findpwd.jsp";
		} else if(url.equals("/findidcomplete.do")) { // 아이디 찾기 완료
			System.out.println("/findidcomplete.do"); 
			service.findid(req, res);

			viewPage = "/suncloth/findcompleteid.jsp";
		} else if(url.equals("/findpwdcomplete.do")) { // 비번찾기 완료
			System.out.println("/findpwdcomplete.do"); 
			service.findpwd(req, res);

			viewPage = "/suncloth/findcompletepwd.jsp";
			// 회원가입 성공
		} else if (url.equals("/mainSuccess.do")) {
			System.out.println("/mainSuccess.do");

			req.setAttribute("id", req.getParameter("id"));
			req.setAttribute("name", req.getParameter("names"));
			req.setAttribute("email", req.getParameter("email"));
			req.setAttribute("member", req.getParameter("member"));
			viewPage = "/suncloth/join_complete.jsp";
			// 로그인
		} else if (url.equals("/loginPro.do")) {
			System.out.println("/loginPro.do");
			service.loginPro(req, res);

			viewPage = "/suncloth/loginPro.jsp";

		} else if (url.equals("/login.do")) {
			System.out.println("/login.do");
			if(req.getParameter("member") == null) {
				viewPage = "/suncloth/g_login.jsp";
			} else if (Integer.parseInt(req.getParameter("member")) == 0) {
				viewPage = "/suncloth/g_login.jsp";
			} else {
				viewPage = "/suncloth/h_login.jsp";
			}
		} else if (url.equals("/logout.do")) {
			// 로그아웃
			System.out.println("/logout.do");

			// 세션삭제
			req.getSession().removeAttribute("memId");
			req.getSession().removeAttribute("memCnt");
			viewPage = "/suncloth/logoutPro.jsp";
			// 회원정보 수정 폼
		} else if (url.equals("/modifyView.do")) {
			System.out.println("/modifyView.do");
			service.modifyView(req, res);

			viewPage = "/suncloth/modify_profile.jsp";
		} else if (url.equals("/modifyacc.do")) {
			System.out.println("/modifyacc.do");

			req.setAttribute("bank", req.getParameter("bank"));
			req.setAttribute("acc", req.getParameter("acc"));
			req.setAttribute("acchost", req.getParameter("acchost"));
			viewPage = "/suncloth/refundbankChange.jsp";
		} else if (url.equals("/refundPro.do")) {
			System.out.println("/refundPro.do");
			service.refundPro(req, res);

			viewPage = "/suncloth/refundPro.jsp";
		} else if (url.equals("/modifyPro.do")) {
			System.out.println("/modifyPro.do");
			service.modifyPro(req, res);

			viewPage = "/suncloth/modifyPro.jsp";
			// 게시글 처리
		} else if (url.equals("/notice.do")) {
			System.out.println("/notice.do");
			service.noticeList(req, res);

			viewPage = "/suncloth/notice.jsp";
		} else if (url.equals("/noticeForm.do")) {
			// 공지 글쓰기 보기
			System.out.println("/noticeForm.do");
			service.h_noticeupdateView(req, res);

			viewPage = "/suncloth/noticeView.jsp";
		} else if (url.equals("/FAQ.do")) {
			// FAQ
			System.out.println("/FAQ.do");
			service.FAQList(req, res);

			viewPage = "/suncloth/FAQ.jsp";
		} else if (url.equals("/FAQForm.do")) {
			// FAQ 글쓰기 보기
			System.out.println("/FAQForm.do");
			service.h_FAQupdateView(req, res);

			viewPage = "/suncloth/FAQView.jsp";
		} else if (url.equals("/QnA.do")) {
			// Q&A
			System.out.println("/QnA.do");
			service.QnAList(req, res);

			viewPage = "/suncloth/QnA.jsp";
		} else if (url.equals("/QnAwrite.do")) {
			// Q&A 글쓰기
			System.out.println("/QnAwrite.do");
			service.writeForm(req, res);

			viewPage = "/suncloth/QnAwrite.jsp";
		} else if(url.equals("/QnAPro.do")) { // Q&A 글쓰기 처리
			System.out.println("/QnAPro.do"); 
			service.QnAPro(req, res);
			  
			viewPage = "/suncloth/QnAPro.jsp"; 
		} else if(url.equals("/QnAForm.do")) { // Q&A 상세 페이지
			System.out.println("/QnAForm.do"); 
			if(req.getParameter("textType") != null && req.getParameter("textType").equals("close")) {
				req.setAttribute("num", req.getParameter("num"));
				req.setAttribute("number", req.getParameter("number"));
				req.setAttribute("pageNum", req.getParameter("pageNum"));
				req.setAttribute("choose", req.getParameter("choose"));
				viewPage = "/suncloth/QnAForm1.jsp";
			} else {
				service.QnAupdateView(req, res);
				if (req.getSession().getAttribute("memCnt") == null || (Integer)req.getSession().getAttribute("memCnt") == 0) {
					viewPage = "/suncloth/QnAForm2.jsp";
				} else if((Integer) req.getSession().getAttribute("memCnt") == 1) {
					viewPage = "/suncloth/h_QnAForm2.jsp";
				}
			}
		} else if(url.equals("/QnAupdate.do")) { // Q&A 수정 상세 페이지
			System.out.println("/QnAupdate.do"); 
			service.QnAupdateView(req, res);
			
			viewPage = "/suncloth/QnAupdate.jsp";
		} else if (url.equals("/QnAupdatePro.do")) {
			// QnA 글쓰기 수정
			System.out.println("/QnAupdatePro.do");
			service.QnAupdatePro(req, res);

			viewPage = "/suncloth/QnAupdatePro.jsp";
		} else if (url.equals("/QnAdeletePro.do")) {
			// QnA 글쓰기 삭제
			System.out.println("/QnAdeletePro.do");
			service.QnAdeletePro(req, res);

			viewPage = "/suncloth/QnAdeletePro.jsp";
			
			
			
			
		} else if (url.equals("/review.do")) {
			// review 리스트
			System.out.println("/review.do");
			service.reviewList(req, res);

			viewPage = "/suncloth/review.jsp";
		} else if (url.equals("/reviewwrite.do")) {
			// review 글쓰기 폼
			System.out.println("/reviewwrite.do");
			service.writeForm(req, res);

			viewPage = "/suncloth/reviewwrite.jsp";
		} else if(url.equals("/reviewPro.do")) { // review 글쓰기 처리
			System.out.println("/reviewPro.do"); 
			service.reviewPro(req, res);
			  
			viewPage = "/suncloth/reviewPro.jsp"; 
		} else if(url.equals("/reviewForm.do")) { // review 상세페이지
			System.out.println("/reviewForm.do"); 
			service.reviewupdateView(req, res);
			  
			viewPage = "/suncloth/reviewForm.jsp"; 
		} else if(url.equals("/reviewdelete.do")) { // review 삭제
			System.out.println("/reviewdelete.do"); 
			service.reviewdeletePro(req, res);
			  
			viewPage = "/suncloth/reviewdelete.jsp"; 
		} else if(url.equals("/replyPro.do")) { // review 답글 처리
			System.out.println("/replyPro.do"); 
			service.replyPro(req, res);
			  
			viewPage = "/suncloth/replyPro.jsp"; 
		} else if(url.equals("/replydelete.do")) { // review 답글 삭제
			System.out.println("/replydelete.do"); 
			service.replydeletePro(req, res);
			  
			viewPage = "/suncloth/replydelete.jsp"; 
		} else if (url.equals("/deletePro.do")) {
			// 회원탈퇴 처리
			System.out.println("/deletePro.do");
			service.deletePro(req, res);

			viewPage = "/suncloth/deletePro.jsp";

			// hostmain
		} else if (url.equals("/hostmain.do")) {
			System.out.println("/hostmain.do");
			service.hostmain(req, res);

			viewPage = "/host/hostmain.jsp";

			// 게시판
		} else if (url.equals("/boardView.do")) {
			System.out.println("/boardView.do");

			viewPage = "/host/h_board.jsp";

		} else if (url.equals("/h_notice.do")) {
			// 공지
			System.out.println("/h_notice.do");
			service.noticeList(req, res);

			viewPage = "/host/h_notice.jsp";
		} else if (url.equals("/h_noticewrite.do")) {
			// 공지 글쓰기
			System.out.println("/h_noticewrite.do");
			service.writeForm(req, res);

			viewPage = "/host/h_noticewrite.jsp";
		} else if (url.equals("/h_noticePro.do")) {
			// 공지 글쓰기 처리
			System.out.println("/h_noticePro.do");
			service.h_noticePro(req, res);

			viewPage = "/host/h_noticePro.jsp";
		} else if (url.equals("/h_noticedeletePro.do")) {
			// 공지 글쓰기 삭제
			System.out.println("/h_noticedeletePro.do");
			service.h_noticedeletePro(req, res);

			viewPage = "/host/h_noticedeletePro.jsp";
		} else if (url.equals("/h_noticeForm.do")) {
			// 공지 글쓰기 수정
			System.out.println("/h_noticeForm.do");
			service.h_noticeupdateView(req, res);

			viewPage = "/host/h_noticeupdate.jsp";
		} else if (url.equals("/h_noticeUpdatePro.do")) {
			// 공지 글쓰기 수정
			System.out.println("/h_noticeUpdatePro.do");
			service.h_noticeupdatePro(req, res);

			viewPage = "/host/h_noticeUpdatePro.jsp";
		} else if (url.equals("/h_noticeselect.do")) {
			// 공지 검색 처리
			System.out.println("/h_noticeselect.do");
			service.noticeList(req, res);

			viewPage = "/host/h_notice.jsp";

		} else if (url.equals("/h_FAQ.do")) {
			// 일반질문답
			System.out.println("/h_FAQ.do");
			service.FAQList(req, res);

			viewPage = "/host/h_FAQ.jsp";
		} else if (url.equals("/h_FAQwrite.do")) {
			// 일반질문답 글쓰기 폼
			System.out.println("/h_FAQwrite.do");
			service.writeForm(req, res);

			viewPage = "/host/h_FAQinputForm.jsp";
		} else if (url.equals("/h_FAQPro.do")) {
			// 일반질문답 글쓰기 처리
			System.out.println("/h_FAQPro.do");
			service.h_FAQPro(req, res);

			viewPage = "/host/h_FAQPro.jsp";
		} else if (url.equals("/h_FAQdeletePro.do")) {
			// 일반질문답 글쓰기 삭제
			System.out.println("/h_FAQdeletePro.do");
			service.h_FAQdeletePro(req, res);

			viewPage = "/host/h_FAQdeletePro.jsp";
		} else if (url.equals("/h_FAQForm.do")) {
			// 일반질문답 글쓰기 수정
			System.out.println("/h_FAQForm.do");
			service.h_FAQupdateView(req, res);

			viewPage = "/host/h_FAQupdate.jsp";
		} else if (url.equals("/h_FAQUpdatePro.do")) {
			// 일반질문답 글쓰기 수정
			System.out.println("/h_FAQUpdatePro.do");
			service.h_FAQupdatePro(req, res);

			viewPage = "/host/h_FAQUpdatePro.jsp";
		} else if (url.equals("/h_FAQselect.do")) {
			// 일반질문답 검색 처리
			System.out.println("/h_FAQselect.do");
			service.FAQList(req, res);

			viewPage = "/host/h_FAQ.jsp";

		} else if (url.equals("/h_QnA.do")) {
			// 질문과 답변
			System.out.println("/h_QnA.do");
			service.QnAList(req, res);

			viewPage = "/host/h_QnA.jsp";
		} else if (url.equals("/h_QnAwrite.do")) {
			// 질문과 답변 글쓰기 폼
			System.out.println("/h_QnAwrite.do");
			service.writeForm(req, res);

			viewPage = "/host/h_QnAinputForm.jsp";
		} else if (url.equals("/h_QnAPro.do")) {
			// 질문과 답변 글쓰기 처리
			System.out.println("/h_QnAPro.do");
			service.QnAPro(req, res);

			viewPage = "/host/h_QnAPro.jsp";
		} else if(url.equals("/h_QnAupdate.do")) { // Q&A 수정 상세 페이지
			System.out.println("/h_QnAupdate.do"); 
			service.QnAreplyupdateView(req, res);
			
			viewPage = "/host/h_QnAupdate.jsp";
		} else if (url.equals("/QnAupdatePro.do")) {
			// QnA 글쓰기 수정
			System.out.println("/QnAupdatePro.do");
			service.QnAupdatePro(req, res);

			viewPage = "/host/h_QnAupdatePro.jsp";
			
			
			
			
		} else if (url.equals("/h_review.do")) {
			// 후기
			System.out.println("/h_review.do");
			service.reviewList(req, res);

			viewPage = "/host/h_review.jsp";
		} else if (url.equals("/h_notwritechar.do")) {
			// 금칙어
			System.out.println("/h_notwritechar.do");
			service.boardList(req, res);

			viewPage = "/host/h_notwritechar.jsp";

		} else if (url.equals("/h_product.do")) {
			// 상품
			System.out.println("/h_product.do");
			service.productList(req, res);

			viewPage = "/host/h_product.jsp";
		} else if (url.equals("/h_productinput.do")) {
			// 상품 글쓰기 폼
			System.out.println("/h_productinput.do");
			service.productwriteForm(req, res);

			viewPage = "/host/h_productinput.jsp";
		} else if (url.equals("/h_csinput.do")) {
			// cs 글쓰기 폼
			System.out.println("/h_csinput.do");
			service.cswriteForm(req, res);

			viewPage = "/host/h_csinput.jsp";
		} else if (url.equals("/h_productPro.do")) {
			// 상품 등록 처리
			System.out.println("/h_productPro.do");
			service.productPro(req, res);

			viewPage = "/host/h_productPro.jsp";
		} else if (url.equals("/h_csPro.do")) {
			// cs 등록 처리
			System.out.println("/h_csPro.do");
			service.csPro(req, res);

			viewPage = "/host/h_csPro.jsp";
		} else if (url.equals("/h_productForm.do")) {
			// 상품 수정 폼
			System.out.println("/h_productForm.do");
			service.h_productupdateView(req, res);

			viewPage = "/host/h_productupdateView.jsp";
		} else if (url.equals("/h_csupdate.do")) {
			// cs 수정 폼
			System.out.println("/h_csupdate.do");
			service.h_csupdateView(req, res);

			viewPage = "/host/h_csupdate.jsp";
		} else if (url.equals("/h_productform2.do")) {
			// 상품 수정 폼
			System.out.println("/h_productform2.do");
			service.h_productupdateView2(req, res);

			viewPage = "/host/h_productupdateView2.jsp";
		} else if (url.equals("/h_productupdatePro1.do")) {
			// 상품 텍스트 수정
			System.out.println("/h_productupdatePro1.do");
			service.h_productupdatePro(req, res);

			viewPage = "/host/h_productupdatePro.jsp";
		} else if (url.equals("/h_csupdatePro.do")) {
			// cs 수정
			System.out.println("/h_csupdatePro.do");
			service.h_csupdatePro(req, res);

			viewPage = "/host/h_csupdatePro.jsp";
		} else if (url.equals("/h_productmainfileupdatePro.do")) {
			// 상품 메인파일 수정
			System.out.println("/h_productmainfileupdatePro.do");
			service.h_productmainfileupdatePro(req, res);

			viewPage = "/host/h_productmainfileupdatePro.jsp";
		} else if (url.equals("/h_productfilesupdatePro.do")) {
			// 상품 파일 수정
			System.out.println("/h_productfilesupdatePro.do");
			service.h_productfilesupdatePro(req, res);

			viewPage = "/host/h_productfilesupdatePro.jsp";
		} else if (url.equals("/h_productwithitemsupdatePro.do")) {
			// 상품 withitems 수정
			System.out.println("/h_productwithitemsupdatePro.do");
			service.h_productwithitemsupdatePro(req, res);

			viewPage = "/host/h_productwithitemsupdatePro.jsp";
		} else if (url.equals("/h_productdeletePro.do")) {
			// 브랜드 삭제
			System.out.println("/h_productdeletePro.do");
			service.h_productdeletePro(req, res);

			viewPage = "/host/h_productdeletePro.jsp";
		} else if (url.equals("/withproductitems.do")) {
			// withitems
			System.out.println("/withproductitems.do");
			service.withproductList(req, res);

			viewPage = "/host/withproductitems.jsp";
		} else if (url.equals("/h_bigpartPro.do")) {
			// 대분류 추가 처리
			System.out.println("/h_bigpartPro.do");
			service.bigpartPro(req, res);

			viewPage = "/host/h_bigpartPro.jsp";
		} else if (url.equals("/h_mediumpartPro.do")) {
			// 중분류 추가 처리
			System.out.println("/h_mediumpartPro.do");
			service.mediumPro(req, res);

			viewPage = "/host/h_mediumpartPro.jsp";
		} else if (url.equals("/h_colorPro.do")) {
			// 컬러 추가 처리
			System.out.println("/h_colorPro.do");
			service.colorPro(req, res);

			viewPage = "/host/h_colorPro.jsp";
		} else if (url.equals("/h_sizePro.do")) {
			// 사이즈 추가 처리
			System.out.println("/h_sizePro.do");
			service.sizePro(req, res);

			viewPage = "/host/h_sizePro.jsp";
		} else if (url.equals("/h_bigpartdelPro.do")) {
			// 대분류 삭제 처리
			System.out.println("/h_bigpartdelPro.do");
			service.bigpartdelPro(req, res);

			viewPage = "/host/h_bigpartdelPro.jsp";
		} else if (url.equals("/h_mediumpartdelPro.do")) {
			// 중분류 삭제 처리
			System.out.println("/h_mediumpartdelPro.do");
			service.mediumpartdelPro(req, res);

			viewPage = "/host/h_mediumpartdelPro.jsp";
		} else if (url.equals("/h_colordelPro.do")) {
			// 컬러 삭제 처리
			System.out.println("/h_colordelPro.do");
			service.colordelPro(req, res);

			viewPage = "/host/h_colordelPro.jsp";
		} else if (url.equals("/h_sizedelPro.do")) {
			// 사이즈 삭제 처리
			System.out.println("/h_sizedelPro.do");
			service.sizedelPro(req, res);

			viewPage = "/host/h_sizedelPro.jsp";
		} else if (url.equals("/h_brand.do")) {
			// 브랜드
			System.out.println("/h_brand.do");
			service.brandList(req, res);
			

			viewPage = "/host/h_brand.jsp";
		} else if (url.equals("/h_brandinput.do")) {
			// 브랜드 등록
			System.out.println("/h_brandinput.do");
			service.getMaxNum(req, res);
			
			viewPage = "/host/h_brandinput.jsp";
		} else if (url.equals("/h_brandPro.do")) {
			// 브랜드 등록 처리
			System.out.println("/h_brandPro.do");
			service.brandPro(req, res);

			viewPage = "/host/h_brandPro.jsp";
		} else if (url.equals("/h_branddeletePro.do")) {
			// 브랜드 삭제
			System.out.println("/h_branddeletePro.do");
			service.h_branddeletePro(req, res);

			viewPage = "/host/h_branddeletePro.jsp";
		} else if (url.equals("/h_brandForm.do")) {
			// 공지 글쓰기 수정
			System.out.println("/h_brandForm.do");
			service.h_brandupdateView(req, res);

			viewPage = "/host/h_brandupdate.jsp";
		} else if (url.equals("/h_brandUpdatePro.do")) {
			// 공지 글쓰기 수정
			System.out.println("/h_brandUpdatePro.do");
			service.h_brandupdatePro(req, res);

			viewPage = "/host/h_brandUpdatePro.jsp";
		} else if (url.equals("/h_brandselect.do")) {
			// 공지 검색 처리
			System.out.println("/h_brandselect.do");
			service.noticeList(req, res);

			viewPage = "/host/h_brandselect.jsp";

		} else if (url.equals("/h_order.do")) {
			// 주문
			System.out.println("/h_order.do");
			service.h_orderlist(req, res);
			
			viewPage = "/host/h_order.jsp";
		} else if (url.equals("/h_member.do")) {
			// 회원
			System.out.println("/h_member.do");
			service.memberlist(req, res);
			
			viewPage = "/host/h_member.jsp";
		} else if (url.equals("/h_memberForm.do")) {
			// 회원 수정 폼
			System.out.println("/h_memberForm.do");
			service.memberForm(req, res);

			viewPage = "/host/h_memberForm.jsp";
		} else if (url.equals("/h_memberPro.do")) {
			// 회원 수정 처리
			System.out.println("/h_memberPro.do");
			service.h_memberPro(req, res);

			viewPage = "/host/h_memberPro.jsp";
		} else if (url.equals("/h_deletePro.do")) {
			// 회원탈퇴 처리
			System.out.println("/h_deletePro.do");
			service.h_deletePro(req, res);

			viewPage = "/host/h_deletePro.jsp";

			// hostmain
		} else if (url.equals("/h_clicktotal.do")) {
			// 통계
			System.out.println("/h_clicktotal.do");
			service.clicktotal(req, res);
			
			viewPage = "/host/h_clicktotal.jsp";
		} else if (url.equals("/h_newmembertotal.do")) {
			// 새 멤버 목록
			System.out.println("/h_newmembertotal.do");
			service.newmembertotal(req, res);

			viewPage = "/host/h_newmembertotal.jsp";
		} else if (url.equals("/h_memberpluspay.do")) {
			// 적립금 목록
			System.out.println("/h_memberpluspay.do");
			service.pluspay(req, res);

			viewPage = "/host/h_memberpluspay.jsp";
		} else if (url.equals("/h_category.do")) {
			// 분야 목록
			System.out.println("/h_category.do");
			service.category(req, res);

			viewPage = "/host/h_category.jsp";
		} else if (url.equals("/h_salerank.do")) {
			// 순위 목록
			System.out.println("/h_salerank.do");
			service.salerank(req, res);

			viewPage = "/host/h_salerank.jsp";
		} else if (url.equals("/h_ordertotal.do")) {
			// 주문통합 목록
			System.out.println("/h_ordertotal.do");
			service.ordertotal(req, res);

			viewPage = "/host/h_ordertotal.jsp";
		} else if (url.equals("/h_saletotal.do")) {
			// 판매통합 목록
			System.out.println("/h_saletotal.do");
			service.saletotal(req, res);

			viewPage = "/host/h_saletotal.jsp";
		} else if (url.equals("/contentForm.do")) {
			System.out.println("/contentForm.do");
			service.contentForm(req, res);

			viewPage = "/board/contentForm.jsp";
		} else if (url.equals("/modifyForm.do")) {
			System.out.println("/modifyForm.do");
			req.setAttribute("num", req.getParameter("num"));
			req.setAttribute("pageNum", req.getParameter("pageNum"));

			viewPage = "/board/modifyForm.jsp";
		} else if (url.equals("/modifyView.bo")) {
			System.out.println("/modifyView.bo");
			service.modifyView(req, res);

			viewPage = "/board/modifyView.jsp";
			// 게시글 수정 처리 페이지
		} else if (url.equals("/modifyPro.bo")) {
			System.out.println("/modifyPro.bo");
			service.modifyPro(req, res);

			viewPage = "/board/modifyPro.jsp";
			// 글쓰기
		} else if (url.equals("/writeForm.bo")) {
			System.out.println("/writeForm.bo");
			service.writeForm(req, res);

			viewPage = "/board/writeForm.jsp";
		} else if (url.equals("/writePro.bo")) {
			System.out.println("/writePro.bo");
			service.writePro(req, res);

			viewPage = "/board/writePro.jsp";
		} else if (url.equals("/deleteForm.bo")) {
			System.out.println("/deleteForm.bo");
			req.setAttribute("num", req.getParameter("num"));
			req.setAttribute("pageNum", req.getParameter("pageNum"));

			viewPage = "/board/deleteForm.jsp";
		} else if (url.equals("/deletePro.bo")) {
			System.out.println("/deletePro.bo");
			service.deletePro(req, res);

			viewPage = "/board/deletePro.jsp";
		} else if (url.equals("/menulist.do")) {
			// only_asclo
			System.out.println("/menulist.do");
			service.menuList(req, res);
			
			viewPage = "/suncloth/menulist.jsp";
		} else if (url.equals("/productclick.do")) {
			// product_click
			System.out.println("/productclick.do");
			service.productclick(req, res);
			
			viewPage = "/suncloth/product_click.jsp";
		} else if (url.equals("/orderform.do")) {
			// order 폼
			System.out.println("/orderform.do");
			service.orderform(req, res);
			
			viewPage = "/suncloth/order_form.jsp";
		} else if (url.equals("/orderPro.do")) {
			// 주문 처리
			System.out.println("/orderPro.do");
			service.orderPro(req, res);

			viewPage = "/suncloth/orderPro.jsp";
		} else if (url.equals("/order.do")) {
			// 주문 리스트 목록
			System.out.println("/order.do");
			service.orderlist(req, res);
			
			viewPage = "/suncloth/order.jsp";
		} else if (url.equals("/findroad.do")) {
			// find 지도
			System.out.println("/findroad.do");
			
			viewPage = "/suncloth/findroad.jsp";
		} else if (url.equals("/cartAdd.do")) {
			// 장바구니 추가
			System.out.println("/cartAdd.do");
			service.cartPro(req, res);

			viewPage = "/suncloth/cartPro.jsp";
		} else if (url.equals("/cart.do")) {
			// 장바구니
			System.out.println("/cart.do");
			service.cartlist(req, res);

			viewPage = "/suncloth/cart.jsp";
		} else if (url.equals("/cartdel.do")) {
			// 장바구니 삭제
			System.out.println("/cartdel.do");
			service.cartdeletePro(req, res);

			viewPage = "/suncloth/cartdelPro.jsp";
		} else if (url.equals("/cartalldel.do")) {
			// 장바구니 삭제
			System.out.println("/cartalldel.do");
			service.cartalldeletePro(req, res);

			viewPage = "/suncloth/cartdelPro.jsp";
		} else if (url.equals("/wishlistAdd.do")) {
			// 찜하기 추가
			System.out.println("/wishlistAdd.do");
			service.wishlistPro(req, res);

			viewPage = "/suncloth/wishlistPro.jsp";
		} else if (url.equals("/wishlist.do")) {
			// 찜하기 리스트
			System.out.println("/wishlist.do");
			service.wishlist(req, res);

			viewPage = "/suncloth/wishlist.jsp";
		} else if (url.equals("/wishlistdel.do")) {
			// 찜하기 삭제
			System.out.println("/wishlistdel.do");
			service.wishlistdeletePro(req, res);

			viewPage = "/suncloth/wishlistdelPro.jsp";
		} else if (url.equals("/wishlistalldel.do")) {
			// 찜하기 전체 삭제
			System.out.println("/wishlistalldel.do");
			service.wishlistalldeletePro(req, res);

			viewPage = "/suncloth/wishlistdelPro.jsp";
		} else if (url.equals("/mypage.do")) {
			// 마이페이지
			System.out.println("/mypage.do");
			service.mypagelist(req, res);

			viewPage = "/suncloth/mypage.jsp";
		} else if (url.equals("/mileage.do")) {
			// 마일리지 페이지
			System.out.println("/mileage.do");
			service.mileagelist(req, res);

			viewPage = "/suncloth/mileage.jsp";
		} else if (url.equals("/my_board.do")) {
			// 내 게시물 페이지
			System.out.println("/my_board.do");
			service.myboard(req, res);

			viewPage = "/suncloth/myboard.jsp";
		} else if (url.equals("/searchlist.do")) {
			// 상품
			System.out.println("/searchlist.do");
			service.searchList(req, res);

			viewPage = "/host/searchlist.jsp";
		}
			

		/*
		 * RequestDispatcher : 서블릿 또는 JSP 요청을 받은 후, 다른 컴포넌트로 요청을 위임하는 클래스이다.
		 * RequestDispatcher를 이용해서 해당 뷰페이지로 포워딩(=이동) RequestDispatcher : "위임", "파견"
		 */
		RequestDispatcher dispatcher = req.getRequestDispatcher(viewPage);
		dispatcher.forward(req, res);
	}
}
