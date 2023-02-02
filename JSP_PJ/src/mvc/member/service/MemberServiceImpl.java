package mvc.member.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import mvc.member.persistence.MemberDAO;
import mvc.member.persistence.MemberDAOImpl;
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
import oracle.net.aso.p;

public class MemberServiceImpl implements MemberService{

	@Override
	public void confirmId(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 입력받은 값을 받아온다.
		String strId = req.getParameter("id");
		int member = Integer.parseInt(req.getParameter("member"));
		
		// 4단계. 다형성적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5단계. 중복된 아이디가 있는지 확인
		int cnt = dao.idCheck(strId, member);
		
		// jsp에 값을 넘길때에는 set Attribute
		// 6단계. requset나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("selectCnt", cnt);
		req.setAttribute("id", strId);
		req.setAttribute("member", member);
	}

	// email 메세지 보내기
	public void emailsend(HttpServletRequest req, HttpServletResponse res) {
		
		
		StringBuffer temp = new StringBuffer();
		Random rnd = new Random();
		for(int i = 0; i < 6; i++) {
			int rIndex = rnd.nextInt(2);
			switch (rIndex) {
			case 0:
				// A-Z
				temp.append((char) ((int) (rnd.nextInt(26)) + 65));
				break;
			case 1:
				// 0~9
				temp.append((rnd.nextInt(10)));
			}
		}
		String key = temp.toString();
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		String email = req.getParameter("idname") + "@" +
				req.getParameter("urlcode");
		System.out.println(email);
		dao.sendmail(email, key);
		
		req.setAttribute("key", key);
	}
	
	// 회원가입 처리
	@Override
	public void inputPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int member = Integer.parseInt(req.getParameter("memberNum"));
		
		// vo 바구니를 생성한다.
		MemberVO vo = new MemberVO();
		vo.setId(req.getParameter("id"));
		vo.setPwd(req.getParameter("pwd"));
		vo.setName(req.getParameter("name"));
		vo.setHp(req.getParameter("telphone1") + "-" +
				req.getParameter("telphone2") + "-" +
				req.getParameter("telphone3"));
		vo.setEmail(req.getParameter("idName") + "@" +
				req.getParameter("urlcode"));
		if(member == 0) {
			vo.setAddress(req.getParameter("address"));
			vo.setAddress1(req.getParameter("address1"));
			vo.setAddress2(req.getParameter("address2"));
			
			
			/*
			 * String hp = ""; String hp1 = req.getParameter("hp1"); String hp2 =
			 * req.getParameter("hp2"); String hp3 = req.getParameter("hp3");
			 * if(!hp1.equals("") && !hp2.equals("") && !hp2.equals("")) { hp = hp1 + "-" +
			 * hp2 + "-" + hp3; } vo.setHp(hp);
			 */
			if(!req.getParameter("tel1").equals("") &&
					!req.getParameter("tel2").equals("") &&
					!req.getParameter("tel3").equals("")) {
			vo.setHomephone(req.getParameter("tel1") + "-" +
					req.getParameter("tel2") + "-" +
					req.getParameter("tel3"));
			}
			
			/*
			 * String email = req.getParameter("email1") + "@" + req.getParameter("email2");
			 * vo.setEmail(email);
			 */
			
			String birth = req.getParameter("birth");
			vo.setBirth(java.sql.Date.valueOf(birth));
			System.out.println(vo.getBirth());
			
			vo.setBirthtype(req.getParameter("birthtype"));
			
			vo.setAcchost(req.getParameter("acchost"));
			
			vo.setBank(req.getParameter("bank"));
			
			vo.setAcc(req.getParameter("acc"));
			
			// reg_date
			vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		int cnt = dao.insertMember(vo, member);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("insertCnt", cnt);
		req.setAttribute("id", vo.getId());
		req.setAttribute("name", vo.getName());
		req.setAttribute("email", vo.getEmail());
		req.setAttribute("member", member);
	}

	// 로그인 처리
	@Override
	public void loginPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String strid = req.getParameter("id");
		String strpwd = req.getParameter("pwd");
		int member = Integer.parseInt(req.getParameter("member"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idPwdCheck(strid, strpwd, member);
		if(selectCnt == 1) {
			// memId 대소문자 구분
			// session에 id를 설정
			if(member == 0) {
				dao.visit(strid);
				dao.visitplus(strid);
			}
			req.getSession().setAttribute("memId", strid);
			req.getSession().setAttribute("memCnt", member);
		}
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("cnt", selectCnt);
		req.setAttribute("member", member);
		
	}

	// 회원탈퇴
	@Override
	public void deletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String strid = (String) req.getSession().getAttribute("memId");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idCheck(strid,0);
		int deleteCnt = 0;
		
		// 5-2단계. 있으면 로그인한 id로 삭제
		if(selectCnt == 1) {
			deleteCnt = dao.deleteMember(strid);
			req.getSession().removeAttribute("memId");
			req.getSession().removeAttribute("memCnt");
		}
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("dcnt", deleteCnt);
	}

	// 회원정보 수정 상세 페이지
	@Override
	public void modifyView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String strid = (String)req.getSession().getAttribute("memId");

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idCheck(strid,0);
		
		// 5-2단계. 있으면 로그인한 id로 정보 조회
		if(selectCnt == 1) {
			MemberVO vo = dao.getMemberInfo(strid);
			req.setAttribute("vo", vo);
		}
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("scnt", selectCnt);
		
	}

	@Override
	public void modifyPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로부터 입력받은 값을 받아오기
		// vo 바구니를 생성한다.
		MemberVO vo = new MemberVO();
		vo.setId((String) req.getSession().getAttribute("memId"));
		vo.setPwd(req.getParameter("pwd"));
		vo.setName(req.getParameter("name"));
		vo.setAddress(req.getParameter("address"));
		vo.setAddress1(req.getParameter("address1"));
		vo.setAddress2(req.getParameter("address2"));
		
		
		/*
		 * String hp = ""; String hp1 = req.getParameter("hp1"); String hp2 =
		 * req.getParameter("hp2"); String hp3 = req.getParameter("hp3");
		 * if(!hp1.equals("") && !hp2.equals("") && !hp2.equals("")) { hp = hp1 + "-" +
		 * hp2 + "-" + hp3; } vo.setHp(hp);
		 */
		if(!req.getParameter("tel1").equals("") &&
				!req.getParameter("tel2").equals("") &&
				!req.getParameter("tel3").equals("")) {
		vo.setHomephone(req.getParameter("tel1") + "-" +
				req.getParameter("tel2") + "-" +
				req.getParameter("tel3"));
		}
		
		vo.setHp(req.getParameter("telphone1") + "-" +
				req.getParameter("telphone2") + "-" +
				req.getParameter("telphone3"));
		/*
		 * String email = req.getParameter("email1") + "@" + req.getParameter("email2");
		 * vo.setEmail(email);
		 */
		
		vo.setEmail(req.getParameter("idName") + "@" +
				req.getParameter("urlcode"));
		
		vo.setBirth(java.sql.Date.valueOf(req.getParameter("birth")));
		System.out.println(vo.getBirth());
		
		vo.setBirthtype(req.getParameter("birthtype"));
		
		vo.setAcchost(req.getParameter("acchost"));
		
		vo.setBank(req.getParameter("bank"));
		
		vo.setAcc(req.getParameter("acc"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		int cnt = dao.updateMember(vo);
		// 5단계. 회원정보 수정
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("updateCnt", cnt);
	}

	@Override
	public void refundPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String strid = (String)req.getSession().getAttribute("memId");
		String strpwd = req.getParameter("pwd");

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idPwdCheck(strid, strpwd, 0);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("bank", req.getParameter("bank"));
		req.setAttribute("acc", req.getParameter("acc"));
		req.setAttribute("acchost", req.getParameter("acchost"));
	}
	
	// 게시글 리스트
	@Override
	public void boardList (HttpServletRequest req, HttpServletResponse res) {
		
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (cnt / pageSize) + (cnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = cnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(cnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<BoardVO> list = dao.getArticleList(start, end);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 글 상세페이지
	@Override
	public void contentForm(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<BoardVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 조회수 증가
		dao.addReadCnt(num , 0);
		
		// 5-2단계. 상세페이지 조회
		BoardVO vo = dao.getArticle(num, 0);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("vo", vo);
	}

	@Override
	public void bmodifyView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("num"));
		String pwd = req.getParameter("pwd");
		String pageNum = req.getParameter("pageNum");

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<BoardVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int selectCnt = dao.numPwdCheck(num, pwd);
		
		// 5-2단계. 맞으면 게시글 조회
		if(selectCnt == 1) {
			BoardVO vo = dao.getArticle(num, 0);
			req.setAttribute("vo", vo);
		}
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("num", num);
		req.setAttribute("pageNum", pageNum);
	}

	// 글 수정 처리페이지
	@Override
	public void bmodifyPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		BoardVO vo = new BoardVO();
		vo.setNum(num);
		vo.setSubject(req.getParameter("subject"));
		vo.setContent(req.getParameter("content"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글 수정 처리
		int updateCnt = dao.updateBoard(vo, 0);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("cnt", updateCnt);
		req.setAttribute("num", num);
		req.setAttribute("pageNum", pageNum);
	}

	// 글쓰기 페이지
	@Override
	public void writeForm(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		// 제목글(답변글이 아닌 경우)
		int num = 0;
		int ref = 0;
		int ref_step = 0; // 글의 순서
		int ref_level = 0; // 글의 들여쓰기
		int choose = Integer.parseInt(req.getParameter("choose"));
		int pageNum = 0;
		
		// 답변글에 대한 글작성시
		if(req.getParameter("num") != null) {
			if(choose == 3) {
				MemberDAO<QnAVO> dao = MemberDAOImpl.getInstance();
				QnAVO vo = dao.getArticle(Integer.parseInt(req.getParameter("num")), choose);
				req.setAttribute("vo", vo);
			}
		}
		if(choose == 3) {
			pageNum = Integer.parseInt(req.getParameter("pageNum"));
			req.setAttribute("pageNum", pageNum);
		}
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("num", num);
		req.setAttribute("ref", ref);
		req.setAttribute("ref_step", ref_step);
		req.setAttribute("ref_level", ref_level);
		req.setAttribute("choose", choose);
	}

	@Override
	public void writePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		BoardVO vo = new BoardVO();
		vo.setNum(Integer.parseInt(req.getParameter("num")));
		vo.setRef(Integer.parseInt(req.getParameter("ref")));
		vo.setRef_step(Integer.parseInt(req.getParameter("ref_step"))); // 글의 순서
		vo.setRef_level(Integer.parseInt(req.getParameter("ref_level"))); // 글의 들여쓰기
		vo.setWriter(req.getParameter("writer"));
		vo.setSubject(req.getParameter("subject"));
		vo.setContent(req.getParameter("content"));
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		
		// url에 localhost대신 본인 IP를 입력하면 그 ip를 읽어서 바구니에 담는다.
		// http://192.168.219.118:80/JSP_mvcBoard/boardList.bo
		vo.setIp(req.getRemoteAddr());
		/*
		 * System.out.println(req.getRemoteAddr());
		 * System.out.println(req.getRemoteHost());
		 * 
		 * try { System.out.println(InetAddress.getLocalHost().getHostAddress()); }
		 * catch (UnknownHostException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글쓰기 처리
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
	}

	@Override
	public void bdeletePro (HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("num"));
		String pwd = req.getParameter("pwd");
		String pageNum = req.getParameter("pageNum");
		int choose = Integer.parseInt(req.getParameter("choose"));

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteBoard(num);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
	}
	
	// 공지 글쓰기
	@Override
	public void h_noticePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		noticeVO vo = new noticeVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		
		
		
		
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());
			FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
			FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
			int data = 0;
			// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
			while((data = fis.read()) != -1) {
			fos.write(data);
			}
			fis.close();
			fos.close();
			/*
			* 위에서 MultipartRequest() 객체를 선언해서 받는 모든 request 객체들은
			* MultipartRequest 타입으로 참조돼야함
			* (예 : request.getParameter 에서 mr.getParameter)
			*/
			vo.setNum(Integer.parseInt(mr.getParameter("num")));
			vo.setWriter((String)req.getSession().getAttribute("memId"));
			vo.setSubject(mr.getParameter("subject"));
			vo.setContent(mr.getParameter("content"));
			vo.setFile1(mr.getFilesystemName("file1"));
			int choose = Integer.parseInt(mr.getParameter("choose"));
			System.out.println("choose   " + mr.getParameter("choose"));
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<noticeVO> dao = MemberDAOImpl.getInstance();
			
			// 5단계. 글쓰기 처리
			int insertCnt = dao.insertBoard(vo, choose);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("icnt", insertCnt);
			req.setAttribute("choose", choose);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	public void noticeList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int choose = Integer.parseInt(req.getParameter("choose"));
		Date firstday = null;
		Date lastday = null;
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<noticeVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectNoticeCnt(firstday, lastday, searchText, searchType);
		cnt = dao.getArticleCnt(choose);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<noticeVO> list = dao.getNoticeList(start, end, firstday, lastday, searchText, searchType);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("dayNum", req.getParameter("dayNum")); // 날짜 선택된 것
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
		
	}

	@Override
	public void h_noticedeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String pageNum = req.getParameter("pageNum");
		String[] checked = null;
		if(req.getParameterValues("noticechecks") != null) {
			checked = req.getParameterValues("noticechecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteNoticeBoard(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", req.getParameter("choose"));
	}

	@Override
	public void h_noticeupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // 공지 = 1
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<noticeVO> dao = MemberDAOImpl.getInstance();
		dao.addReadCnt(num, choose);
		
		// 5-2단계. 상세페이지 조회
		noticeVO vo = dao.getArticle(num, choose);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		req.setAttribute("choose", choose); // 종류 번호
	}

	@Override
	public void h_noticeupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		int choose = Integer.parseInt(req.getParameter("choose"));
		noticeVO vo = new noticeVO();
		vo.setNum(num);
		vo.setContent(req.getParameter("content"));
		
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<noticeVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글 수정 처리
		int updateCnt = dao.updateBoard(vo, choose);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("cnt", updateCnt);
		req.setAttribute("num", num);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", choose); // 종류 번호
	}

	
	// FAQ 리스트
	@Override
	public void FAQList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int choose = Integer.parseInt(req.getParameter("choose"));
		Date firstday = null;
		Date lastday = null;
		int searchType = 0;
		String searchText = "";
		String[] state = null;
		
		// 검색하기 위한 조건들
		if(req.getParameterValues("state") != null) {
			state = req.getParameterValues("state");
		}
		
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<FAQVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectFAQCnt(firstday, lastday, searchText, searchType, state);
		cnt = dao.getArticleCnt(choose);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<FAQVO> list = dao.getFAQList(start, end, firstday, lastday, searchText, searchType, state);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("dayNum", req.getParameter("dayNum")); // 날짜 선택된 것
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	@Override
	public void h_FAQPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		FAQVO vo = new FAQVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		vo.setWriter((String)req.getSession().getAttribute("memId"));
		vo.setState(req.getParameter("state"));
		vo.setSubject(req.getParameter("subject"));
		vo.setContent(req.getParameter("content"));
		int choose = Integer.parseInt(req.getParameter("choose"));
		System.out.println("choose   " + req.getParameter("choose"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<FAQVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글쓰기 처리
		int insertCnt = dao.insertBoard(vo, choose);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("choose", choose);
	}
	
	@Override

	public void h_FAQupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // 공지 = 1
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<FAQVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 상세페이지 조회
		FAQVO vo = dao.getArticle(num, choose);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		req.setAttribute("choose", choose); // 종류 번호
	}

	@Override
	public void h_FAQupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		int choose = Integer.parseInt(req.getParameter("choose"));
		FAQVO vo = new FAQVO();
		vo.setNum(num);
		vo.setState(req.getParameter("state"));
		vo.setContent(req.getParameter("content"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<FAQVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글 수정 처리
		int updateCnt = dao.updateBoard(vo, choose);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("cnt", updateCnt);
		req.setAttribute("num", num);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", choose); // 종류 번호
	}

	
	@Override
	public void h_FAQdeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String pageNum = req.getParameter("pageNum");
		String[] checked = null;
		if(req.getParameterValues("FAQchecks") != null) {
			checked = req.getParameterValues("FAQchecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteFAQBoard(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", req.getParameter("choose"));
	}

	
	// Q&A 리스트
	@Override
	public void QnAList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int choose = Integer.parseInt(req.getParameter("choose"));
		Date firstday = null;
		Date lastday = null;
		int searchType = 0;
		String searchText = "";
		String[] state = null;
		
		// 검색하기 위한 조건들
		if(req.getParameterValues("Qtype") != null) {
			state = req.getParameterValues("Qtype");
		}
		
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<QnAVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectQnACnt(firstday, lastday, searchText, searchType, state, null);
		cnt = dao.getArticleCnt(choose);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<QnAVO> list = dao.getQnAList(start, end, firstday, lastday, searchText, searchType, state);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("dayNum", req.getParameter("dayNum")); // 날짜 선택된 것
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
		
	}

	
	// Q&A 작성 처리
	@Override
	public void QnAPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		QnAVO vo = new QnAVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());
			
			if(mr.getFilesystemName("file1") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile1(mr.getFilesystemName("file1"));
			}
			/*
			* 위에서 MultipartRequest() 객체를 선언해서 받는 모든 request 객체들은
			* MultipartRequest 타입으로 참조돼야함
			* (예 : request.getParameter 에서 mr.getParameter)
			*/
			vo.setNum(Integer.parseInt(mr.getParameter("num")));
			vo.setWriter((String)req.getSession().getAttribute("memId"));
			vo.setState(mr.getParameter("Qtype"));
			vo.setPwd(mr.getParameter("pwd"));
			vo.setSubject(mr.getParameter("subject"));
			vo.setContent(mr.getParameter("content"));
			vo.setTextType(mr.getParameter("textType"));
			vo.setRef(Integer.parseInt(mr.getParameter("ref")));
			vo.setRef_step(Integer.parseInt(mr.getParameter("ref_step"))); // 글의 순서
			vo.setRef_level(Integer.parseInt(mr.getParameter("ref_level"))); // 글의 들여쓰기
			// url에 localhost대신 본인 IP를 입력하면 그 ip를 읽어서 바구니에 담는다.
			// http://192.168.219.118:80/JSP_mvcBoard/boardList.bo
			vo.setIp(req.getRemoteAddr());
			int choose = Integer.parseInt(mr.getParameter("choose"));
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<QnAVO> dao = MemberDAOImpl.getInstance();
			
			// 5단계. 글쓰기 처리
			int insertCnt = dao.insertBoard(vo, choose);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("icnt", insertCnt);
			req.setAttribute("choose", choose);
			req.setAttribute("pageNum", mr.getParameter("pageNum"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// QnA 상세 페이지
	@Override
	public void QnAupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // QnA = 3
		int pwdCnt = 1;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<QnAVO> dao = MemberDAOImpl.getInstance();
		if(req.getSession().getAttribute("memCnt") != "1" && 
				req.getParameter("textType") == null && req.getParameter("pwd") != null) {
			pwdCnt = dao.numPwdCheck(num, req.getParameter("pwd"));
		}
		
		// 5-2단계. 상세페이지 조회
		QnAVO vo = dao.getArticle(num, choose);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("cnt", pwdCnt);
	}

	// QnA 수정 처리
	public void QnAupdatePro(HttpServletRequest req, HttpServletResponse res) {
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());
			FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
			FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
			int data = 0;
			// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
			while((data = fis.read()) != -1) {
			fos.write(data);
			}
			fis.close();
			fos.close();
			/*
			* 위에서 MultipartRequest() 객체를 선언해서 받는 모든 request 객체들은
			* MultipartRequest 타입으로 참조돼야함
			* (예 : request.getParameter 에서 mr.getParameter)
			*/
			// 3단계. 입력받은 값을 받아오기
			int num = Integer.parseInt(mr.getParameter("num"));
			String pageNum = mr.getParameter("pageNum");
			int choose = Integer.parseInt(mr.getParameter("choose"));
			QnAVO vo = new QnAVO();
			vo.setNum(num);
			vo.setContent(mr.getParameter("content"));
			vo.setFile1(mr.getFilesystemName("file1"));
			vo.setTextType(mr.getParameter("textType"));
			vo.setPwd(mr.getParameter("pwd"));
			

			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<QnAVO> dao = MemberDAOImpl.getInstance();

			// 5단계. 글 수정 처리
			int updateCnt = dao.updateBoard(vo, choose);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("cnt", updateCnt);
			req.setAttribute("num", num);
			req.setAttribute("pageNum", pageNum);
			req.setAttribute("choose", choose); // 종류 번호
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// QnA 답글 수정 폼
	public void QnAreplyupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // QnA = 3
		int ref = Integer.parseInt(req.getParameter("ref"));
		int pwdCnt = 1;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<QnAVO> dao = MemberDAOImpl.getInstance();
		if((Integer)req.getSession().getAttribute("memCnt") != 1 && 
				req.getParameter("textType") == null) {
			pwdCnt = dao.numPwdCheck(num, req.getParameter("pwd"));
		}
		QnAVO basevo = dao.getArticle(ref, choose);
		
		// 5-2단계. 상세페이지 조회
		QnAVO vo = dao.getArticle(num, choose);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("basevo", basevo);
		req.setAttribute("vo", vo);
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("cnt", pwdCnt);
	}

	// QnA 삭제
	public void QnAdeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String pageNum = req.getParameter("pageNum");
		String[] checked = null;
		if(req.getParameterValues("QnAchecks") != null) {
			checked = req.getParameterValues("QnAchecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteQnABoard(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", req.getParameter("choose"));
	}


	// review 리스트
	public void reviewList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int choose = Integer.parseInt(req.getParameter("choose"));
		Date firstday = null;
		Date lastday = null;
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<reviewVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectreviewCnt(firstday, lastday, searchText, searchType, null);
		cnt = dao.getArticleCnt(choose);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<reviewVO> list = dao.getreviewList(start, end, firstday, lastday, searchText, searchType);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("dayNum", req.getParameter("dayNum")); // 날짜 선택된 것
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// review 글쓰기 처리
	public void reviewPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		reviewVO vo = new reviewVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		
		
		
		
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 30 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());
			if(mr.getFilesystemName("file1") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile1(mr.getFilesystemName("file1"));
			}
			/*
			* 위에서 MultipartRequest() 객체를 선언해서 받는 모든 request 객체들은
			* MultipartRequest 타입으로 참조돼야함
			* (예 : request.getParameter 에서 mr.getParameter)
			*/
			vo.setNum(Integer.parseInt(mr.getParameter("num")));
			vo.setWriter((String)req.getSession().getAttribute("memId"));
			vo.setSubject(mr.getParameter("subject"));
			vo.setContent(mr.getParameter("content"));
			vo.setIp(req.getRemoteAddr());
			int choose = Integer.parseInt(mr.getParameter("choose"));
			System.out.println("choose   " + mr.getParameter("choose"));
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<reviewVO> dao = MemberDAOImpl.getInstance();
			
			// 5단계. 글쓰기 처리
			int insertCnt = dao.insertBoard(vo, choose);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("icnt", insertCnt);
			req.setAttribute("choose", choose);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	// review 상세 페이지
	@Override
	// review 글 상세 페이지
	public void reviewupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // review = 4
		int pwdCnt = 1;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<reviewVO> dao = MemberDAOImpl.getInstance();
		dao.addReadCnt(num, choose);
		
		// 5-2단계. 상세페이지 조회
		reviewVO vo = dao.getArticle(num, choose);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		req.setAttribute("choose", choose); // 종류 번호
		req.setAttribute("cnt", pwdCnt);
		
		
		
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 5;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int r_number = 0;	   // 출력용 글번호
		String rpageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectreplyCnt(num);
		
		// 5-2단계. 페이지 갯수 구하기
		rpageNum = req.getParameter("pageNum");
		
		if(rpageNum == null) {
			rpageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(rpageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		System.out.println("srhcnt : " + srhCnt);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > srhCnt) end = srhCnt;
		
		// 출력용 글번호
		r_number = srhCnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + r_number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<replyVO> list = dao.getreplyList(start, end, num);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("r_number", r_number); // 출력용 글번호
		req.setAttribute("rpageNum", rpageNum); // 페이지번호
		
		if(srhCnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}


	// 답글 처리
	public void replyPro(HttpServletRequest req, HttpServletResponse res) {
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // review = 4
		String strpwd = req.getParameter("pwd");
		String strid = (String) req.getSession().getAttribute("memId");
		// 3단계. 입력받은 값을 받아와 바구니 담음
		replyVO vo = new replyVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		vo.setWriter((String)req.getSession().getAttribute("memId"));
		vo.setRef(Integer.parseInt(req.getParameter("num")));
		vo.setIp(req.getRemoteAddr());
		vo.setContent(req.getParameter("content"));
		System.out.println("choose   " + req.getParameter("choose"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<replyVO> dao = MemberDAOImpl.getInstance();
		int pwdCnt = 0;
		if(req.getSession().getAttribute("memId") != null) {
			pwdCnt = dao.idPwdCheck(strid, strpwd, (Integer)req.getSession().getAttribute("memCnt"));
		}
		int insertCnt = 0;
		
		// 5단계. 글쓰기 처리
		if(pwdCnt == 1) {
			insertCnt = dao.insertBoard(vo, 5);
		}
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("num", num);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("pcnt", pwdCnt);
		req.setAttribute("choose", choose);
	}


	// 답글 삭제
	public void replydeletePro(HttpServletRequest req, HttpServletResponse res) {
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		int choose = Integer.parseInt(req.getParameter("choose")); // review = 4
		String rpageNum = req.getParameter("rpageNum");
		// 3단계. 입력받은 값을 받아오기
		String[] checked = null;
		if(req.getParameterValues("replychecks") != null) {
			checked = req.getParameterValues("replychecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deletereplyBoard(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("rpageNum", rpageNum);
		req.setAttribute("number", number);
		req.setAttribute("num", num);
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", choose);
	}

	// review 글 삭제
	public void reviewdeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String pageNum = req.getParameter("pageNum");
		String[] checked = null;
		if(req.getParameterValues("reviewchecks") != null) {
			checked = req.getParameterValues("reviewchecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deletereviewBoard(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("choose", req.getParameter("choose"));
	}


	// brand 목록 조회
	public void brandList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<BrandVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectbrandCnt(searchText, searchType);
		cnt = dao.getbrandCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<BrandVO> list = dao.getbrandList(start, end, searchText, searchType);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
		
	}

	
	// brand 등록 처리
	public void brandPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		// vo 바구니를 생성한다.
		BrandVO vo = new BrandVO();
		vo.setName(req.getParameter("name"));
		if(!req.getParameter("telphone1").equals("") &&
				!req.getParameter("telphone2").equals("") &&
				!req.getParameter("telphone3").equals("")) {
		vo.setHp(req.getParameter("telphone1") + "-" +
				req.getParameter("telphone2") + "-" +
				req.getParameter("telphone3"));
		}
		// reg_date
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		int cnt = dao.insertBrand(vo);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
	}

	
	public void getMaxNum(HttpServletRequest req, HttpServletResponse res) {
		MemberDAO<BrandVO> dao = MemberDAOImpl.getInstance();
		
		int Num = dao.getbrandMaxNum();
		
		req.setAttribute("num", Num);
	}


	// brand 삭제
	public void h_branddeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String pageNum = req.getParameter("pageNum");
		String[] checked = null;
		if(req.getParameterValues("brandchecks") != null) {
			checked = req.getParameterValues("brandchecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deletebrand(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
	}

	// brand 수정 폼
	public void h_brandupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<BrandVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 상세페이지 조회
		BrandVO vo = dao.getbrand(num);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
	}


	// brand 수정 처리
	public void h_brandupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNum = req.getParameter("pageNum");
		BrandVO vo = new BrandVO();
		vo.setNum(num);
		vo.setName(req.getParameter("name"));
		if(!req.getParameter("telphone1").equals("") &&
				!req.getParameter("telphone2").equals("") &&
				!req.getParameter("telphone3").equals("")) {
		vo.setHp(req.getParameter("telphone1") + "-" +
				req.getParameter("telphone2") + "-" +
				req.getParameter("telphone3"));
		}
		
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<noticeVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글 수정 처리
		int updateCnt = dao.updateBrand(vo);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("cnt", updateCnt);
		req.setAttribute("num", num);
		req.setAttribute("pageNum", pageNum);
	}


	// product 목록 조회
	public void productList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectproductCnt(searchText, searchType);
		bigsrhCnt = dao.getSelectbigpartCnt();
		if(req.getParameter("onecategory") != null && req.getParameter("onecategory") != "") {
			mediumsrhCnt = dao.getSelectmediumpartCnt(req.getParameter("onecategory"));
		}
		brandsrhCnt = dao.getbrandCnt();
		cnt = dao.getproductCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<clothVO> list = dao.getproductList(start, end, searchText, searchType);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		if(bigsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<bigpartVO> biglist = dao.getbigpartallList();
			req.setAttribute("biglist", biglist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(mediumsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<mediumpartVO> medilist = dao.getmediumallList(Integer.parseInt(req.getParameter("onecategory")));
			req.setAttribute("medilist", medilist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(brandsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<BrandVO> brandlist = dao.getbrandallList();
			req.setAttribute("brandlist", brandlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
		
	}


	// product 글쓰기 폼
	public void productwriteForm(HttpServletRequest req, HttpServletResponse res) {
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int topmediumsrhCnt = 0;	   // 검색한 글 갯수
		int bottommediumsrhCnt = 0;
		String onecategory = null;
		String opart = null;
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());
			
			if(mr.getFilesystemName("file1") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				req.setAttribute("file11", mr.getFilesystemName("file1"));
			}
			if(mr.getFilesystemName("file2") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file2"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file2"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				req.setAttribute("file21", mr.getFilesystemName("file2"));
			}
			if(mr.getFilesystemName("file3") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file3"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file3"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				req.setAttribute("file31", mr.getFilesystemName("file3"));
			}
			if(mr.getFilesystemName("file4") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file4"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file4"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				req.setAttribute("file41", mr.getFilesystemName("file4"));
			}
			if(mr.getFilesystemName("file5") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file5"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file5"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				req.setAttribute("file51", mr.getFilesystemName("file5"));
			}
			if(mr.getFilesystemName("mainfile") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("mainfile"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("mainfile"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				req.setAttribute("mainfile1", mr.getFilesystemName("mainfile"));
			}
		
		
		
			if(mr.getParameter("subbigpart") != null && !mr.getParameter("subbigpart").equals("")) {
				System.out.println("towpart");
				onecategory = mr.getParameter("subbigpart");
				opart = mr.getParameter("opart");
			} else if(mr.getParameter("opart") != null && !mr.getParameter("opart").equals("")) {
				System.out.println("onepart " + mr.getParameter("opart"));
				opart = mr.getParameter("opart");
			}
			
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
			
			// 5-1단계. 글 갯수 구하기
			bigsrhCnt = dao.getSelectbigpartCnt();
			if(mr.getParameter("subbigpart") != null && !mr.getParameter("subbigpart").equals("")) {
				System.out.println("onepart " + mr.getParameter("subbigpart"));
				topmediumsrhCnt = dao.getSelectmediumpartCnt(mr.getParameter("subbigpart"));
				if(mr.getParameter("opart") != null && !mr.getParameter("opart").equals("")) {
					bottommediumsrhCnt = dao.getSelectmediumpartCnt(mr.getParameter("opart"));
				}
			} else if(mr.getParameter("opart") != null && !mr.getParameter("opart").equals("")) {
				System.out.println("towpart");
				bottommediumsrhCnt = dao.getSelectmediumpartCnt(mr.getParameter("opart"));
			}
			brandsrhCnt = dao.getbrandCnt();
			
			if(bigsrhCnt > 0) {
				// 5-2단계. 게시글 목록 조회
				List<bigpartVO> biglist = dao.getbigpartallList();
				req.setAttribute("biglist", biglist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			}
			
			if(topmediumsrhCnt > 0) {
				// 5-2단계. 게시글 목록 조회
				List<mediumpartVO> topmedilist = dao.getmediumallList(Integer.parseInt(onecategory));
				req.setAttribute("topmedilist", topmedilist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			}
			
			if(bottommediumsrhCnt > 0) {
				// 5-2단계. 게시글 목록 조회
				List<mediumpartVO> bottommedilist = dao.getmediumallList(Integer.parseInt(opart));
				req.setAttribute("bottommedilist", bottommedilist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			}
			
			if(brandsrhCnt > 0) {
				// 5-2단계. 게시글 목록 조회
				List<BrandVO> brandlist = dao.getbrandallList();
				req.setAttribute("brandlist", brandlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			}
			
	
			// 기본 값들
			if(mr.getParameter("subbigpart") != null && !mr.getParameter("subbigpart").equals("")) {
				req.setAttribute("opart1", opart);
				req.setAttribute("opart2", onecategory);
			} else if(mr.getParameter("opart") != null && !mr.getParameter("opart").equals("")) {
				req.setAttribute("opart1", opart);
			}
			req.setAttribute("tpart1", mr.getParameter("tpart"));
			req.setAttribute("name1", mr.getParameter("name"));
			req.setAttribute("tax1", mr.getParameter("tax"));
			req.setAttribute("brands1", mr.getParameter("brands"));
			req.setAttribute("icon1", mr.getParameter("icon"));
			req.setAttribute("plus1", mr.getParameter("plus"));
			req.setAttribute("pluspay1", mr.getParameter("pluspay"));
			req.setAttribute("saleprice1", mr.getParameter("saleprice"));
			req.setAttribute("buyprice1", mr.getParameter("buyprice"));
			req.setAttribute("delidate1", mr.getParameter("delidate"));
			req.setAttribute("delipay1", mr.getParameter("delipay"));
			req.setAttribute("content1", mr.getParameter("content"));
			
			req.setAttribute("bottommediumsrhCnt", bottommediumsrhCnt);
			req.setAttribute("topmediumsrhCnt", topmediumsrhCnt);
			req.setAttribute("bigsrhCnt", bigsrhCnt);
			req.setAttribute("brandsrhCnt", brandsrhCnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// bigpart 추가 처리
	public void bigpartPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String name = req.getParameter("name");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<bigpartVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.insertBigpart(name);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
	}

	// mediumpart 추가 처리
	public void mediumPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String name = req.getParameter("name");
		int bcode = Integer.parseInt(req.getParameter("bcode"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<mediumpartVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.insertmediumpart(name, bcode);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
	}

	// color 추가 처리
	public void colorPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String name = req.getParameter("name");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<colorVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.insertcolorpart(name);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
		req.setAttribute("num", req.getParameter("num"));
	}

	// size 추가 처리
	public void sizePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String name = req.getParameter("name");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<sizeVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.insertsizepart(name);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
		req.setAttribute("num", req.getParameter("num"));
	}

	
	// 대분류 삭제처리
	public void bigpartdelPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("name"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<colorVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.deletebigpart(num);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
	}
	
	// 중분류 삭제처리
	public void mediumpartdelPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("name"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<mediumpartVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.deletemediumpart(num);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
	}

	// 컬러 삭제처리
	public void colordelPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("name"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<colorVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.deletecolorpart(num);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
		req.setAttribute("num", req.getParameter("num"));
	}

	// 사이즈 삭제처리
	public void sizedelPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		int num = Integer.parseInt(req.getParameter("name"));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<colorVO> dao = MemberDAOImpl.getInstance();
		int cnt = dao.deletesizepart(num);
		
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", cnt);
		req.setAttribute("num", req.getParameter("num"));
	}

	// 상품 등록 처리
	public void productPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		clothVO vo = new clothVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());
			
			if(mr.getFilesystemName("file1") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile1(mr.getFilesystemName("file1"));
			}
			if(mr.getFilesystemName("file2") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file2"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file2"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile2(mr.getFilesystemName("file2"));
			}
			if(mr.getFilesystemName("file3") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file3"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file3"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile3(mr.getFilesystemName("file3"));
			}
			if(mr.getFilesystemName("file4") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file4"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file4"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile4(mr.getFilesystemName("file4"));
			}
			if(mr.getFilesystemName("file5") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file5"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file5"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile5(mr.getFilesystemName("file5"));
			}
			if(mr.getFilesystemName("mainfile") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("mainfile"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("mainfile"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setMainfile(mr.getFilesystemName("mainfile"));
			}
			/*
			* 위에서 MultipartRequest() 객체를 선언해서 받는 모든 request 객체들은
			* MultipartRequest 타입으로 참조돼야함
			* (예 : request.getParameter 에서 mr.getParameter)
			*/
			if(mr.getParameter("tpart") == null || mr.getParameter("tpart").equals("")) {
				vo.setMediumcode(0);
			} else {
				vo.setMediumcode(Integer.parseInt(mr.getParameter("tpart")));
			}
			vo.setName(mr.getParameter("name"));
			vo.setTex(mr.getParameter("tex"));
			vo.setBrandnum(Integer.parseInt(mr.getParameter("brands")));
			vo.setIcon(mr.getParameter("icon"));
			if(mr.getParameter("plus").equals("plus")) {
				vo.setPlus(Integer.parseInt(mr.getParameter("pluspay")));
			} else {
				vo.setPlus(0);
			}
			vo.setSaleprice(Integer.parseInt(mr.getParameter("saleprice")));
			vo.setBuyprice(Integer.parseInt(mr.getParameter("buyprice")));
			vo.setDeliday(Integer.parseInt(mr.getParameter("delidate")));
			if(mr.getParameter("delipay").equals("pluspay")) {
				vo.setDeliprice(Integer.parseInt(mr.getParameter("deliprice")));
			} else if(mr.getParameter("delipay").equals("basepay")) {
				vo.setDeliprice(2500);
			} else {
				vo.setDeliprice(0);
			}
			vo.setContent(mr.getParameter("content"));
			if(mr.getParameter("withitem") != "") {
				vo.setWithprdnum(Integer.parseInt(mr.getParameter("withitem")));
			}
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
			
			// 5단계. 글쓰기 처리
			int insertCnt = dao.insertproduct(vo);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("icnt", insertCnt);
			req.setAttribute("pageNum", mr.getParameter("pageNum"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	// with상품 목록 조회
	public void withproductList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 5;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectproductCnt(searchText, searchType);
		cnt = dao.getproductCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<clothVO> list = dao.getproductList(start, end, searchText, searchType);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}


	// 상품 수정 폼
	public void h_productupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 상세페이지 조회
		clothVO vo = dao.getproduct(num);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;
		String opart = req.getParameter("opart");
		
		// 5-1단계. 글 갯수 구하기
		bigsrhCnt = dao.getSelectbigpartCnt();
		if(req.getParameter("opart") != null && !req.getParameter("opart").equals("")) {
			mediumsrhCnt = dao.getSelectmediumpartCnt(opart);
		}
		brandsrhCnt = dao.getbrandCnt();
		
		if(bigsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<bigpartVO> biglist = dao.getbigpartallList();
			req.setAttribute("biglist", biglist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(mediumsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<mediumpartVO> bottommedilist = dao.getmediumallList(Integer.parseInt(opart));
			req.setAttribute("medilist", bottommedilist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(brandsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<BrandVO> brandlist = dao.getbrandallList();
			req.setAttribute("brandlist", brandlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		

		// 기본 값들
		if(req.getParameter("opart") != null && !req.getParameter("opart").equals("")) {
			req.setAttribute("opart1", opart);
		}
		
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
	}

	// 파일 수정 폼
	public void h_productupdateView2(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 상세페이지 조회
		clothVO vo = dao.getproduct(num);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
	}

	// 상품 수정 처리
	public void h_productupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		clothVO vo = new clothVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		vo.setNum(num);
		vo.setMediumcode(Integer.parseInt(req.getParameter("tpart")));
		vo.setName(req.getParameter("name"));
		vo.setTex(req.getParameter("tex"));
		vo.setBrandnum(Integer.parseInt(req.getParameter("brands")));
		vo.setIcon(req.getParameter("icon"));
		if(req.getParameter("plus").equals("plus")) {
			vo.setPlus(Integer.parseInt(req.getParameter("pluspay")));
		} else {
			vo.setPlus(0);
		}
		vo.setSaleprice(Integer.parseInt(req.getParameter("saleprice")));
		vo.setBuyprice(Integer.parseInt(req.getParameter("buyprice")));
		vo.setDeliday(Integer.parseInt(req.getParameter("delidate")));
		if(req.getParameter("delipay").equals("pluspay")) {
			vo.setDeliprice(Integer.parseInt(req.getParameter("deliprice")));
		} else if(req.getParameter("delipay").equals("basepay")) {
			vo.setDeliprice(2500);
		} else {
			vo.setDeliprice(0);
		}
		vo.setContent(req.getParameter("content"));
			
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글쓰기 처리
		int insertCnt = dao.updateproduct(vo);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);

	

	}

	// mainfile 수정
	public void h_productmainfileupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		clothVO vo = new clothVO();
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());

			int number = Integer.parseInt(mr.getParameter("number"));  // 출력용 글번호
			String pageNum = mr.getParameter("pageNum"); // 페이지 번호
			int num = Integer.parseInt(mr.getParameter("num")); // 식별자
			vo.setNum(num);
			if(mr.getFilesystemName("mainfile") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("mainfile"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("mainfile"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setMainfile(mr.getFilesystemName("mainfile"));
			}
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
			
			// 5단계. 글쓰기 처리
			int insertCnt = dao.updatemainfileproduct(vo);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("icnt", insertCnt);
			req.setAttribute("number", number);
			req.setAttribute("pageNum", pageNum);
			req.setAttribute("num", num);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// files 수정
	public void h_productfilesupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		clothVO vo = new clothVO();
		
		// MultipartRequest 타입의 변수 선언
		MultipartRequest mr = null;
		// 업로드할 파일의 최대 사이즈(10 * 1024 * 1024 = 10MB)
		int maxSize = 10 * 1024 * 1024;
		// 임시 파일이 저장되는 논리적인 경로
		String saveDir = req.getRealPath("/fileready/");
		// 업로드할 파일이 위치하게될 물리적인 경로
		String realDir = "C:\\Dev50\\workspace\\JSP_PJ\\WebContent\\fileready\\";
		// 인코딩 타입 : 한글 파일명이 열화되는 것을 방지함
		String encType = "UTF-8";
		try {
			/*
			* DefaultFileRenamePolicy() 객체는 중복된 파일명이 있을 경우, 자동으로 파일명을 변경함
			* (예 : filename.png 가 이미 존재할 경우, filename1.png 과 같이)
			*/
			mr = new MultipartRequest(req, saveDir, maxSize, encType, new DefaultFileRenamePolicy());

			int number = Integer.parseInt(mr.getParameter("number"));  // 출력용 글번호
			String pageNum = mr.getParameter("pageNum"); // 페이지 번호
			int num = Integer.parseInt(mr.getParameter("num")); // 식별자
			if(mr.getFilesystemName("file1") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file1"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file1"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile1(mr.getFilesystemName("file1"));
			}
			if(mr.getFilesystemName("file2") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file2"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file2"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile2(mr.getFilesystemName("file2"));
			}
			if(mr.getFilesystemName("file3") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file3"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file3"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile3(mr.getFilesystemName("file3"));
			}
			if(mr.getFilesystemName("file4") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file4"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file4"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile4(mr.getFilesystemName("file4"));
			}
			if(mr.getFilesystemName("file5") != null) {
				FileInputStream fis = new FileInputStream(saveDir + mr.getFilesystemName("file5"));
				FileOutputStream fos = new FileOutputStream(realDir + mr.getFilesystemName("file5"));
				int data = 0;
				// 논리적인 경로에 저장된 임시 파일을 물리적인 경로로 복사함
				while((data = fis.read()) != -1) {
				fos.write(data);
				}
				fis.close();
				fos.close();
				vo.setFile5(mr.getFilesystemName("file5"));
			}
			
			// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
			MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
			
			// 5단계. 글쓰기 처리
			int insertCnt = dao.updatefilesproduct(vo);
			
			// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
			req.setAttribute("icnt", insertCnt);
			req.setAttribute("number", number);
			req.setAttribute("pageNum", pageNum);
			req.setAttribute("num", num);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// withitems 수정
	public void h_productwithitemsupdatePro(HttpServletRequest req, HttpServletResponse res) {
		int number = Integer.parseInt(req.getParameter("number"));  // 출력용 글번호
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		// 3단계. 입력받은 값을 받아와 바구니 담음
		clothVO vo = new clothVO();
		
		if(req.getParameter("withitem") != "") {
			vo.setWithprdnum(Integer.parseInt(req.getParameter("withitem")));
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글쓰기 처리
		int insertCnt = dao.updatewithitemsproduct(vo);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("number", number);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
	}


	// product 삭제
	public void h_productdeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String pageNum = req.getParameter("pageNum");
		String[] checked = null;
		if(req.getParameterValues("productchecks") != null) {
			checked = req.getParameterValues("productchecks");
		} else {
			checked = new String[]{req.getParameter("onenum")};
		}
		System.out.println("checked : " + checked[0]);
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteproduct(checked);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", pageNum);
	}

	// 메뉴 리스트 조회
	public void menuList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		String name = req.getParameter("name");
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectprdCnt(name);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 20;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > srhCnt) end = srhCnt;
		
		// 출력용 글번호
		number = srhCnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<clothVO> list = dao.getprdList(start, end, name);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("name", name);
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(srhCnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 상품 상세 조회
	public void productclick(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 상세페이지 조회
		clothVO vo = dao.getproduct(num);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		
		int colorsrhCnt = 0;
		int sizesrhCnt = 0;
		String opart = req.getParameter("color");
		String size = req.getParameter("size");
		
		// 5-1단계. 글 갯수 구하기
		colorsrhCnt = dao.getSelectcolorCnt(num);
		
		if(req.getParameter("color") != null && !req.getParameter("color").equals("")) {
			sizesrhCnt = dao.getSelectsizeCnt(num, Integer.parseInt(opart));
		}
		
		if(colorsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<colorVO> colorlist = dao.getSelectcolorList(num);
			req.setAttribute("colorlist", colorlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(sizesrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<sizeVO> sizelist = dao.getSelectsizeList(num, Integer.parseInt(opart));
			req.setAttribute("sizelist", sizelist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(req.getParameter("color") != null && !req.getParameter("color").equals("")
				&& req.getParameter("size") != null && !req.getParameter("size").equals("")) {
			stockVO stockvo = dao.getcs(num, Integer.parseInt(opart), Integer.parseInt(size));
			req.setAttribute("svo", stockvo); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(req.getParameter("color") != null && !req.getParameter("color").equals("")) {
			req.setAttribute("opart1", opart);
		}
		if(req.getParameter("size") != null && !req.getParameter("size").equals("")) {
			req.setAttribute("opart2", size);
		}
		
		// 기본 값들
		req.setAttribute("colorsrhCnt", colorsrhCnt);
		req.setAttribute("sizesrhCnt", sizesrhCnt);

	}


	// 주문 상세 폼
	public void orderform(HttpServletRequest req, HttpServletResponse res) {
		int prdnum = Integer.parseInt(req.getParameter("num"));
		int colorcode = Integer.parseInt(req.getParameter("color"));
		int sizecode = Integer.parseInt(req.getParameter("size"));
		int count = Integer.parseInt(req.getParameter("count"));
		// 3단계. 입력받은 값을 받아오기
		String strid = (String)req.getSession().getAttribute("memId");

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idCheck(strid,0);
		int CountChkCnt = dao.countChk(prdnum, colorcode, sizecode, count);
		
		// 5-2단계. 있으면 로그인한 id로 정보 조회
		if(selectCnt == 1 && CountChkCnt >= 0) {
			MemberVO guestvo = dao.getMemberInfo(strid);
			req.setAttribute("gvo", guestvo);
			clothVO clothvo = dao.getproduct(prdnum);
			req.setAttribute("cvo", clothvo);
			stockVO stockvo = dao.getcs(prdnum, colorcode, sizecode);
			req.setAttribute("svo", stockvo);
			req.setAttribute("count", count);
			
		}
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("swh", req.getParameter("swh"));
		req.setAttribute("swit", req.getParameter("swit"));
		req.setAttribute("num", prdnum);
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("ucnt", CountChkCnt);
		
	}


	// 사이즈 컬러 상품 추가 폼
	public void cswriteForm(HttpServletRequest req, HttpServletResponse res) {
		int num = Integer.parseInt(req.getParameter("num"));
		int colorsrhCnt = 0;
		int sizesrhCnt = 0;
				
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<stockVO> dao = MemberDAOImpl.getInstance();
		// 5-2단계. 상세페이지 조회
		clothVO vo = dao.getproduct(num);
		req.setAttribute("vo", vo);
		
		colorsrhCnt = dao.getcolorCnt();
		sizesrhCnt = dao.getsizeCnt();

		
		if(colorsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<colorVO> colorlist = dao.getcolorList();
			req.setAttribute("colorlist", colorlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(sizesrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<sizeVO> sizelist = dao.getsizeList();
			req.setAttribute("sizelist", sizelist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		req.setAttribute("num", num);
		req.setAttribute("colorsrhCnt", colorsrhCnt);
		req.setAttribute("sizesrhCnt", sizesrhCnt);
		req.setAttribute("pageNum", req.getParameter("pageNum"));
	}


	// 사이즈 컬러 상품 추가 처리
	public void csPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		int num = Integer.parseInt(req.getParameter("num"));
		stockVO vo = new stockVO();
		vo.setColorcode(Integer.parseInt(req.getParameter("color")));
		vo.setSizecode(Integer.parseInt(req.getParameter("size")));
		vo.setState(req.getParameter("state"));
		vo.setCount(Integer.parseInt(req.getParameter("stock")));
		vo.setMaxcount(Integer.parseInt(req.getParameter("maxcount")));
		vo.setPrdnum(num);;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<stockVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글쓰기 처리
		int insertCnt = dao.insertcs(vo);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("pageNum", req.getParameter("pageNum"));
	}

	// cs 수정 폼
	public void h_csupdateView(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 상세페이지 조회
		clothVO vo = dao.getproduct(num);
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
		req.setAttribute("vo", vo);
		
		int colorsrhCnt = 0;
		int sizesrhCnt = 0;
		String opart = req.getParameter("color");
		String size = req.getParameter("size");
		
		// 5-1단계. 글 갯수 구하기
		colorsrhCnt = dao.getSelectcolorCnt(num);
		
		if(req.getParameter("color") != null && !req.getParameter("color").equals("")) {
			sizesrhCnt = dao.getSelectsizeCnt(num, Integer.parseInt(opart));
		}
		
		if(colorsrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<colorVO> colorlist = dao.getSelectcolorList(num);
			req.setAttribute("colorlist", colorlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(sizesrhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<sizeVO> sizelist = dao.getSelectsizeList(num, Integer.parseInt(opart));
			req.setAttribute("sizelist", sizelist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(req.getParameter("color") != null && !req.getParameter("color").equals("")
				&& req.getParameter("size") != null && !req.getParameter("size").equals("")) {
			stockVO stockvo = dao.getcs(num, Integer.parseInt(opart), Integer.parseInt(size));
			req.setAttribute("stockvo", stockvo); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		if(req.getParameter("color") != null && !req.getParameter("color").equals("")) {
			req.setAttribute("opart1", opart);
		}
		if(req.getParameter("size") != null && !req.getParameter("size").equals("")) {
			req.setAttribute("opart2", size);
		}
		// 기본 값들
		req.setAttribute("colorsrhCnt", colorsrhCnt);
		req.setAttribute("sizesrhCnt", sizesrhCnt);
	}

	public void h_csupdatePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		stockVO vo = new stockVO();
		
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		String pageNum = req.getParameter("pageNum"); // 페이지 번호
		int num = Integer.parseInt(req.getParameter("num")); // 식별자
		vo.setPrdnum(num);
		vo.setColorcode(Integer.parseInt(req.getParameter("color")));
		vo.setSizecode(Integer.parseInt(req.getParameter("size")));
		vo.setCount(Integer.parseInt(req.getParameter("stock")));
		vo.setState(req.getParameter("state"));
		vo.setMaxcount(Integer.parseInt(req.getParameter("maxcount")));
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<stockVO> dao = MemberDAOImpl.getInstance();
		
		// 5단계. 글쓰기 처리
		int insertCnt = dao.updatecs(vo);
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("pageNum", pageNum);
		req.setAttribute("num", num);
	}


	// 주문 처리
	public void orderPro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아와 바구니 담음
		orderVO vo = new orderVO();
		
		int pluspay = 0;
		int asd = 0;
		// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
		// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
		vo.setGid((String)req.getSession().getAttribute("memId"));
		vo.setPrdnum(Integer.parseInt(req.getParameter("num")));
		System.out.println(vo.getPrdnum());
		vo.setColorcode(Integer.parseInt(req.getParameter("color")));
		vo.setSizecode(Integer.parseInt(req.getParameter("size")));
		vo.setCount(Integer.parseInt(req.getParameter("count")));
		vo.setRealprice(Integer.parseInt(req.getParameter("price")));
		vo.setPrice(Integer.parseInt(req.getParameter("price")));
		vo.setBankname(req.getParameter("bank"));
		vo.setPay_option(req.getParameter("howpay"));
		vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		if(req.getParameter("howpay").equals("notacc")) {
			vo.setState("입금전");
			vo.setDepositname(req.getParameter("depositname"));
		} else {
			vo.setState("배송준비중");
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		if(req.getParameter("pluspay") != null && req.getParameter("pluspay") != "") {
			pluspay = Integer.parseInt(req.getParameter("pluspay"));
			int prdplus = Integer.parseInt(req.getParameter("prdplus")) * Integer.parseInt(req.getParameter("count"));
			vo.setUseplus(pluspay);
			vo.setPrice(Integer.parseInt(req.getParameter("price")) - pluspay);
			asd = dao.updategplus(pluspay,prdplus,(String)req.getSession().getAttribute("memId"));
		}
		
		
		int updateCnt = dao.updatecs(vo);
		
		if(updateCnt != 0) {
			int insertCnt = dao.insertorder(vo);
			req.setAttribute("icnt", insertCnt);
			if(!req.getParameter("swit").equals("") && req.getParameter("swit") != null) {
				if(req.getParameter("swh").equals("1")) {
					dao.deletecart(Integer.parseInt(req.getParameter("swit")));
				} else if(req.getParameter("swh").equals("2")) {
					dao.deletewish(Integer.parseInt(req.getParameter("swit")));
				}
			}
		}
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("ucnt", updateCnt);
	}

	// 주문 목록 리스트
	public void orderlist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		String state = "";
		Date firstday = null;
		Date lastday = null;
		
		// 검색하기 위한 조건들
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("state") != null) {
			state = req.getParameter("state");
			System.out.println("state" + state);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectOrderCnt(state, firstday, lastday, (String)req.getSession().getAttribute("memId"));
		cnt = dao.getorderCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(req.getParameter("statenumber") != null) {
			int updateCnt = dao.updatestate(Integer.parseInt(req.getParameter("statenumber")), Integer.parseInt(req.getParameter("num")), 0);
			req.setAttribute("ucnt", updateCnt);
			req.setAttribute("snum", Integer.parseInt(req.getParameter("statenumber")));
		}
		
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<orderVO> list = dao.getorderList(start, end, state, firstday, lastday, (String)req.getSession().getAttribute("memId"));
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 장바구니 추가
	public void cartPro(HttpServletRequest req, HttpServletResponse res) {
		int prdnum = Integer.parseInt(req.getParameter("num"));
		int colorcode = Integer.parseInt(req.getParameter("color"));
		int sizecode = Integer.parseInt(req.getParameter("size"));
		int count = Integer.parseInt(req.getParameter("count"));
		// 3단계. 입력받은 값을 받아오기
		String strid = (String)req.getSession().getAttribute("memId");
		
		// 3단계. 입력받은 값을 받아와 바구니 담음
		cartVO vo = new cartVO();

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<cartVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idCheck(strid,0);
		int CountChkCnt = dao.countChk(prdnum, colorcode, sizecode, count);
		
		// 5-2단계. 있으면 로그인한 id로 정보 조회
		if(selectCnt == 1 && CountChkCnt >= 0) {
			// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
			// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
			vo.setGid((String)req.getSession().getAttribute("memId"));
			vo.setPrdnum(Integer.parseInt(req.getParameter("num")));
			System.out.println(vo.getPrdnum());
			vo.setColorcode(Integer.parseInt(req.getParameter("color")));
			vo.setSizecode(Integer.parseInt(req.getParameter("size")));
			vo.setCount(Integer.parseInt(req.getParameter("count")));
			vo.setPrice(Integer.parseInt(req.getParameter("price")));
			vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		}
		
		int insertCnt = dao.insertcart(vo);
		if(insertCnt != 0) {
			if(req.getParameter("swit") != null && !req.getParameter("swit").equals("")) {
				dao.deletewish(Integer.parseInt(req.getParameter("swit")));
			}
		}
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("ucnt", CountChkCnt);
	}

	// 장바구니 목록 리스트
	public void cartlist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<cartVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectCartCnt((String)req.getSession().getAttribute("memId"));
		cnt = dao.getcartCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<orderVO> list = dao.getcartList(start, end, (String)req.getSession().getAttribute("memId"));
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 장바구니 삭제
	public void cartdeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deletecart(Integer.parseInt(req.getParameter("num")));
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
	}

	// 장바구니 모두 삭제
	public void cartalldeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteallcart((String)req.getSession().getAttribute("memId"));
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
	}

	// 마일리지 리스트
	public void mileagelist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		String state = "";
		Date firstday = null;
		Date lastday = null;
		
		// 검색하기 위한 조건들
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("state") != null) {
			state = req.getParameter("state");
			System.out.println("state" + state);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectOrderCnt(state, firstday, lastday, (String)req.getSession().getAttribute("memId"));
		cnt = dao.getorderCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<orderVO> list = dao.getplusList(start, end, (String)req.getSession().getAttribute("memId"));
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			MemberVO vo = dao.getMemberInfo((String)req.getSession().getAttribute("memId"));
			req.setAttribute("myplus", vo.getPlus());
			int refundplus = dao.getrefundplus((String)req.getSession().getAttribute("memId"));
			req.setAttribute("refundplus", refundplus);
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 마이페이지 리스트
	public void mypagelist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 게시글 목록 조회
		int srhCnt = dao.getSelectOrderCnt(null, null, null, (String)req.getSession().getAttribute("memId"));
		int beforeCnt = dao.selectstateCnt("입금전", (String)req.getSession().getAttribute("memId"));
		int delistartCnt = dao.selectstateCnt("배송준비중", (String)req.getSession().getAttribute("memId"));
		int delingCnt = dao.selectstateCnt("배송중", (String)req.getSession().getAttribute("memId"));
		int deliendCnt = dao.selectstateCnt("배송완료", (String)req.getSession().getAttribute("memId"));
		int cancelCnt = dao.selectstateCnt("주문취소", (String)req.getSession().getAttribute("memId"));
		int refundendCnt = dao.selectstateCnt("환불완료", (String)req.getSession().getAttribute("memId"));
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<orderVO> list = dao.getorderList(0, srhCnt, null, null, null, (String)req.getSession().getAttribute("memId"));
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			MemberVO vo = dao.getMemberInfo((String)req.getSession().getAttribute("memId"));
			req.setAttribute("myplus", vo.getPlus());
			int refundplus = dao.getrefundplus((String)req.getSession().getAttribute("memId"));
			req.setAttribute("refundplus", refundplus);
		}
		
		req.setAttribute("srhCnt", srhCnt);
		req.setAttribute("becnt", beforeCnt);
		req.setAttribute("dscnt", delistartCnt);
		req.setAttribute("dicnt", delingCnt);
		req.setAttribute("decnt", deliendCnt);
		req.setAttribute("ccnt", cancelCnt);
		req.setAttribute("rcnt", refundendCnt);
		
	}

	// 회원 목록
	public void memberlist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		Date firstday = null;
		Date lastday = null;
		int searchType = 0;
		String searchText = "";
		String month = "";
		int pluspay = 0;
		
		// 검색하기 위한 조건들
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<cartVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectmemberCnt(searchText, searchType, firstday, lastday, month, pluspay);
		cnt = dao.getmemberCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 있으면 로그인한 id로 정보 조회
			List<MemberVO> list = dao.getMemberlist(start, end);
			req.setAttribute("list", list);
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 회원 수정
	public void memberForm(HttpServletRequest req, HttpServletResponse res) {
		String strid = req.getParameter("id");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		// 5-2단계. 게시글 목록 조회
		int delistartCnt = dao.selectstateCnt("배송준비중", strid);
		int delingCnt = dao.selectstateCnt("배송중", strid);
		int deliendCnt = dao.selectstateCnt("배송완료", strid);
		int refundendCnt = dao.selectstateCnt("환불완료", strid);
		int refundingCnt = dao.selectstateCnt("환불신청", strid);
		
		// 5-2단계. 게시글 목록 조회
		int orderCnt = dao.getSelectOrderCnt(null, null, null, strid);
		req.setAttribute("orderCnt", orderCnt); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		int qnaCnt = dao.getSelectQnACnt(null, null, null, 0, null, strid);
		req.setAttribute("qnaCnt", qnaCnt);
		int reviewCnt = dao.getSelectreviewCnt(null, null, null, 0, strid);
		req.setAttribute("reviewCnt", reviewCnt);
		MemberVO vo = dao.getMemberInfo(strid);
		req.setAttribute("vo", vo);
		
		req.setAttribute("dscnt", delistartCnt);
		req.setAttribute("dicnt", delingCnt);
		req.setAttribute("decnt", deliendCnt);
		req.setAttribute("recnt", refundendCnt);
		req.setAttribute("ricnt", refundingCnt);
		req.setAttribute("pageNum", req.getParameter("pageNum"));
		
	}


	// 회원 수정 처리
	public void h_memberPro(HttpServletRequest req, HttpServletResponse res) {
		String hostmemo = req.getParameter("hostmemo");
		String strid = req.getParameter("id");
		String pageNum = req.getParameter("pageNum");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<MemberVO> dao = MemberDAOImpl.getInstance();
		
		int updateCnt = dao.updatehostmemoMember(hostmemo, strid);
		
		req.setAttribute("ucnt", updateCnt);
		req.setAttribute("pageNum", pageNum);
	}
	
	// 회원 강탈
	public void h_deletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		String strid = req.getParameter("id");
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idCheck(strid,0);
		int deleteCnt = 0;
		
		// 5-2단계. 있으면 로그인한 id로 삭제
		if(selectCnt == 1) {
			deleteCnt = dao.deleteMember(strid);
		}
		// 5단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("dcnt", deleteCnt);
		req.setAttribute("pageNum", req.getParameter("pageNum"));
	}


	// 관리자 주문 목록 리스트
	public void h_orderlist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		String state = "";
		Date firstday = null;
		Date lastday = null;
		
		// 검색하기 위한 조건들
		if(req.getParameter("firstday") != null && req.getParameter("lastday") != null) {
			firstday = java.sql.Date.valueOf(req.getParameter("firstday"));
			lastday = java.sql.Date.valueOf(req.getParameter("lastday"));
		}
		System.out.println("srchTdae" + req.getParameter("firstday"));
		System.out.println("srchdaye" + req.getParameter("lastday"));
		if(req.getParameter("state") != null) {
			state = req.getParameter("state");
			System.out.println("state" + state);
		}
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		if(req.getParameter("ordernum") != null && req.getParameter("ordernum") != "" 
				&& req.getParameter("orderstate") != null && req.getParameter("orderstate") != ""
				&& req.getParameter("gid") != null && req.getParameter("gid") != ""
				&& req.getParameter("prdnum") != null && req.getParameter("prdnum") != "") {
				int stateswitch = dao.h_updatestate(Integer.parseInt(req.getParameter("ordernum")), req.getParameter("orderstate"),
				req.getParameter("gid"), Integer.parseInt(req.getParameter("prdnum")));
		}
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectOrderCnt(state, firstday, lastday, null);
		cnt = dao.getorderCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<orderVO> list = dao.getorderList(start, end, state, firstday, lastday, null);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 방문자 토탈
	public void clicktotal(HttpServletRequest req, HttpServletResponse res) {
		String year = null;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		year = req.getParameter("yearname");
		
		if(year == null) {
			year = "2019";
		}
		
		int[] clicktotal = dao.getclicktotal(year);
		
		req.setAttribute("clicktotal", clicktotal);
		
	}

	// 신규멤버
	public void newmembertotal(HttpServletRequest req, HttpServletResponse res) {
		String year = null;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		year = req.getParameter("yearname");
		
		if(year == null) {
			year = "2019";
		}
		
		int[] newmembertotal = dao.getnewmembertotal(year);
		
		req.setAttribute("newtotal", newmembertotal);
	}

	// 주문 통합
	public void ordertotal(HttpServletRequest req, HttpServletResponse res) {
		String year = null;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		year = req.getParameter("yearname");
		
		if(year == null) {
			year = "2019";
		}
		
		int[] oredermembertotal = dao.getordermembertotal(year);
		int[] orederCnttotal = dao.getorderCnttotal(year);
		int[] orederpricetotal = dao.getorderpricetotal(year);
		
		req.setAttribute("omtotal", oredermembertotal);
		req.setAttribute("octotal", orederCnttotal);
		req.setAttribute("optotal", orederpricetotal);
	}

	// 판매 토탈
	public void saletotal(HttpServletRequest req, HttpServletResponse res) {
		String year = null;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		year = req.getParameter("yearname");
		
		if(year == null) {
			year = "2019";
		}
		
		int[] orederCnttotal = dao.getorderCnttotal(year);
		int[] orederpricetotal = dao.getorderpricetotal(year);
		int[] orederrealpricetotal = dao.getorderrealpricetotal(year);
		int[] orederrefundtotal = dao.getorderrefundtotal(year);
		
		req.setAttribute("octotal", orederCnttotal);
		req.setAttribute("optotal", orederpricetotal);
		req.setAttribute("orptotal", orederrealpricetotal);
		req.setAttribute("ortotal", orederrefundtotal);
		
	}

	// 카테고리 순위
	public void category(HttpServletRequest req, HttpServletResponse res) {
		String year = null;
		int number = 1;	   // 출력용 글번호	
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<CategoryVO> dao = MemberDAOImpl.getInstance();
		
		year = req.getParameter("yearname");
		
		if(year == null) {
			year = "2019";
		}
		
		List<CategoryVO> list = dao.getCategoryrank(year);
		
		req.setAttribute("list", list);
		req.setAttribute("number", number); // 출력용 글번호
	}

	// 판매순위
	public void salerank(HttpServletRequest req, HttpServletResponse res) {
		String year = null;
		int number = 1;	   // 출력용 글번호	
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<SalerankVO> dao = MemberDAOImpl.getInstance();
		
		year = req.getParameter("yearname");
		
		if(year == null) {
			year = "2019";
		}
		
		List<SalerankVO> list = dao.getSalerank(year);
		
		req.setAttribute("list", list);
		req.setAttribute("number", number); // 출력용 글번호
	}

	// 적립금 순위
	public void pluspay(HttpServletRequest req, HttpServletResponse res) {
		int number = 1;	   // 출력용 글번호	
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<PluspayVO> dao = MemberDAOImpl.getInstance();
		
		
		List<PluspayVO> list = dao.getPluspay();
		
		req.setAttribute("list", list);
		req.setAttribute("number", number); // 출력용 글번호
	}


	// 관리자 메인
	public void hostmain(HttpServletRequest req, HttpServletResponse res) {
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<orderVO> dao = MemberDAOImpl.getInstance();
		
		int monthorederCnttotal = dao.getmonthorderCnttotal();
		int monthorederrealpricetotal = dao.getmonthorderrealpricetotal();
		
		int dayorederCnttotal = dao.getdayorderCnttotal();
		int todayorederrealprice = dao.getdayorderrealpricetotal();
		int daycancelCnttotal = dao.getdaycancelCnttotal();
		int todayclicktotal = dao.getdayclicktotal();
		int todayQnACnt = dao.selectQnACnt();
		int todaynewmemberCnt = dao.selectnewmember();


		int[] orederpricetotal = dao.getweekpricetotal();
		int[] payendCnt = dao.selectPayendCnt();
		int[] delistartCnt = dao.selectDeliStartCnt();
		int[] delingCnt = dao.selectDeliingCnt();
		int[] deliendCnt = dao.selectDeliEndCnt();
		int[] cancelCnt = dao.selectCancelCnt();
		int[] reviewCnt = dao.selectReviewCnt();
		
		int[] day = new int[7];
		int[] month = new int[7];
		String[] year = new String[2];
		java.util.Date date = new java.util.Date(); /* 현재 */
		day[0] = date.getDate();
		month[0] = date.getMonth()+1;
		year[0] = date.toLocaleString().substring(0, 4);
		for(int i = 1; i<7; i++) {
			date.setDate(date.getDate() - 1);
			day[i] = date.getDate();
			month[i] = date.getMonth()+1;
		}
		year[1] = date.toLocaleString().substring(0, 4);
		
		req.setAttribute("day", day);
		req.setAttribute("month", month);
		req.setAttribute("year", year);
		
		req.setAttribute("monthorederCnttotal", monthorederCnttotal);
		req.setAttribute("monthorederrealpricetotal", monthorederrealpricetotal);
		
		req.setAttribute("dayorederCnttotal", dayorederCnttotal);
		req.setAttribute("todayorederrealprice", todayorederrealprice);
		req.setAttribute("daycancelCnttotal", daycancelCnttotal);
		req.setAttribute("todayclicktotal", todayclicktotal);
		req.setAttribute("todayQnACnt", todayQnACnt);
		req.setAttribute("todaynewmemberCnt", todaynewmemberCnt);
		
		req.setAttribute("orederpricetotal", orederpricetotal);
		req.setAttribute("payendCnt", payendCnt);
		req.setAttribute("delistartCnt", delistartCnt);
		req.setAttribute("delingCnt", delingCnt);
		req.setAttribute("deliendCnt", deliendCnt);
		req.setAttribute("cancelCnt", cancelCnt);
		req.setAttribute("reviewCnt", reviewCnt);
	}


	// 내 게시물
	public void myboard(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		String strid = (String)req.getSession().getAttribute("memId");
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<MyBoardVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectMyBoardCnt(searchText, searchType, strid);
		cnt = dao.getMyBoardCnt(strid);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 10;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<MyBoardVO> list = dao.myboard(start, end, searchText, searchType, strid);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");
		
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("schType", searchType); // 타입
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
		
	}

	// 찜하기 추가
	public void wishlistPro(HttpServletRequest req, HttpServletResponse res) {
		int prdnum = Integer.parseInt(req.getParameter("num"));
		int colorcode = Integer.parseInt(req.getParameter("color"));
		int sizecode = Integer.parseInt(req.getParameter("size"));
		int count = Integer.parseInt(req.getParameter("count"));
		// 3단계. 입력받은 값을 받아오기
		String strid = (String)req.getSession().getAttribute("memId");
		
		// 3단계. 입력받은 값을 받아와 바구니 담음
		wishVO vo = new wishVO();

		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<wishVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 로그인 정보가 있는지 확인
		int selectCnt = dao.idCheck(strid,0);
		int CountChkCnt = dao.countChk(prdnum, colorcode, sizecode, count);
		
		// 5-2단계. 있으면 로그인한 id로 정보 조회
		if(selectCnt == 1 && CountChkCnt >= 0) {
			// db에서 reg_date가 default로 sysdate로 작성해놓았으므로
			// 별도로 지정안할시 sysdate로 적용되고, 지정할 경우 로컬이 우선순위
			vo.setGid((String)req.getSession().getAttribute("memId"));
			vo.setPrdnum(Integer.parseInt(req.getParameter("num")));
			System.out.println(vo.getPrdnum());
			vo.setColorcode(Integer.parseInt(req.getParameter("color")));
			vo.setSizecode(Integer.parseInt(req.getParameter("size")));
			vo.setCount(Integer.parseInt(req.getParameter("count")));
			vo.setPrice(Integer.parseInt(req.getParameter("price")));
			vo.setReg_date(new Timestamp(System.currentTimeMillis()));
		}
		
		int insertCnt = dao.insertwish(vo);
		if(insertCnt != 0) {
			if(req.getParameter("swit") != null) {
				dao.deletecart(Integer.parseInt(req.getParameter("swit")));
			}
		}
		req.setAttribute("icnt", insertCnt);
		req.setAttribute("scnt", selectCnt);
		req.setAttribute("ucnt", CountChkCnt);
	}

	// 찜하기 목록 리스트
	public void wishlist(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 10;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int cnt = 0; 	   // 글 갯수
		int srhCnt = 0;	   // 검색한 글 갯수
		int brandsrhCnt = 0;	   // 검색한 글 갯수
		int bigsrhCnt = 0;	   // 검색한 글 갯수
		int mediumsrhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<cartVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectWishCnt((String)req.getSession().getAttribute("memId"));
		cnt = dao.getWishCnt();
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > cnt) end = srhCnt;
		
		// 출력용 글번호
		number = cnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<orderVO> list = dao.getwishList(start, end, (String)req.getSession().getAttribute("memId"));
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("cnt", cnt); // 글갯수
		req.setAttribute("mediumsrhCnt", mediumsrhCnt);
		req.setAttribute("bigsrhCnt", bigsrhCnt);
		req.setAttribute("brandsrhCnt", brandsrhCnt);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(cnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 찜하기 삭제
	public void wishlistdeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deletewish(Integer.parseInt(req.getParameter("num")));
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
	}

	// 찜하기 모두 삭제
	public void wishlistalldeletePro(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 입력받은 값을 받아오기
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 패스워드 인증
		int deleteCnt = 0;
		
		// 5-2단계. 맞으면 게시글 삭제
		deleteCnt = dao.deleteallwish((String)req.getSession().getAttribute("memId"));
		
		// 6단계. request나 session에 처리결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("dcnt", deleteCnt);
	}

	// 검색 리스트 조회
	public void searchList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 0;	   // 현재페이지 시작 글번호
		int end = 0; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		int searchType = 0;
		String searchText = "";
		
		// 검색하기 위한 조건들
		if(req.getParameter("searchType") != null) {
			searchType = Integer.parseInt(req.getParameter("searchType"));
			System.out.println("srchType" + searchType);
		}
		if(req.getParameter("srch") != null) {
			searchText = req.getParameter("srch");
			System.out.println("srchtext" + searchText);
		}
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getSelectSearchCnt(searchText, searchType);
		
		// 5-2단계. 페이지 갯수 구하기
		pageNum = req.getParameter("pageNum");
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 20;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		currentPage = Integer.parseInt(pageNum); // 현재페이지 : 1
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		// 현재페이지 시작 글번호(페이지별)
		start = (currentPage - 1) * pageSize + 1;
		
		// 현재페이지 마지막 글번호
		end = start + pageSize - 1;
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > srhCnt) end = srhCnt;
		
		// 출력용 글번호
		number = srhCnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<clothVO> list = dao.getSearchList(start, end, searchText, searchType);
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		req.setAttribute("searchtext", searchText);
		
		if(srhCnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

	// 아이디 찾기
	public void findid(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int type = Integer.parseInt(req.getParameter("typesel"));  // 출력용 글번호
		String name = req.getParameter("GNameN"); // 페이지 번호
		String email = req.getParameter("idName") + "@" +
				req.getParameter("urlcode");
		int idCnt = 0;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<MemberVO> dao = MemberDAOImpl.getInstance();
		List<MemberVO> vo = dao.findid(name, email, type);
		if(vo != null) {
			idCnt = 1;
		}
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("type", type);
		req.setAttribute("idCnt", idCnt);
		req.setAttribute("name", name);
		req.setAttribute("email", email);
		req.setAttribute("vo", vo);
	}
	
	// 아이디 찾기
	public void findpwd(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		int type = Integer.parseInt(req.getParameter("typesel"));  // 출력용 글번호
		String strid = req.getParameter("useridN"); // 페이지 번호
		String email = req.getParameter("idName") + "@" +
				req.getParameter("urlcode");
		int idCnt = 0;
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<MemberVO> dao = MemberDAOImpl.getInstance();
		MemberVO vo = dao.findpwd(strid, email, type);
		if(vo != null) {
			idCnt = 1;
		}
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		req.setAttribute("type", type);
		req.setAttribute("idCnt", idCnt);
		req.setAttribute("id", strid);
		req.setAttribute("email", email);
		req.setAttribute("vo", vo);
	}

	// 메뉴 리스트 조회
	public void prdList(HttpServletRequest req, HttpServletResponse res) {
		// 3단계. 화면으로 부터 입력받은 값을 받아온다.
		// 페이징 처리 계산 처리
		int pageSize = 0;  // 한페이지당 출력할 글 갯수
		int pageBlock = 5; // 한 블럭당 페이지 갯수
		
		int srhCnt = 0;	   // 검색한 글 갯수
		int start = 1;	   // 현재페이지 시작 글번호
		int end = 7; 	   // 현재페이지 마지막 글번호
		int number = 0;	   // 출력용 글번호
		String pageNum = ""; // 페이지 번호
		int currentPage = 0; // 현재 페이지
		
		int pageCount = 0; // 페이지 갯수
		int startPage = 0; // 시작 페이지
		int endPage = 0;   // 마지막 페이지
		
		// 4단계. 다형성 적용, 싱글톤 방식으로 dao 객체 생성
		MemberDAO<clothVO> dao = MemberDAOImpl.getInstance();
		
		// 5-1단계. 글 갯수 구하기
		srhCnt = dao.getproductCnt();
		
		// 페이지 사이즈 구하기
		if(req.getParameter("pageSize") == null) {
			pageSize = 20;
		} else {
			pageSize = Integer.parseInt(req.getParameter("pageSize"));
		}
		
		if(pageNum == null) {
			pageNum = "1"; // 첫페이지를 1페이지로 지정
		}
		
		// 글 30건 기준
		
		// 현재 페이지 출력
		System.out.println(currentPage);
		
		// 페이지 갯수 6 = 30/5 + 0;
		pageCount = (srhCnt / pageSize) + (srhCnt % pageSize == 0 ? 0 : 1); // 페이지 갯수 + 나머지 있으면 1
		/*
		 * if(cnt%5 == 0) { pageCount = cnt/pageSize; } else { pageCount =
		 * (cnt/pageSize) + 1; }
		 */
		
		System.out.println("start : " + start);
		System.out.println("end : " + end);
		
		if(end > srhCnt) end = srhCnt;
		
		// 출력용 글번호
		number = srhCnt - (currentPage - 1) * pageSize;
		
		System.out.println("number : " + number);
		System.out.println("pageSize : " + pageSize);
		
		if(srhCnt > 0) {
			// 5-2단계. 게시글 목록 조회
			List<clothVO> outerlist = dao.getprdList(start, end, "outer");
			req.setAttribute("outerlist", outerlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> toplist = dao.getprdList(start, end, "top");
			req.setAttribute("toplist", toplist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> shirtlist = dao.getprdList(start, end, "shirt");
			req.setAttribute("shirtlist", shirtlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> knitlist = dao.getprdList(start, end, "knit");
			req.setAttribute("knitlist", knitlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> bottomlist = dao.getprdList(start, end, "bottom");
			req.setAttribute("bottomlist", bottomlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> suitlist = dao.getprdList(start, end, "suit");
			req.setAttribute("suitlist", suitlist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> acclist = dao.getprdList(start, end, "acc");
			req.setAttribute("acclist", acclist); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
			List<clothVO> list = dao.productlist();
			req.setAttribute("list", list); // 큰바구니 : 게시글 목록 cf) 작은 바구니 : 게시글 1건
		}
		
		// 6단계. request나 session에 처리 결과를 저장(jsp에 전달하기 위함)
		// 1 = (1/3)*3+1
		startPage = (currentPage / pageBlock) * pageBlock + 1;
		
		if(currentPage % pageBlock == 0) startPage -= pageBlock;
		System.out.println("startPage : " + startPage);
		
		// 마지막페이지
		// 3 = 1 + 3 - 1
		endPage = startPage + pageBlock - 1;
		if(endPage > pageCount) endPage = pageCount;
		System.out.println("endpage : " + endPage);
		
		System.out.println("========================");	 	
	 	
		req.setAttribute("pageSize", pageSize);
		req.setAttribute("srhCnt", srhCnt); // 검색한 글갯수
		req.setAttribute("number", number); // 출력용 글번호
		req.setAttribute("pageNum", pageNum); // 페이지번호
		
		if(srhCnt>0) {
			req.setAttribute("startPage", startPage); // 시작페이지
			req.setAttribute("endPage", endPage); // 마지막 페이지
			req.setAttribute("pageBlock", pageBlock); // 출력할 페이지 갯수
			req.setAttribute("pageCount", pageCount); // 페이지 갯수
			req.setAttribute("currentPage", currentPage); // 현재 페이지
		}
	}

}
