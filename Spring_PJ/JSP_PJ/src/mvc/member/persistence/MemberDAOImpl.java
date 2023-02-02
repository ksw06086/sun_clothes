package mvc.member.persistence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

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


public class MemberDAOImpl<E> implements MemberDAO{

	// 싱글톤방식 : 객체를번만 생성하겠다.(private 객체 생성) 
	private static MemberDAOImpl instance = new MemberDAOImpl();
	
	public static MemberDAOImpl getInstance() {
		if(instance == null) {
			instance = new MemberDAOImpl();
		}
		return instance;
	}
	
	// 커넥션풀 객체를 보관
	DataSource datasource;
	
	// 생성자
	private MemberDAOImpl() {
		try {
			/*
			 * dbcp(database connection Pool) 설정을 읽어서 커넥션을 발급받겠다.
			 * 1. Context : Servers > Tomcat 8.5.. > context.xml 파일의 resource를 분석할 객체
			 */
			Context context = new InitialContext();
			/*
			 * 2. context.xml 검색(lookup)시 resource name으로 찾겠다.(커넥션풀 name : jdbc/Oracle11g)
			 * 3. db 서버가 startup시 이미 커넥션이 50개 만들어진 상태
			 * 4. DataSource에 dbcp 설정된 정보를 읽어들여서 담는다.
			 * 5. 아래 각메소드에서 datasource.getConnection()시 50개 중 1개 커넥션을 받고
			 * 6. 사용이 끝나면 finally에서 conn.close()에서 반납
			 */
			datasource = (DataSource) context.lookup("java:comp/env/jdbc/Oracle11g"); // java:comp/env/명이름
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 중복확인 
	@Override
	public int idCheck(String strId, int member) {
		int selectCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		try {
			conn = datasource.getConnection();
			if(member == 0) {
				sql = "select * from mvc_guest_tbl where id = ?";
			} else {
				sql = "select * from mvc_host_tbl where id = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strId);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				selectCnt = 1;
			} else {
				selectCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return selectCnt;
	}

	// 회원가입 처리
	@Override
	public int insertMember(MemberVO vo, int member) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			if(member == 0) {
				String sql = "insert into mvc_guest_tbl values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, vo.getId());
				pstmt.setString(2, vo.getPwd());
				pstmt.setString(3, vo.getName());
				pstmt.setString(4, vo.getAddress());
				pstmt.setString(5, vo.getAddress1());
				pstmt.setString(6, vo.getAddress2());
				pstmt.setString(7, vo.getHomephone());
				pstmt.setInt(8, 0);
				pstmt.setString(9, vo.getHp());
				pstmt.setString(10, vo.getEmail());
				pstmt.setDate(11, vo.getBirth());
				pstmt.setString(12, vo.getBirthtype());
				pstmt.setString(13, vo.getAcchost());
				pstmt.setString(14, vo.getBank());
				pstmt.setString(15, vo.getAcc());
				pstmt.setTimestamp(16, vo.getReg_date());
				pstmt.setInt(17, 0);
				pstmt.setString(18, null);
				pstmt.setInt(19, 0);
				pstmt.setString(20, null);
			} else {
				String sql = "insert into mvc_host_tbl values (?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, vo.getId());
				pstmt.setString(2, vo.getPwd());
				pstmt.setString(3, vo.getName());
				pstmt.setString(4, vo.getHp());
				pstmt.setString(5, vo.getEmail());
			}
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return insertCnt;
	}

	// 로그인 처리
	@Override
	public int idPwdCheck(String strid, String strpwd, int member) {
		int selectCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			if(member == 0) {
				sql = "select pwd from mvc_guest_tbl where id = ?";
			} else {
				sql = "select pwd from mvc_host_tbl where id = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 패스워드가 일치하면 selectCnt = 1;
				if(rs.getString("pwd").equals(strpwd)) {
					selectCnt = 1;
				} else {
					// 패스워드 일치하지 않으면 selectCnt = -1;
					selectCnt = 0;
				}
			} else {
				// 로그인한 아이디가 없으면 selectCnt = 0;
				selectCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		
		return selectCnt;
	}

	// 삭제처리
	@Override
	public int deleteMember(String strid) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "delete mvc_guest_tbl where id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			
			deleteCnt = pstmt.executeUpdate();
			if(deleteCnt != 0) {
				deleteCnt = 1;
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return deleteCnt;
	}

	@Override
	public MemberVO getMemberInfo(String strid) {
		MemberVO vo = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = datasource.getConnection();
			// 로그인한 화면에서 입력받은 id와 일치한 데이터가 있는지 확인
			String sql = "select * from mvc_guest_tbl where id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			
			rs = pstmt.executeQuery();
			// 2. resultSet에 id에 해당하는 1사람의 회원정보가 존재하면
			if(rs.next()) {
				// 2-1. resultSet을 읽어서 바구니에 담는다.
				// vo 바구니를 생성한다.
				vo = new MemberVO();
				vo.setId(rs.getString("id"));
				vo.setPwd(rs.getString("pwd"));
				vo.setName(rs.getString("name"));
				vo.setAddress(rs.getString("address"));
				vo.setAddress1(rs.getString("address1"));
				vo.setAddress2(rs.getString("address2"));
				vo.setHomephone(rs.getString("homephone"));
				
				vo.setHp(rs.getString("hp"));
				/*
				 * String email = req.getParameter("email1") + "@" + req.getParameter("email2");
				 * vo.setEmail(email);
				 */
				
				vo.setEmail(rs.getString("email"));
				
				vo.setBirth(rs.getDate("birth"));
				
				vo.setBirthtype(rs.getString("birthtype"));
				
				vo.setAcchost(rs.getString("acchost"));
				
				vo.setBank(rs.getString("bank"));
				
				vo.setAcc(rs.getString("acc"));
				
				// reg_date
				vo.setReg_date(rs.getTimestamp("reg_date"));
				vo.setPlus(rs.getInt("plus"));
				vo.setVisitcnt(rs.getInt("visitcnt"));
				vo.setAuth(rs.getInt("auth"));
				vo.setKey(rs.getString("key"));
				vo.setHostmemo(rs.getString("hostmemo"));
				System.out.println(vo.getReg_date());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	@Override
	public int updateMember(MemberVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "update mvc_guest_tbl set pwd = ?, name = ?, "
					+ "address = ?, address1 = ?, address2 = ?, homephone = ?, "
					+ "hp = ?, email = ?, birth = ?, birthtype = ?, acchost = ?, "
					+ "bank = ?, acc = ? where id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getPwd());
			pstmt.setString(2, vo.getName());
			pstmt.setString(3, vo.getAddress());
			pstmt.setString(4, vo.getAddress1());
			pstmt.setString(5, vo.getAddress2());
			pstmt.setString(6, vo.getHomephone());
			pstmt.setString(7, vo.getHp());
			pstmt.setString(8, vo.getEmail());
			pstmt.setDate(9, vo.getBirth());
			pstmt.setString(10, vo.getBirthtype());
			pstmt.setString(11, vo.getAcchost());
			pstmt.setString(12, vo.getBank());
			pstmt.setString(13, vo.getAcc());
			pstmt.setString(14, vo.getId());
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return updateCnt;
	}
	
	// 게시물
	// 게시글 갯수
	@Override
	public int getArticleCnt(int nnumber) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			if(nnumber == 1) {
				sql = "select count(*) as cnt from mvc_notice_tbl";
			} else if(nnumber == 2) {
				sql = "select count(*) as cnt from mvc_FAQ_tbl";
			} else if(nnumber == 3) {
				sql = "select count(*) as cnt from mvc_QnA_tbl";
			} else if(nnumber == 4) {
				sql = "select count(*) as cnt from mvc_review_tbl";
			}
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	
	@Override
	public List<BoardVO> getArticleList(int start, int end) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "select * from " + 
					"(select num, writer, pwd, subject, content, readCnt, ref, ref_step, ref_level, reg_date, ip, rownum rNum " + 
						"from(SELECT * FROM mvc_board_tbl " + 
							"order by ref DESC, ref_step asc" +  // 최신글부터 select
							")" + 
					")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
					" where rNum >= ? and rNum <= ?"; // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
													  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
													  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					BoardVO vo = new BoardVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("writer"));
					vo.setPwd(rs.getString("pwd"));
					vo.setSubject(rs.getString("subject"));
					vo.setContent(rs.getString("content"));
					vo.setReadCnt(rs.getInt("readCnt"));
					vo.setRef(rs.getInt("ref"));
					vo.setRef_step(rs.getInt("ref_step"));
					vo.setRef_level(rs.getInt("ref_level"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setIp(rs.getString("ip"));
					System.out.println(vo.getNum());
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	// 게시글 상세 조회, 게시글 수정을 위한 상세페이지
	@Override
	public E getArticle(int num, int choose) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		E vo = null;
		String sql = "";
		
		try {
			conn = datasource.getConnection();
			if(choose == 1) {
				sql = "select * from( " + 
						"select h.*, a.subject asubject, b.subject bsubject FROM" + 
						" mvc_notice_tbl h, mvc_notice_tbl a, mvc_notice_tbl b" + 
						" where h.nextnum = a.num(+)" + 
						" and h.fwnum = b.num(+) " + 
						"union" + 
						" select num, id, subject, content, reg_date, file1, readcnt, fwnum, nextnum, null atsubject, null bsubject FROM" + 
						" mvc_notice_tbl)" + 
						" where num = ?" + 
						"order by 11 desc, num asc";
			} else if(choose == 2) {
				sql = "select * from( " + 
						"select h.*, a.subject asubject, b.subject bsubject FROM" + 
						" mvc_FAQ_tbl h, mvc_FAQ_tbl a, mvc_FAQ_tbl b" + 
						" where h.nextnum = a.num(+)" + 
						" and h.fwnum = b.num(+) " + 
						"union" + 
						" select num, id, state, subject, content, reg_date, fwnum, nextnum, null atsubject, null bsubject FROM" + 
						" mvc_FAQ_tbl)" + 
						" where num = ?" + 
						"order by asubject desc, bsubject desc, num asc";
			} else if(choose == 3) {
				sql = "select * from( " + 
						"select h.*, a.subject asubject, b.subject bsubject, a.texttype atexttype, b.texttype btexttype FROM" + 
						" mvc_QnA_tbl h, mvc_QnA_tbl a, mvc_QnA_tbl b" + 
						" where h.nextnum = a.num(+)" + 
						" and h.fwnum = b.num(+) " + 
						"union" + 
						" select num, id, state, pwd, subject, content,"
						+ " file1, textType, ref, ref_step, ref_level, "
						+ "reg_date, ip, writestate, fwnum, nextnum, null asubject, null bsubject, null atexttype, null btexttype FROM" + 
						" mvc_QnA_tbl)" + 
						" where num = ?" + 
						"order by asubject desc, bsubject desc, num asc";
			} else if(choose == 4) {
				sql = "select * from( " + 
						"select h.*, a.subject asubject, b.subject bsubject FROM" + 
						" mvc_review_tbl h, mvc_review_tbl a, mvc_review_tbl b" + 
						" where h.nextnum = a.num(+)" + 
						" and h.fwnum = b.num(+) " + 
						"union" + 
						" select num, id, subject, content, readcnt,"
						+ " file1, reg_date, ip, fwnum, nextnum, null asubject, null bsubject FROM" + 
						" mvc_review_tbl)" + 
						" where num = ?" + 
						"order by asubject desc, bsubject desc, num asc";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(choose == 1) {
					vo = (E) new noticeVO();
					((noticeVO) vo).setNum(rs.getInt("num"));
					((noticeVO) vo).setWriter(rs.getString("id"));
					((noticeVO) vo).setSubject(rs.getString("subject"));
					((noticeVO) vo).setContent(rs.getString("content"));
					((noticeVO) vo).setReg_date(rs.getTimestamp("reg_date"));
					((noticeVO) vo).setFile1(rs.getString("file1"));
					((noticeVO) vo).setNextnum(rs.getInt("nextnum"));
					((noticeVO) vo).setFwnum(rs.getInt("fwnum"));
					if(rs.next()) {
							((noticeVO) vo).setNextsubject(rs.getString("asubject"));
							((noticeVO) vo).setFwsubject(rs.getString("bsubject"));
					}
				} else if(choose == 2) {
					vo = (E) new FAQVO();
					((FAQVO) vo).setNum(rs.getInt("num"));
					((FAQVO) vo).setWriter(rs.getString("id"));
					((FAQVO) vo).setState(rs.getString("state"));
					((FAQVO) vo).setSubject(rs.getString("subject"));
					((FAQVO) vo).setContent(rs.getString("content"));
					((FAQVO) vo).setReg_date(rs.getTimestamp("reg_date"));
					((FAQVO) vo).setNextnum(rs.getInt("nextnum"));
					((FAQVO) vo).setFwnum(rs.getInt("fwnum"));
					if(rs.next()) {
						((FAQVO) vo).setNextsubject(rs.getString("asubject"));
						((FAQVO) vo).setFwsubject(rs.getString("bsubject"));
					}
				} else if(choose == 3) {
					vo = (E) new QnAVO();
					((QnAVO) vo).setNum(rs.getInt("num"));
					((QnAVO) vo).setWriter(rs.getString("id"));
					((QnAVO) vo).setState(rs.getString("state"));
					((QnAVO) vo).setPwd(rs.getString("pwd"));
					((QnAVO) vo).setSubject(rs.getString("subject"));
					((QnAVO) vo).setContent(rs.getString("content"));
					((QnAVO) vo).setFile1(rs.getString("file1"));
					((QnAVO) vo).setRef(rs.getInt("ref"));
					((QnAVO) vo).setRef_step(rs.getInt("ref_step"));
					((QnAVO) vo).setRef_level(rs.getInt("ref_level"));
					((QnAVO) vo).setReg_date(rs.getTimestamp("reg_date"));
					((QnAVO) vo).setIp(rs.getString("ip"));
					((QnAVO) vo).setFwnum(rs.getInt("fwnum"));
					((QnAVO) vo).setNextnum(rs.getInt("nextnum"));
					((QnAVO) vo).setTextType(rs.getString("textType"));
					if(rs.next()) {
						((QnAVO) vo).setNextsubject(rs.getString("asubject"));
						((QnAVO) vo).setFwsubject(rs.getString("bsubject"));
						((QnAVO) vo).setNexttextType(rs.getString("atexttype"));
						((QnAVO) vo).setFwtextType(rs.getString("btexttype"));
					}
				} else if(choose == 4) {
					vo = (E) new reviewVO();
					((reviewVO) vo).setNum(rs.getInt("num"));
					((reviewVO) vo).setWriter(rs.getString("id"));
					((reviewVO) vo).setSubject(rs.getString("subject"));
					((reviewVO) vo).setContent(rs.getString("content"));
					((reviewVO) vo).setReg_date(rs.getTimestamp("reg_date"));
					((reviewVO) vo).setReadcnt(rs.getInt("readcnt"));
					((reviewVO) vo).setFile1(rs.getString("file1"));
					((reviewVO) vo).setNextnum(rs.getInt("nextnum"));
					((reviewVO) vo).setFwnum(rs.getInt("fwnum"));
					if(rs.next()) {
							((reviewVO) vo).setNextsubject(rs.getString("asubject"));
							((reviewVO) vo).setFwsubject(rs.getString("bsubject"));
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	@Override
	public void addReadCnt(int num, int choose) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		try {
			conn = datasource.getConnection();
			if(choose == 1) {
				sql = "update mvc_notice_tbl set readcnt = readcnt + 1 where num = ?";
			} else if(choose == 2) {
				sql = "update mvc_FAQ_tbl set readcnt = readcnt + 1 where num = ?";
			} else if(choose == 4) {
				sql = "update mvc_review_tbl set readcnt = readcnt + 1 where num = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 게시글 수정 - 비밀번호 인증
	@Override
	public int numPwdCheck(int num, String pwd) {
		int selectCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			String sql = "select pwd from mvc_QnA_tbl where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 패스워드가 일치하면 selectCnt = 1;
				if(rs.getString("pwd").equals(pwd)) {
					selectCnt = 1;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		
		return selectCnt;
	}

	// 글 수정 처리
	@Override
	public int updateBoard(Object vo, int choose) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// 로그인한 id에 해당하는 데이터가 있고
			if(choose == 1) {
				sql = "update mvc_notice_tbl set content = ? where num = ?";
			} else if(choose == 2) {
				sql = "update mvc_FAQ_tbl set state = ?, content = ? where num = ?";
			} else if(choose == 3) {
				sql = "update mvc_QnA_tbl set content = ?, file1 = ?, texttype = ?, pwd = ? where num = ?";
			}
			pstmt = conn.prepareStatement(sql);
			if(choose == 1) {
				pstmt.setString(1, ((noticeVO) vo).getContent());
				pstmt.setInt(2, ((noticeVO) vo).getNum());
			} else if(choose == 2) {
				pstmt.setString(1, ((FAQVO) vo).getState());
				pstmt.setString(2, ((FAQVO) vo).getContent());
				pstmt.setInt(3, ((FAQVO) vo).getNum());
			} else if(choose == 3) {
				pstmt.setString(1, ((QnAVO) vo).getContent());
				pstmt.setString(2, ((QnAVO) vo).getFile1());
				pstmt.setString(3, ((QnAVO) vo).getTextType());
				pstmt.setString(4, ((QnAVO) vo).getPwd());
				pstmt.setInt(5, ((QnAVO) vo).getNum());
			}
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		
		return updateCnt;
	}

	// 글작성 처리
	@Override
	public int insertBoard (Object vo, int choose) {
		int insertCnt = 0;
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고

			int num = 0;
			int ref = 0;
			int ref_step = 0;
			int ref_level = 0;
			int startNum = 0;
			
			if(choose == 3) {
				num = ((QnAVO) vo).getNum();
				ref = ((QnAVO) vo).getRef();
				ref_step = ((QnAVO) vo).getRef_step();
				ref_level = ((QnAVO) vo).getRef_level();
			}
			
			// 답변글이 아닌경우(제목글인 경우)
			if(num == 0) {
				if(choose == 1) {
					sql = "select max(num) as maxNum from mvc_notice_tbl"; // 최신글부터 가져온다.
				} else if(choose == 2) {
					sql = "select max(num) as maxNum from mvc_FAQ_tbl"; // 최신글부터 가져온다.
				} else if(choose == 3) {
					sql = "select max(num) as maxNum from mvc_QnA_tbl"; // 최신글부터 가져온다.
				} else if(choose == 4) {
					sql = "select max(num) as maxNum from mvc_review_tbl"; // 최신글부터 가져온다.
				} else if(choose == 5) {
					sql = "select max(num) as maxNum from mvc_reply_tbl"; // 최신글부터 가져온다.
				}
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();

				// 첫글이 아닌 경우
				if(rs.next()) {
					ref = rs.getInt("maxNum") + 1;
					System.out.println("첫글이 아닌 경우");
				// 첫글인 경우	
				} else {
					ref = 1;
				}
				
			// 답변글인 경우
			// 삽입할 글보다 아래쪽 글들이 한줄씩 밀려내려간다. 즉 ref_step(=행)이 1씩 증가한다... ref_step을 update시켜라
			} else {
				sql = "update mvc_QnA_tbl set ref_step = ref_step + 1 where ref_step > ? and ref = ?"; 
				pstmt = conn.prepareStatement(sql); 
				pstmt.setInt(1, ref_step);
				pstmt.setInt(2, ref);
				 
				pstmt.executeUpdate(); pstmt.close();
				 
				ref_step += 1; ref_level += 1; 
				
				sql = "update mvc_QnA_tbl set writestate = ? where num = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, "답변완료");
				pstmt.setInt(2, ((QnAVO) vo).getNum());
//					sql = "select ref_step from mvc_board_tbl where ref = ? and ref_level = ? and rownum = 2";
//					pstmt = conn.prepareStatement(sql);
//					pstmt.setInt(1, ref);
//					pstmt.setInt(2, ref_level);
//					
//					rs = pstmt.executeQuery();
//					if(rs.next()) { 
//						ref_step = rs.getInt("ref_step");
//						System.out.println(ref_step);
//						pstmt.close();
//						
//						sql = "update mvc_board_tbl set ref_step = ref_step + 1 where ref_step >= ? and ref = ?"; 
//						pstmt = conn.prepareStatement(sql); 
//						pstmt.setInt(1, ref_step);
//						pstmt.setInt(2, ref);
//						
//						pstmt.executeUpdate(); 
//						pstmt.close();
//						ref_level += 1; 
//					} else { 
//						pstmt.close();
//						sql = "select max(ref_step) as maxStep from mvc_board_tbl where ref = ?"; 
//						pstmt = conn.prepareStatement(sql); 
//						pstmt.setInt(1, ref);
//					  
//						rs = pstmt.executeQuery(); 
//					  
//						if(rs.next()) {
//							ref_step = rs.getInt("maxStep") + 1; 
//							ref_level += 1;
//						}
//						
//						pstmt.close();
//					}
			}
			pstmt.close();
			// 공통부분
			if(choose == 1) {
				sql = "insert into mvc_notice_tbl(num, id, subject, content, reg_date, file1) " + 
						"values (notice_seq.nextval, ?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, ((noticeVO) vo).getWriter());
				pstmt.setString(2, ((noticeVO) vo).getSubject());
				pstmt.setString(3, ((noticeVO) vo).getContent());
				pstmt.setTimestamp(4, ((noticeVO) vo).getReg_date());
				pstmt.setString(5, ((noticeVO) vo).getFile1());
				
				insertCnt = pstmt.executeUpdate();
				
				sql = "select num from (select * from mvc_notice_tbl "
						+ "where num < (select max(num) as maxNum from mvc_notice_tbl) "
						+ "order by num desc) "
						+ "where rownum = 1";
				pstmt = conn.prepareStatement(sql); 
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					sql = "update mvc_notice_tbl set nextnum = ? where num = (select max(num) as maxNum from mvc_notice_tbl)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("num"));
					
					insertCnt = pstmt.executeUpdate();
					pstmt.close();
					sql = "update mvc_notice_tbl set fwnum = (select max(num) as maxNum from mvc_notice_tbl) where num = ?";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("num"));
					
					updateCnt = pstmt.executeUpdate();
				}
			} else if(choose == 2) {
				rs.close();
				sql = "insert into mvc_FAQ_tbl(num, id, state, subject, content, reg_date) " + 
						"values (FAQ_seq.nextval, ?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, ((FAQVO) vo).getWriter());
				pstmt.setString(2, ((FAQVO) vo).getState());
				pstmt.setString(3, ((FAQVO) vo).getSubject());
				pstmt.setString(4, ((FAQVO) vo).getContent());
				pstmt.setTimestamp(5, ((FAQVO) vo).getReg_date());
				
				insertCnt = pstmt.executeUpdate();
				
				
				sql = "select num from (select * from mvc_FAQ_tbl "
						+ "where num < (select max(num) as maxNum from mvc_FAQ_tbl) "
						+ "order by num desc) "
						+ "where rownum = 1";
				pstmt = conn.prepareStatement(sql); 
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					sql = "update mvc_FAQ_tbl set nextnum = ? where num = (select max(num) as maxNum from mvc_FAQ_tbl)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("num"));
					
					insertCnt = pstmt.executeUpdate();
					pstmt.close();
					sql = "update mvc_FAQ_tbl set fwnum = (select max(num) as maxNum from mvc_FAQ_tbl) where num = ?";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("num"));
					
					updateCnt = pstmt.executeUpdate();
				}
			} else if(choose == 3) {
				sql = "insert into mvc_QnA_tbl(num, id, state, pwd, subject, content, "
						+ "file1, texttype, ref, ref_step, ref_level, reg_date, ip) " + 
						"values (QnA_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, ((QnAVO) vo).getWriter());
				pstmt.setString(2, ((QnAVO) vo).getState());
				pstmt.setString(3, ((QnAVO) vo).getPwd());
				pstmt.setString(4, ((QnAVO) vo).getSubject());
				pstmt.setString(5, ((QnAVO) vo).getContent());
				pstmt.setString(6, ((QnAVO) vo).getFile1());
				pstmt.setString(7, ((QnAVO) vo).getTextType());
				pstmt.setInt(8, ref);
				pstmt.setInt(9, ref_step);
				pstmt.setInt(10, ref_level);
				pstmt.setTimestamp(11, ((QnAVO) vo).getReg_date());
				pstmt.setString(12, ((QnAVO) vo).getIp());
				
				insertCnt = pstmt.executeUpdate();
				
				if(ref_level == 0) {
					sql = "select num from (select * from mvc_QnA_tbl "
							+ "where num < (select max(num) as maxNum from mvc_QnA_tbl) "
							+ "order by num desc) "
							+ "where rownum = 1";
					pstmt = conn.prepareStatement(sql); 
					
					rs = pstmt.executeQuery();
					if(rs.next()) {
						sql = "update mvc_QnA_tbl set nextnum = ? where num = (select max(num) as maxNum from mvc_QnA_tbl where fwnum > -1 and nextnum > -1)";
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, rs.getInt("num"));
						
						updateCnt = pstmt.executeUpdate();
						pstmt.close();
						sql = "update mvc_QnA_tbl set fwnum = (select max(num) as maxNum from mvc_QnA_tbl where fwnum > -1 and nextnum > -1) where num = ?";
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, rs.getInt("num"));
						
						updateCnt = pstmt.executeUpdate();
					}
				} else {
					sql = "update mvc_QnA_tbl set nextnum = -1, fwnum = -1 where num = (select max(num) as maxNum from mvc_QnA_tbl)";
					pstmt = conn.prepareStatement(sql);
					
					updateCnt = pstmt.executeUpdate();
				}
			} else if(choose == 4) {
				sql = "insert into mvc_review_tbl(num, id, subject, content, reg_date, file1, ip) " + 
						"values (review_seq.nextval, ?, ?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, ((reviewVO) vo).getWriter());
				pstmt.setString(2, ((reviewVO) vo).getSubject());
				pstmt.setString(3, ((reviewVO) vo).getContent());
				pstmt.setTimestamp(4, ((reviewVO) vo).getReg_date());
				pstmt.setString(5, ((reviewVO) vo).getFile1());
				pstmt.setString(6, ((reviewVO) vo).getIp());
				
				insertCnt = pstmt.executeUpdate();
				
				sql = "select num from (select * from mvc_review_tbl "
						+ "where num < (select max(num) as maxNum from mvc_review_tbl) "
						+ "order by num desc) "
						+ "where rownum = 1";
				pstmt = conn.prepareStatement(sql); 
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					sql = "update mvc_review_tbl set nextnum = ? where num = (select max(num) as maxNum from mvc_review_tbl)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("num"));
					
					updateCnt = pstmt.executeUpdate();
					pstmt.close();
					sql = "update mvc_review_tbl set fwnum = (select max(num) as maxNum from mvc_review_tbl) where num = ?";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("num"));
					
					updateCnt = pstmt.executeUpdate();
				}
			} else if(choose == 5) {
				sql = "insert into mvc_reply_tbl(num, id, content, reg_date, ip, ref) " + 
						"values (reply_seq.nextval, ?, ?, ?, ?, ?)";
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, ((replyVO) vo).getWriter());
				pstmt.setString(2, ((replyVO) vo).getContent());
				pstmt.setTimestamp(3, ((replyVO) vo).getReg_date());
				pstmt.setString(4, ((replyVO) vo).getIp());
				pstmt.setInt(5, ((replyVO) vo).getRef());
				
				insertCnt = pstmt.executeUpdate();
				updateCnt = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		if(updateCnt == 1) {
			return insertCnt;
		} else {
			return 0;
		}
	}

	// 글삭제 처리
	@Override
	public int deleteBoard(int num) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			sql = "select * from mvc_board_tbl where num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				int ref = rs.getInt("ref");
				int ref_step = rs.getInt("ref_step");
				int ref_level = rs.getInt("ref_level");
				
				// 답글이 존재하는지 여부
				sql = "select * from mvc_board_tbl where ref = ? and ref_step = ?+1 and ref_level > ?";
				pstmt.close();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, ref);
				pstmt.setInt(2, ref_step);
				pstmt.setInt(3, ref_level);
				
				rs.close();
				rs = pstmt.executeQuery();
				// 답글이 존재하는 경우
				if(rs.next()) {
					sql = "SELECT min(ref_step) as mi FROM mvc_board_tbl where ref_level <= ? " + 
							"and ref_step > ? and ref = ?";
					pstmt.close();
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, ref_level);
					pstmt.setInt(2, ref_step);
					pstmt.setInt(3, ref);
					/*
					 * sql = "delete mvc_board_tbl where num=? or (ref = ? and ref_level > ?)";
					 * pstmt = conn.prepareStatement(sql); pstmt.setInt(1, num); pstmt.setInt(2,
					 * ref); pstmt.setInt(3, ref_level);
					 */
					rs.close();
					rs = pstmt.executeQuery();
					if(rs.next()) {
						System.out.println("답글이 존재하는 경우");
						if(rs.getInt("mi") > 0) {
							sql = "delete mvc_board_tbl where (num=? or (ref = ? and ref_level > ?)) and ref_step < (SELECT\r\n" + 
									"    min(ref_step)\r\n" + 
									"FROM mvc_board_tbl\r\n" + 
									"where ref_level <= ?\r\n" + 
									"and ref_step > ?\r\n" + 
									"and ref = ?)\r\n" + 
									"and ref_step >= ?";
							pstmt.close();
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, num);
							pstmt.setInt(2, ref);
							pstmt.setInt(3, ref_level);
							pstmt.setInt(4, ref_level);
							pstmt.setInt(5, ref_step);
							pstmt.setInt(6, ref);
							pstmt.setInt(7, ref_step);
							
							deleteCnt = pstmt.executeUpdate();
						} else {
							sql = "delete mvc_board_tbl where num=? or (ref = ? and ref_step > ?)";
							pstmt.close();
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, num); 
							pstmt.setInt(2, ref); 
							pstmt.setInt(3, ref_step);

							deleteCnt = pstmt.executeUpdate();
						}
					}
				} else {
					// 답글이 존재하지 않은 경우
					sql = "delete mvc_board_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, num);
					
					deleteCnt = pstmt.executeUpdate();
					System.out.println("답글이 존재하지 않은 경우");
				}
				
				sql = "update mvc_board_tbl set ref_step = ref_step - ? where ref=? and ref_step > ?";
				pstmt.close();
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, deleteCnt);
				pstmt.setInt(2, ref);
				pstmt.setInt(3, ref_step);
				
				pstmt.executeUpdate();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		
		return deleteCnt;
	}

	// 공지 검색 리스트 조회
	@Override
	public List<noticeVO> getNoticeList(int start, int end, Date firstday, Date lastday, String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<noticeVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, id, subject, content, reg_date, file1, readcnt, fwnum, nextnum, rownum rNum " + 
					"from(SELECT * FROM mvc_notice_tbl ";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += ("where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%') ");
				} else if(searchType == 1) {
					sql += ("where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%' ");
				} else {
					sql += ("where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%' ");
				}
			}
			sql += ("order by num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					pstmt.setInt(5, start);
					pstmt.setInt(6, end);
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setInt(4, start);
					pstmt.setInt(5, end);
				}
			} else {
				pstmt.setInt(1, start);
				pstmt.setInt(2, end);
			}
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					noticeVO vo = new noticeVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("id"));
					vo.setSubject(rs.getString("subject"));
					vo.setContent(rs.getString("content"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setFile1(rs.getString("file1"));
					vo.setReadcnt(rs.getInt("readcnt"));
					vo.setFwnum(rs.getInt("fwnum"));
					vo.setNextnum(rs.getInt("nextnum"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 공지 삭제
	@Override
	public int deleteNoticeBoard(String[] checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_notice_tbl where num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "select * from mvc_notice_tbl where num = ?";
					pstmt = conn.prepareStatement(sql); 
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					rs = pstmt.executeQuery();
					if(rs.next()) {
						if(rs.getInt("fwnum") != 0) {
							sql = "update mvc_notice_tbl set nextnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("nextnum"));
							pstmt.setInt(2, rs.getInt("fwnum"));
							
							pstmt.executeUpdate();
						}
						if(rs.getInt("nextnum") != 0) {
							sql = "update mvc_notice_tbl set fwnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("fwnum"));
							pstmt.setInt(2, rs.getInt("nextnum"));
							
							pstmt.executeUpdate();
						}
						
					}
					pstmt.close();
					sql = "delete mvc_notice_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					deleteCnt = pstmt.executeUpdate();
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// 공지 검색 갯수
	@Override
	public int getSelectNoticeCnt(Date firstday, Date lastday, String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_notice_tbl";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += " where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%')";
				} else if(searchType == 1) {
					sql += " where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%'";
				} else {
					sql += " where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%'";
				}
			}
			pstmt = conn.prepareStatement(sql);
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
				}
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// FAQ 검색 갯수
	@Override
	public int getSelectFAQCnt(Date firstday, Date lastday, String searchText, int searchType,
			String[] state) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_FAQ_tbl";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += " where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%')";
				} else if(searchType == 1) {
					sql += " where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%'";
				} else {
					sql += " where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%'";
				}
				
				if(state != null) {
					sql += " and ";
					for(int i = 0; i < state.length; i++) {
						if(i == 0 && state.length == 1) {
							sql += "state = ?";
						} else if(i == 0){
							sql += "(state = ? ";
						} else if(i == state.length - 1){
							sql += "or state = ?)";
						} else {
							sql += "or state = ? ";
						}
					}
				}
			}
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					if(state != null) {
						for(int i = 0; i < state.length; i++) {
							pstmt.setString(i+5, state[i]);
						}
					}
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					if(state != null) {
						for(int i = 0; i < state.length; i++) {
							pstmt.setString(i+4, state[i]);
						}
					}
				}
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// FAQ 검색 리스트 조회
	@Override
	public List<FAQVO> getFAQList(int start, int end, Date firstday, Date lastday, String searchText,
			int searchType, String[] state) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<FAQVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// FAQ일 때
			sql = "select * from " + 
					"(select num, id, state, subject, content, reg_date, fwnum, nextnum, rownum rNum " + 
						"from(SELECT * FROM mvc_FAQ_tbl";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += (" where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%')");
				} else if(searchType == 1) {
					sql += (" where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%'");
				} else {
					sql += (" where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%'");
				}
				
				if(state != null) {
					sql += " and ";
					for(int i = 0; i < state.length; i++) {
						if(i == 0 && state.length == 1) {
							sql += "state = ?";
						} else if(i == 0){
							sql += "(state = ? ";
						} else if(i == state.length - 1){
							sql += "or state = ?)";
						} else {
							sql += "or state = ? ";
						}
					}
				}
			}
			
			sql += (" order by num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					if(state != null) {
						for(int i = 0; i < state.length; i++) {
							pstmt.setString(i+5, state[i]);
							if(i == state.length - 1) {
								pstmt.setInt(i+6, start);
								pstmt.setInt(i+7, end);
							}
						}
					} else {
						pstmt.setInt(5, start);
						pstmt.setInt(6, end);
					}
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					if(state != null) {
						System.out.println(state);
						for(int i = 0; i < state.length; i++) {
							pstmt.setString(i+4, state[i]);
							if(i == state.length - 1) {
								pstmt.setInt(i+5, start);
								pstmt.setInt(i+6, end);
							}
						}
					} else {
						pstmt.setInt(4, start);
						pstmt.setInt(5, end);
					}
				}
			} else {
				pstmt.setInt(1, start);
				pstmt.setInt(2, end);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					FAQVO vo = new FAQVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("id"));
					vo.setState(rs.getString("state"));
					vo.setSubject(rs.getString("subject"));
					vo.setContent(rs.getString("content"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setFwnum(rs.getInt("fwnum"));
					vo.setNextnum(rs.getInt("nextnum"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
					
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// FAQ 삭제
	@Override
	public int deleteFAQBoard(String[] checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_FAQ_tbl where num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "select * from mvc_FAQ_tbl where num = ?";
					pstmt = conn.prepareStatement(sql); 
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					rs = pstmt.executeQuery();
					if(rs.next()) {
						if(rs.getInt("fwnum") != 0) {
							sql = "update mvc_FAQ_tbl set nextnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("nextnum"));
							pstmt.setInt(2, rs.getInt("fwnum"));
							
							pstmt.executeUpdate();
						}
						if(rs.getInt("nextnum") != 0) {
							sql = "update mvc_FAQ_tbl set fwnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("fwnum"));
							pstmt.setInt(2, rs.getInt("nextnum"));
							
							pstmt.executeUpdate();
						}
						
					}
					pstmt.close();
					sql = "delete mvc_FAQ_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					deleteCnt = pstmt.executeUpdate();
					pstmt.close();
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	
	// QnA 검색 리스트 조회
	@Override
	public List getQnAList(int start, int end, Date firstday, Date lastday, String searchText, int searchType,
			String[] state) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<QnAVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
					"(select num, id, state, pwd, subject, content, reg_date, file1, ref, ref_step, ref_level, ip, fwnum, nextnum, texttype, writestate, rownum rNum " + 
					"from(SELECT * FROM mvc_QnA_tbl ";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += ("where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%') ");
				} else if(searchType == 1) {
					sql += ("where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%' ");
				} else {
					sql += ("where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%' ");
				}
			}
			sql += ("order by ref DESC, ref_step asc, num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					pstmt.setInt(5, start);
					pstmt.setInt(6, end);
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setInt(4, start);
					pstmt.setInt(5, end);
				}
			} else {
				pstmt.setInt(1, start);
				pstmt.setInt(2, end);
			}
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					QnAVO vo = new QnAVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("id"));
					vo.setState(rs.getString("state"));
					vo.setPwd(rs.getString("pwd"));
					vo.setSubject(rs.getString("subject"));
					vo.setContent(rs.getString("content"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setFile1(rs.getString("file1"));
					vo.setRef(rs.getInt("ref"));
					vo.setRef_step(rs.getInt("ref_step"));
					vo.setRef_level(rs.getInt("ref_level"));
					vo.setIp(rs.getString("ip"));
					vo.setFwnum(rs.getInt("fwnum"));
					vo.setNextnum(rs.getInt("nextnum"));
					vo.setTextType(rs.getString("textType"));
					vo.setWritestate(rs.getString("writestate"));
					
					System.out.println(vo.getNum());
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	// QnA 검색 갯수
	@Override
	public int getSelectQnACnt(Date firstday, Date lastday, String searchText, int searchType, String[] state
			, String strid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_QnA_tbl";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += " where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%')";
				} else if(searchType == 1) {
					sql += " where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%'";
				} else {
					sql += " where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%'";
				}
			}
			if(strid != null) {
				if(firstday != null && lastday != null) {
					sql += " and id = ?";
				} else {
					sql += " where id = ?";
				}
			}
			pstmt = conn.prepareStatement(sql);
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					if(strid != null) {
						pstmt.setString(5, strid);
					}
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					if(strid != null) {
						pstmt.setString(4, strid);
					}
				}
			} else {
				if(strid != null) {
					pstmt.setString(1, strid);
				}
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// QnA 삭제 처리
	@Override
	public int deleteQnABoard(String[] checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_QnA_tbl where num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "select * from mvc_QnA_tbl where num = ?";
					pstmt = conn.prepareStatement(sql); 
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					rs = pstmt.executeQuery();
					if(rs.next()) {
						pstmt.close();
						if(rs.getInt("fwnum") > 0) {
							sql = "update mvc_QnA_tbl set nextnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("nextnum"));
							pstmt.setInt(2, rs.getInt("fwnum"));
							
							pstmt.executeUpdate();
						}
						if(rs.getInt("nextnum") > 0) {
							sql = "update mvc_QnA_tbl set fwnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("fwnum"));
							pstmt.setInt(2, rs.getInt("nextnum"));
							
							pstmt.executeUpdate();
						}
						
						int ref = rs.getInt("ref");
						int ref_step = rs.getInt("ref_step");
						int ref_level = rs.getInt("ref_level");
						
						// 답글이 존재하는지 여부
						sql = "select * from mvc_board_tbl where ref = ? and ref_step = ?+1 and ref_level > ?";
						pstmt.close();
						
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, ref);
						pstmt.setInt(2, ref_step);
						pstmt.setInt(3, ref_level);
						
						rs.close();
						rs = pstmt.executeQuery();
						// 답글이 존재하는 경우
						if(rs.next()) {
							sql = "SELECT min(ref_step) as mi FROM mvc_QnA_tbl where ref_level <= ? " + 
									"and ref_step > ? and ref = ?";
							pstmt.close();
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, ref_level);
							pstmt.setInt(2, ref_step);
							pstmt.setInt(3, ref);
							/*
							 * sql = "delete mvc_board_tbl where num=? or (ref = ? and ref_level > ?)";
							 * pstmt = conn.prepareStatement(sql); pstmt.setInt(1, num); pstmt.setInt(2,
							 * ref); pstmt.setInt(3, ref_level);
							 */
							rs.close();
							rs = pstmt.executeQuery();
							if(rs.next()) {
								System.out.println("답글이 존재하는 경우");
								if(rs.getInt("mi") > 0) {
									sql = "delete mvc_QnA_tbl where (num=? or (ref = ? and ref_level > ?)) and ref_step < (SELECT\r\n" + 
											"    min(ref_step)\r\n" + 
											"FROM mvc_QnA_tbl\r\n" + 
											"where ref_level <= ?\r\n" + 
											"and ref_step > ?\r\n" + 
											"and ref = ?)\r\n" + 
											"and ref_step >= ?";
									pstmt.close();
									pstmt = conn.prepareStatement(sql);
									pstmt.setInt(1, Integer.parseInt(checked[i]));
									pstmt.setInt(2, ref);
									pstmt.setInt(3, ref_level);
									pstmt.setInt(4, ref_level);
									pstmt.setInt(5, ref_step);
									pstmt.setInt(6, ref);
									pstmt.setInt(7, ref_step);
									
									deleteCnt = pstmt.executeUpdate();
								} else {
									sql = "delete mvc_QnA_tbl where num=? or (ref = ? and ref_step > ?)";
									pstmt.close();
									pstmt = conn.prepareStatement(sql);
									pstmt.setInt(1, Integer.parseInt(checked[i])); 
									pstmt.setInt(2, ref); 
									pstmt.setInt(3, ref_step);

									deleteCnt = pstmt.executeUpdate();
								}
							}
						} else {
							// 답글이 존재하지 않은 경우
							sql = "delete mvc_QnA_tbl where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, Integer.parseInt(checked[i]));
							
							deleteCnt = pstmt.executeUpdate();
							System.out.println("답글이 존재하지 않은 경우");
						}
					}
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// review 검색 갯수
	@Override
	public int getSelectreviewCnt(Date firstday, Date lastday, String searchText, int searchType, String strid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_review_tbl";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += " where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%')";
				} else if(searchType == 1) {
					sql += " where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%'";
				} else {
					sql += " where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%'";
				}
			}
			if(strid != null) {
				if(firstday != null && lastday != null) {
					sql += " and id = ?";
				} else {
					sql += " where id = ?";
				}
			}
			pstmt = conn.prepareStatement(sql);
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					if(strid != null) {
						pstmt.setString(5, strid);
					}
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					if(strid != null) {
						pstmt.setString(4, strid);
					}
				}
			} else {
				if(strid != null) {
					pstmt.setString(1, strid);
				}
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// review 검색 리스트 조회
	@Override
	public List getreviewList(int start, int end, Date firstday, Date lastday, String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<reviewVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, id, subject, content, reg_date, file1, readcnt, ip, fwnum, nextnum, rownum rNum " + 
					"from(SELECT * FROM mvc_review_tbl ";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += ("where reg_date between ? and ?+1 "
							+ "and (subject like '%' || ? || '%' or content like '%' || ? || '%') ");
				} else if(searchType == 1) {
					sql += ("where reg_date between ? and ?+1 "
							+ "and subject like '%' || ? || '%' ");
				} else {
					sql += ("where reg_date between ? and ?+1 "
							+ "and content like '%' || ? || '%' ");
				}
			}
			sql += ("order by num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setString(4, searchText);
					pstmt.setInt(5, start);
					pstmt.setInt(6, end);
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, searchText);
					pstmt.setInt(4, start);
					pstmt.setInt(5, end);
				}
			} else {
				pstmt.setInt(1, start);
				pstmt.setInt(2, end);
			}
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					reviewVO vo = new reviewVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("id"));
					vo.setSubject(rs.getString("subject"));
					vo.setContent(rs.getString("content"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setFile1(rs.getString("file1"));
					vo.setReadcnt(rs.getInt("readcnt"));
					vo.setIp(rs.getString("ip"));
					vo.setFwnum(rs.getInt("fwnum"));
					vo.setNextnum(rs.getInt("nextnum"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	// reply 검색 갯수
	@Override
	public int getSelectreplyCnt(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_reply_tbl where ref = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}
	
	// reply 검색 리스트 조회
	@Override
	public List getreplyList(int start, int end, int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<replyVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, id, content, reg_date, ip, ref, rownum rNum " + 
					"from(SELECT * FROM mvc_reply_tbl where ref = ? ";
			sql += ("order by num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					replyVO vo = new replyVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("id"));
					vo.setContent(rs.getString("content"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setIp(rs.getString("ip"));
					vo.setRef(rs.getInt("ref"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// reply 삭제
	@Override
	public int deletereplyBoard(String[] checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_reply_tbl where num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "delete mvc_reply_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					deleteCnt = pstmt.executeUpdate();
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// review 삭제
	@Override
	public int deletereviewBoard(String[] checked) {
		int r_deleteCnt = 0;
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_review_tbl where num=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "select * from mvc_review_tbl where num = ?";
					pstmt = conn.prepareStatement(sql); 
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					rs = pstmt.executeQuery();
					if(rs.next()) {
						if(rs.getInt("fwnum") != 0) {
							sql = "update mvc_review_tbl set nextnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("nextnum"));
							pstmt.setInt(2, rs.getInt("fwnum"));
							
							pstmt.executeUpdate();
						}
						if(rs.getInt("nextnum") != 0) {
							sql = "update mvc_review_tbl set fwnum = ? where num = ?";
							pstmt = conn.prepareStatement(sql);
							pstmt.setInt(1, rs.getInt("fwnum"));
							pstmt.setInt(2, rs.getInt("nextnum"));
							
							pstmt.executeUpdate();
						}
						
					}
					sql = "delete mvc_reply_tbl where ref = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					r_deleteCnt = pstmt.executeUpdate();
					
					pstmt.close();
					sql = "delete mvc_review_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					deleteCnt = pstmt.executeUpdate();
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}


	// brand 검색 갯수
	@Override
	public int getSelectbrandCnt(String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_brand_tbl";
			if(searchType == 0) {
				sql += " where name like '%' || ? || '%'";
			}
			pstmt = conn.prepareStatement(sql);
			if(searchType == 0) {
				pstmt.setString(1, searchText);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// brand 갯수
	@Override
	public int getbrandCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_brand_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// brand 검색 리스트
	@Override
	public List getbrandList(int start, int end, String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BrandVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, name, reg_date, hp, rownum rNum " + 
					"from(SELECT * FROM mvc_brand_tbl ";
			if(searchType == 0) {
				sql += "where name like '%' || ? || '%' ";
			}
			sql += ("order by num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			if(searchType == 0) {
				pstmt.setString(1, searchText);
				pstmt.setInt(2, start);
				pstmt.setInt(3, end);
			}
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					BrandVO vo = new BrandVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setName(rs.getString("name"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setHp(rs.getString("hp"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// brand Max Num 가져오기
	@Override
	public int getbrandMaxNum() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select max(num) as maxNum from mvc_brand_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("maxNum") + 1; // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			} else {
				selectCnt = 1;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 브랜드 등록 처리
	@Override
	public int insertBrand(BrandVO vo) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mvc_brand_tbl(num, name, reg_date, hp) " + 
					"values (brand_seq.nextval, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getName());
			pstmt.setTimestamp(2, vo.getReg_date());
			pstmt.setString(3, vo.getHp());
			
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}

	// brand 삭제
	@Override
	public int deletebrand(String[] checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_brand_tbl where num = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "delete mvc_brand_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					deleteCnt = pstmt.executeUpdate();
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// brand 1개 상세 가져오기
	@Override
	public BrandVO getbrand(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BrandVO vo = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "select * from mvc_brand_tbl where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);;
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				vo = new BrandVO();
				vo.setNum(rs.getInt("num"));
				vo.setName(rs.getString("name"));
				vo.setReg_date(rs.getTimestamp("reg_date"));
				vo.setHp(rs.getString("hp"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}


	// brand 수정 처리
	@Override
	public int updateBrand(BrandVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_brand_tbl set name = ?, hp = ? where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getHp());
			pstmt.setInt(3, vo.getNum());
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		
		return updateCnt;
	}

	// 브랜드 리스트 모두 조회	
	@Override
	public List getbrandallList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BrandVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from mvc_brand_tbl";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<BrandVO>();
				
				do {
					// 3. 작은 바구니 생성
					BrandVO vo = new BrandVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setName(rs.getString("name"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setHp(rs.getString("hp"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// product 검색 갯수
	@Override
	public int getSelectproductCnt(String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_cloths_tbl";
			if(searchType == 0) {
				sql += " where name like '%' || ? || '%'";
			}
			pstmt = conn.prepareStatement(sql);
			if(searchType == 0) {
				pstmt.setString(1, searchText);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// product 갯수
	@Override
	public int getproductCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_cloths_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 상품 리스트 조회
	@Override
	public List getproductList(int start, int end, String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<clothVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, name, content, mediumcode, tex, brandnum, icon, plus, "
				+ "saleprice, buyprice, deliday, deliprice, mainfile, file1, file2, "
				+ "file3, file4, file5, withprdnum, reg_date"
				+ ", bname, mname, brandname, rownum rNum " + 
					"from (SELECT c.*, b.bigname bname, m.mediumname mname, brand.name brandname" + 
					" FROM mvc_cloths_tbl c, bigpart b, mediumpart m, mvc_brand_tbl brand" + 
					" where c.mediumcode = m.mediumcode" + 
					" and m.bigcode = b.bigcode" + 
					" and c.brandnum = brand.num";
			if(searchType == 0) {
				sql += " and c.name like '%' || ? || '%' ";
			}
			sql += ("order by 1 desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			if(searchType == 0) {
				pstmt.setString(1, searchText);
				pstmt.setInt(2, start);
				pstmt.setInt(3, end);
			}
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					clothVO vo = new clothVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setName(rs.getString("name"));
					vo.setContent(rs.getString("content"));
					vo.setMediumcode(rs.getInt("mediumcode"));
					vo.setTex(rs.getString("tex"));
					vo.setBrandnum(rs.getInt("brandnum"));
					vo.setIcon(rs.getString("icon"));
					vo.setPlus(rs.getInt("plus"));
					vo.setSaleprice(rs.getInt("saleprice"));
					vo.setBuyprice(rs.getInt("buyprice"));
					vo.setDeliday(rs.getInt("deliday"));
					vo.setDeliprice(rs.getInt("deliprice"));
					vo.setMainfile(rs.getString("mainfile"));
					vo.setFile1(rs.getString("file1"));
					vo.setFile1(rs.getString("file2"));
					vo.setFile1(rs.getString("file3"));
					vo.setFile1(rs.getString("file4"));
					vo.setFile1(rs.getString("file5"));
					vo.setWithprdnum(rs.getInt("withprdnum"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setBigpartname(rs.getString("bname"));
					vo.setMediumpartname(rs.getString("mname"));
					vo.setBrandname(rs.getString("brandname"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 대분류 검색 갯수
	@Override
	public int getSelectbigpartCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from bigpart";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}




	// 대분류 리스트 모두 조회
	@Override
	public List getbigpartallList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<bigpartVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from bigpart";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<bigpartVO>();
				
				do {
					// 3. 작은 바구니 생성
					bigpartVO vo = new bigpartVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setBigcode(rs.getInt("bigcode"));
					vo.setBigname(rs.getString("bigname"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 중분류 검색 갯수
	@Override
	public int getSelectmediumpartCnt(String parameter) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mediumpart";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 중분류 리스트 모두 조회	
	@Override
	public List getmediumallList(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<mediumpartVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from mediumpart where bigcode = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<mediumpartVO>();
				
				do {
					// 3. 작은 바구니 생성
					mediumpartVO vo = new mediumpartVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setMediumcode(rs.getInt("mediumcode"));
					vo.setMediumname(rs.getString("mediumname"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// color 개수
	@Override
	public int getcolorCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from color_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}


	// color 목록 조회
	@Override
	public List getcolorList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<colorVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from color_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<colorVO>();
				
				do {
					// 3. 작은 바구니 생성
					colorVO vo = new colorVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setColorcode(rs.getInt("colorcode"));
					vo.setColorname(rs.getString("colorname"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// size 개수	
	@Override
	public int getsizeCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from size_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// size 목록 조회
	@Override
	public List getsizeList() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<sizeVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from size_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<sizeVO>();
				
				do {
					// 3. 작은 바구니 생성
					sizeVO vo = new sizeVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setSizecode(rs.getInt("sizecode"));
					vo.setSizename(rs.getString("sizename"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 대분류 등록
	@Override
	public int insertBigpart(String name) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into bigpart(bigcode, bigname) " + 
					"values (bigpart_seq.nextval, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}


	// 중분류 등록
	@Override
	public int insertmediumpart(String name, int bcode) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mediumpart(mediumcode, mediumname, bigcode) " + 
					"values (mediumpart_seq.nextval, ?,?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setInt(2, bcode);
			
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}


	// 컬러등록
	@Override
	public int insertcolorpart(String name) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into color_tbl(colorcode, colorname) " + 
					"values (color_seq.nextval, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}


	// 사이즈 등록
	@Override
	public int insertsizepart(String name) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into size_tbl(sizecode, sizename) " + 
					"values (size_seq.nextval, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}


	// 대분류 삭제
	@Override
	public int deletebigpart(int num) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "delete bigpart " + 
					"where bigcode = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return deleteCnt;
	}


	// 중분류 삭제
	@Override
	public int deletemediumpart(int num) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "delete mediumpart " + 
					"where mediumcode = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return deleteCnt;
	}


	// 컬러 삭제
	@Override
	public int deletecolorpart(int num) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "delete color_tbl " + 
					"where colorcode = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return deleteCnt;
	}


	// 사이즈 삭제
	@Override
	public int deletesizepart(int num) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "delete size_tbl " + 
					"where sizecode = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return deleteCnt;
	}


	// product 등록 처리
	@Override
	public int insertproduct(clothVO vo) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mvc_cloths_tbl(num, name, content, mediumcode, tex, brandnum, icon, plus," + 
					" saleprice, buyprice, deliday, deliprice, mainfile, file1, file2," + 
					" file3, file4, file5, reg_date, withprdnum) " + 
					"values (cloths_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
					+ ", ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getContent());
			pstmt.setInt(3, vo.getMediumcode());;
			pstmt.setString(4, vo.getTex());
			pstmt.setInt(5, vo.getBrandnum());
			pstmt.setString(6, vo.getIcon());
			pstmt.setInt(7, vo.getPlus());
			pstmt.setInt(8, vo.getSaleprice());
			pstmt.setInt(9, vo.getBuyprice());
			pstmt.setInt(10, vo.getDeliday());
			pstmt.setInt(11, vo.getDeliprice());
			pstmt.setString(12, vo.getMainfile());
			pstmt.setString(13, vo.getFile1());
			pstmt.setString(14, vo.getFile2());
			pstmt.setString(15, vo.getFile3());
			pstmt.setString(16, vo.getFile4());
			pstmt.setString(17, vo.getFile5());
			pstmt.setTimestamp(18, vo.getReg_date());
			pstmt.setInt(19, vo.getWithprdnum());
			
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}

	// product 수정 폼
	@Override
	public clothVO getproduct(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		clothVO vo = new clothVO();
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(SELECT c.*, b.bigname bname, m.mediumname mname, brand.name brandname" + 
					" FROM mvc_cloths_tbl c, bigpart b, mediumpart m, mvc_brand_tbl brand" + 
					" where c.mediumcode = m.mediumcode" + 
					" and m.bigcode = b.bigcode" + 
					" and c.brandnum = brand.num" +
					" and c.num = ?" +
					")";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);;
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
				vo.setNum(rs.getInt("num"));
				vo.setName(rs.getString("name"));
				vo.setContent(rs.getString("content"));
				vo.setMediumcode(rs.getInt("mediumcode"));
				vo.setTex(rs.getString("tex"));
				vo.setBrandnum(rs.getInt("brandnum"));
				vo.setIcon(rs.getString("icon"));
				vo.setPlus(rs.getInt("plus"));
				vo.setSaleprice(rs.getInt("saleprice"));
				vo.setBuyprice(rs.getInt("buyprice"));
				vo.setDeliday(rs.getInt("deliday"));
				vo.setDeliprice(rs.getInt("deliprice"));
				vo.setMainfile(rs.getString("mainfile"));
				vo.setFile1(rs.getString("file1"));
				vo.setFile2(rs.getString("file2"));
				vo.setFile3(rs.getString("file3"));
				vo.setFile4(rs.getString("file4"));
				vo.setFile5(rs.getString("file5"));
				vo.setWithprdnum(rs.getInt("withprdnum"));
				vo.setReg_date(rs.getTimestamp("reg_date"));
				vo.setBigpartname(rs.getString("bname"));
				vo.setMediumpartname(rs.getString("mname"));
				vo.setBrandname(rs.getString("brandname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	// 상품 수정처리
	@Override
	public int updateproduct(clothVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_cloths_tbl"
					+ " set name = ?, content = ?, mediumcode = ?, brandnum = ?, icon = ?, plus = ?," + 
					" saleprice = ?, buyprice = ?, deliday = ?, deliprice = ? " + 
					"where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getName());
			pstmt.setString(2, vo.getContent());
			pstmt.setInt(3, vo.getMediumcode());
			pstmt.setInt(4, vo.getBrandnum());
			pstmt.setString(5, vo.getIcon());
			pstmt.setInt(6, vo.getPlus());
			pstmt.setInt(7, vo.getSaleprice());
			pstmt.setInt(8, vo.getBuyprice());
			pstmt.setInt(9, vo.getDeliday());
			pstmt.setInt(10, vo.getDeliprice());
			pstmt.setInt(11, vo.getNum());
			
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}


	// 상품 메인처리
	@Override
	public int updatemainfileproduct(clothVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_cloths_tbl"
					+ " set mainfile = ? " + 
					"where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getMainfile());
			pstmt.setInt(2, vo.getNum());
			
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}


	// 상품 파일 처리
	@Override
	public int updatefilesproduct(clothVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_cloths_tbl"
					+ " set file1 = ?, file2 = ?, file3 = ?, file4 = ?, file5 = ? " + 
					"where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getFile1());
			pstmt.setString(2, vo.getFile2());
			pstmt.setString(3, vo.getFile3());
			pstmt.setString(4, vo.getFile4());
			pstmt.setString(5, vo.getFile5());
			pstmt.setInt(6, vo.getNum());
			
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}


	// 상품 with 처리
	@Override
	public int updatewithitemsproduct(clothVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_cloths_tbl"
					+ " set withprdnum = ? " + 
					"where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.getWithprdnum());
			pstmt.setInt(2, vo.getNum());
			
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}


	// 상품 삭제
	@Override
	public int deleteproduct(String[] checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			for(int i = 0; i < checked.length; i++) {
				sql = "select * from mvc_cloths_tbl where num = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, Integer.parseInt(checked[i]));
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					deleteCnt++;
				}
				pstmt.close();
			}
			
			if(deleteCnt == checked.length) {
				for(int i = 0; i < checked.length; i++) {
					sql = "delete mvc_cloths_tbl where num = ?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, Integer.parseInt(checked[i]));
					
					deleteCnt = pstmt.executeUpdate();
				}
			} else {
				deleteCnt = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// 분류에 따른 리스트 개수
	@Override
	public int getSelectprdCnt(String name) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "select count(*) as cnt from (select distinct c.* from mvc_cloths_tbl c, mvc_stock_tbl s "
					+ "where c.num = s.prdnum "
					+ "and c.mediumcode in (select m.mediumcode from mediumpart m, bigpart b " + 
					"where m.bigcode = b.bigcode " + 
					"and b.bigname = ?) "
					+ "and s.state = '판매중')";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 분류에 따른 리스트 조회
	@Override
	public List getprdList(int start, int end, String name) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<clothVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, name, icon, saleprice, mainfile, rownum rNum " + 
					"from (SELECT distinct c.num, c.name, c.icon, c.saleprice, c.mainfile" + 
					" FROM mvc_cloths_tbl c, mvc_stock_tbl s"
					+ " where c.num = s.prdnum"
					+ " and s.state = '판매중'" + 
					" and c.mediumcode in (select m.mediumcode from mediumpart m, bigpart b " + 
					"where m.bigcode = b.bigcode " + 
					"and b.bigname = ?)";
			sql += ("order by 1 desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					clothVO vo = new clothVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setName(rs.getString("name"));
					vo.setIcon(rs.getString("icon"));
					vo.setSaleprice(rs.getInt("saleprice"));
					vo.setMainfile(rs.getString("mainfile"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 상품상세 컬러 개수
	@Override
	public int getSelectcolorCnt(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "select count(*) as cnt from mvc_stock_tbl where prdnum = ? and state = '판매중'";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 상품상세 사이즈 개수
	@Override
	public int getSelectsizeCnt(int num, int colorcode) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "select count(*) as cnt from mvc_stock_tbl where prdnum = ? and colorcode = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setInt(2, colorcode);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 상품상세 컬러 목록
	@Override
	public List getSelectcolorList(int num) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<colorVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from color_tbl where colorcode in (select mvc_stock_tbl.colorcode from mvc_stock_tbl where prdnum = ? and state = '판매중')";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<colorVO>();
				
				do {
					// 3. 작은 바구니 생성
					colorVO vo = new colorVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setColorcode(rs.getInt("colorcode"));
					vo.setColorname(rs.getString("colorname"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 상품 상세 사이즈 목록
	@Override
	public List getSelectsizeList(int num, int colorcode) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<sizeVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from size_tbl where sizecode in (select mvc_stock_tbl.sizecode from mvc_stock_tbl where prdnum = ? and colorcode = ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setInt(2, colorcode);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<sizeVO>();
				
				do {
					// 3. 작은 바구니 생성
					sizeVO vo = new sizeVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setSizecode(rs.getInt("sizecode"));
					vo.setSizename(rs.getString("sizename"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 사이즈 컬러 추가 처리
	@Override
	public int insertcs(stockVO vo) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mvc_stock_tbl(num, prdnum, colorcode, sizecode, state, maxcount, count) " + 
					"values (stock_seq.nextval, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.getPrdnum());
			pstmt.setInt(2, vo.getColorcode());
			pstmt.setInt(3, vo.getSizecode());
			pstmt.setString(4, vo.getState());
			pstmt.setInt(5, vo.getMaxcount());
			pstmt.setInt(6, vo.getCount());
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}

	// 사이즈 컬러 목록 불러오기
	@Override
	public stockVO getcs(int num, int parseInt, int parseInt2) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		stockVO vo = new stockVO();
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select s.*, si.sizename sname, c.colorname cname from mvc_stock_tbl s, color_tbl c, size_tbl si " + 
					"where s.colorcode = c.colorcode " + 
					"and s.sizecode = si.sizecode " + 
					"and s.sizecode = ? and s.colorcode = ? and prdnum = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, parseInt2);
			pstmt.setInt(2, parseInt);
			pstmt.setInt(3, num);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
				vo.setColorcode(rs.getInt("colorcode"));
				vo.setSizecode(rs.getInt("sizecode"));
				vo.setColorname(rs.getString("cname"));
				vo.setSizename(rs.getString("sname"));
				vo.setCount(rs.getInt("count"));
				vo.setMaxcount(rs.getInt("maxcount"));
				vo.setState(rs.getString("state"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	// cs 수정 처리 
	@Override
	public int updatecs(stockVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_stock_tbl"
					+ " set count = ?, maxcount = ?, state = ? " + 
					"where num = ?"
					+ "and colorcode = ? and sizecode = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.getCount());
			pstmt.setInt(2, vo.getMaxcount());
			pstmt.setString(3, vo.getState());
			pstmt.setInt(4, vo.getPrdnum());
			pstmt.setInt(5, vo.getColorcode());
			pstmt.setInt(6, vo.getSizecode());
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}

	// order 등록 처리
	@Override
	public int insertorder(orderVO vo) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mvc_order_tbl(num, gid, prdnum, colorcode, sizecode, count, price, "
					+ "reg_date, depositname, bankname, pay_option, state, useplus, realprice) " + 
					"values (order_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getGid());
			pstmt.setInt(2, vo.getPrdnum());
			pstmt.setInt(3, vo.getColorcode());
			pstmt.setInt(4, vo.getSizecode());
			pstmt.setInt(5, vo.getCount());
			pstmt.setInt(6, vo.getPrice());
			pstmt.setTimestamp(7, vo.getReg_date());
			pstmt.setString(8, vo.getDepositname());
			pstmt.setString(9, vo.getBankname());
			pstmt.setString(10, vo.getPay_option());
			pstmt.setString(11, vo.getState());
			pstmt.setInt(12, vo.getUseplus());
			pstmt.setInt(13, vo.getRealprice());
			
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}

	// cs count update
	@Override
	public int updatecs(orderVO vo) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			sql = "update mvc_stock_tbl set count = count - ?" + 
					"where colorcode = ? " + 
					"and sizecode = ? " + 
					"and prdnum = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, vo.getCount());
			pstmt.setInt(2, vo.getColorcode());
			pstmt.setInt(3, vo.getSizecode());
			pstmt.setInt(4, vo.getPrdnum());
			
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}

	// count check
	@Override
	public int countChk(int prdnum, int colorcode, int sizecode, int count) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "select count - ? as cnt from mvc_stock_tbl " + 
					"where colorcode = ? " + 
					"and sizecode = ? " + 
					"and prdnum = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, count);
			pstmt.setInt(2, colorcode);
			pstmt.setInt(3, sizecode);
			pstmt.setInt(4, prdnum);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				updateCnt = rs.getInt("cnt");
				if(rs.getInt("cnt") == 0) {
					sql = "update mvc_stock_tbl "
							+ "set state = '품절'" + 
							"where colorcode = ? " + 
							"and sizecode = ? " + 
							"and prdnum = ?";
					pstmt.close();
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, colorcode);
					pstmt.setInt(2, sizecode);
					pstmt.setInt(3, prdnum);
					
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}

	// order 검색 개수
	@Override
	public int getSelectOrderCnt(String state, Date firstday, Date lastday, String memId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_order_tbl where state != '주문취소' and state != '환불확인'";
			if(firstday != null && lastday != null) {
				if(state == null || state == "") {
					sql += " and reg_date between ? and ?+1";
				} else {
					sql += " and reg_date between ? and ?+1 "
							+ "and state = ?";
				}
				if(memId != null) {
					sql += " and gid = ?";
				}
			} else {
				if(memId != null) {
					sql += " and gid = ?";
				}
			}
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(state == null || state == "") {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					if(memId != null) {
						pstmt.setString(3, memId);
					}
				} else {
					pstmt.setDate(1, firstday);
					pstmt.setDate(2, lastday);
					pstmt.setString(3, state);
					if(memId != null) {
						pstmt.setString(4, memId);
					}
				}
			} else {
				if(memId != null) {
					pstmt.setString(1, memId);
				}
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// order 전체 개수
	@Override
	public int getorderCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_order_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// order 자기꺼 목록 조회
	@Override
	public List getorderList(int start, int end, String state, Date firstday, Date lastday, String memId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<orderVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, gid, prdnum, colorcode, sizecode, count, price, " + 
				"reg_date, bankname, pay_option, state, useplus, realprice, cname, sname, cloname, mfile, cplus, rownum rNum " + 
					"from(SELECT o.*, c.colorname cname, s.sizename sname, clo.name cloname, clo.mainfile mfile, clo.plus cplus "
					+ "FROM mvc_order_tbl o, color_tbl c, size_tbl s, mvc_cloths_tbl clo "
					+ "where o.colorcode = c.colorcode "
					+ "and o.sizecode = s.sizecode "
					+ "and o.prdnum = clo.num";
			if(memId != null) {
				sql += " and o.gid = ?";
			}
			if(firstday != null && lastday != null) {
				if(state == null || state == "") {
					sql += " and o.reg_date between ? and ?+1";
				} else {
					sql += " and o.reg_date between ? and ?+1"
							+ "and o.state = ?";
				}
			}
			sql += (" order by o.num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			
			if(firstday != null && lastday != null) {
				if(state == null || state == "") {
					if(memId != null) {
						pstmt.setString(1, memId);
						pstmt.setDate(2, firstday);
						pstmt.setDate(3, lastday);
						pstmt.setInt(4, start);
						pstmt.setInt(5, end);
					} else {
						pstmt.setDate(1, firstday);
						pstmt.setDate(2, lastday);
						pstmt.setInt(3, start);
						pstmt.setInt(4, end);
					}
				} else {
					if(memId != null) {
						pstmt.setString(1, memId);
						pstmt.setDate(2, firstday);
						pstmt.setDate(3, lastday);
						pstmt.setString(4, state);
						pstmt.setInt(5, start);
						pstmt.setInt(6, end);
					} else {
						pstmt.setDate(1, firstday);
						pstmt.setDate(2, lastday);
						pstmt.setString(3, state);
						pstmt.setInt(4, start);
						pstmt.setInt(5, end);
					}
				}
			} else {
				if(memId != null) {
					pstmt.setString(1, memId);
					pstmt.setInt(2, start);
					pstmt.setInt(3, end);
				} else {
					pstmt.setInt(1, start);
					pstmt.setInt(2, end);
				}
			}
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					orderVO vo = new orderVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setGid(rs.getString("gid"));
					vo.setPrdnum(rs.getInt("prdnum"));
					vo.setColorcode(rs.getInt("colorcode"));
					vo.setSizecode(rs.getInt("sizecode"));
					vo.setCount(rs.getInt("count"));
					vo.setPrice(rs.getInt("price"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setBankname(rs.getString("bankname"));
					vo.setPay_option(rs.getString("pay_option"));
					vo.setState(rs.getString("state"));
					vo.setUseplus(rs.getInt("useplus"));
					vo.setRealprice(rs.getInt("realprice"));
					vo.setColorname(rs.getString("cname"));
					vo.setSizename(rs.getString("sname"));
					vo.setMainfile(rs.getString("mfile"));
					vo.setPrdname(rs.getString("cloname"));
					vo.setPrdplus(rs.getInt("cplus"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// cart 추가
	@Override
	public int insertcart(cartVO vo) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mvc_cart_tbl(num, gid, prdnum, colorcode, sizecode, count, price, reg_date) " + 
					"values (cart_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getGid());
			pstmt.setInt(2, vo.getPrdnum());
			pstmt.setInt(3, vo.getColorcode());
			pstmt.setInt(4, vo.getSizecode());
			pstmt.setInt(5, vo.getCount());
			pstmt.setInt(6, vo.getPrice());
			pstmt.setTimestamp(7, vo.getReg_date());
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}

	// state 변경
	@Override
	public int updatestate(int parseInt, int i, int j) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_order_tbl"
					+ " set state = ? " + 
					"where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			if(parseInt == 1) {
				pstmt.setString(1, "주문취소");
			} else if(parseInt == 2) {
				pstmt.setString(1, "환불신청");
			}
			pstmt.setInt(2, i);
			
			updateCnt = pstmt.executeUpdate();
			if(parseInt == 1) {
					pstmt.close();
					sql = "update mvc_stock_tbl set count = count + ?, state = '판매중' " + 
							"where prdnum = (select prdnum from mvc_order_tbl where num = ?) " + 
							"and colorcode = (select colorcode from mvc_order_tbl where num = ?) " + 
							"and sizecode = (select sizecode from mvc_order_tbl where num = ?)";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, j);
					pstmt.setInt(2, i);
					pstmt.setInt(3, i);
					pstmt.setInt(4, i);
					
					pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}

	// cart 검색 갯수
	@Override
	public int getSelectCartCnt(String string) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_cart_tbl where gid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, string);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}
	
	// cart 갯수
	@Override
	public int getcartCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_cart_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}
	
	// cart 목록 리스트
	@Override
	public List getcartList(int start, int end, String attribute) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<cartVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, gid, prdnum, colorcode, sizecode, count, price, " + 
				"reg_date, cname, sname, cloname, mfile, cprice, gplus, cplus, rownum rNum " + 
					"from(SELECT o.*, c.colorname cname, s.sizename sname, clo.name cloname, "
					+ "clo.mainfile mfile, clo.deliprice cprice, g.plus gplus, clo.plus cplus "
					+ "FROM mvc_cart_tbl o, color_tbl c, size_tbl s, mvc_cloths_tbl clo, mvc_guest_tbl g "
					+ "where o.colorcode = c.colorcode "
					+ "and o.sizecode = s.sizecode "
					+ "and o.prdnum = clo.num "
					+ "and o.gid = g.id "
					+ "and g.id = ?";
			sql += (" order by o.num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, attribute);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					cartVO vo = new cartVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setGid(rs.getString("gid"));
					vo.setPrdnum(rs.getInt("prdnum"));
					vo.setColorcode(rs.getInt("colorcode"));
					vo.setSizecode(rs.getInt("sizecode"));
					vo.setCount(rs.getInt("count"));
					vo.setPrice(rs.getInt("price"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setColorname(rs.getString("cname"));
					vo.setSizename(rs.getString("sname"));
					vo.setMainfile(rs.getString("mfile"));
					vo.setPrdname(rs.getString("cloname"));
					vo.setDelipay(rs.getInt("cprice"));
					vo.setUserplus(rs.getInt("gplus"));
					vo.setPluspay(rs.getInt("cplus"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 메일 보내기
	/*
	 * gmail 이메일 전송 세팅
	 * gmail -> 환경설정 -> 전달 및 pop/IMAP -> IMAP 사용설정 :
	 * 내 계정 -> 로그인 및 보안 -> 연결된 앱 및 사이ㅡ -> 보안 수준이 낮은 앱 허용 : 사용으로 변경
	 */
	@Override
	public void sendmail(String email, String key) {
		final String username="ksw04180@gmail.com";
	    final String password="ksw13226";
	    
        Properties props = new Properties(); 
        props.put("mail.smtp.user", username); 
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "25"); 
        props.put("mail.debug", "true"); 
        props.put("mail.smtp.auth", "true"); 
        props.put("mail.smtp.starttls.enable","true"); 
        props.put("mail.smtp.EnableSSL.enable","true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
        props.setProperty("mail.smtp.socketFactory.fallback", "false");   
        props.setProperty("mail.smtp.port", "465");   
        props.setProperty("mail.smtp.socketFactory.port", "465"); 
    
        Session session = Session.getInstance(props, 
         new javax.mail.Authenticator() { 
        protected PasswordAuthentication getPasswordAuthentication() { 
        return new PasswordAuthentication(username, password); 
        }});
        try{
            Message message = new MimeMessage(session); 
           
            message.setFrom(new InternetAddress("admin@eoulim.com"));
            
            message.setRecipients(Message.RecipientType.TO,
            		InternetAddress.parse(email)); 
    	    
    	    String content = "회원가입 인증메일입니다. 링크를 눌러 이메일을 인증하십셔! "
    	    		+ "인증 비밀번호는 " + key +"입니다."; 
    	    
            message.setSubject("이메일 인증메일"); 
            message.setContent(content, "text/html; charset=utf-8");
	         
            System.out.println("send!!!");
            Transport.send(message); 
            System.out.println("SEND");
            
        } catch(Exception e){
            e.printStackTrace();
    	}
	}

	// 장바구니 삭제
	@Override
	public int deletecart(int checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			sql = "delete mvc_cart_tbl where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, checked);
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// 장바구니 모두 삭제
	@Override
	public int deleteallcart(String attribute) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			sql = "delete mvc_cart_tbl where gid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, attribute);
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}


	// 적립금 추가
	@Override
	public int updategplus(int parseInt, int prdplus, String memId) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			sql = "update mvc_guest_tbl set plus = plus - ? + ?" + 
					"where id = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, parseInt);
			pstmt.setInt(2, prdplus);
			pstmt.setString(3, memId);
			
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}

	// 적립금 리스트 가져오기
	@Override
	public List getplusList(int start, int end, String attribute) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<orderVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, gid, prdnum, colorcode, sizecode, count, price, " + 
				"reg_date, bankname, pay_option, state, useplus, realprice, cname, sname, cloname, mfile, cplus, rownum rNum " + 
					"from(SELECT o.*, c.colorname cname, s.sizename sname, clo.name cloname, clo.mainfile mfile, clo.plus cplus "
					+ "FROM mvc_order_tbl o, color_tbl c, size_tbl s, mvc_cloths_tbl clo "
					+ "where o.colorcode = c.colorcode "
					+ "and o.sizecode = s.sizecode "
					+ "and o.prdnum = clo.num "
					+ "and o.gid = ? "
					+ "and o.state != '주문취소' "
					+ "and o.state != '환불신청' "
					+ "and o.state != '환불완료'";
			sql += (" order by o.num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, attribute);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					orderVO vo = new orderVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setGid(rs.getString("gid"));
					vo.setPrdnum(rs.getInt("prdnum"));
					vo.setColorcode(rs.getInt("colorcode"));
					vo.setSizecode(rs.getInt("sizecode"));
					vo.setCount(rs.getInt("count"));
					vo.setPrice(rs.getInt("price"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setBankname(rs.getString("bankname"));
					vo.setPay_option(rs.getString("pay_option"));
					vo.setState(rs.getString("state"));
					vo.setUseplus(rs.getInt("useplus"));
					vo.setRealprice(rs.getInt("realprice"));
					vo.setColorname(rs.getString("cname"));
					vo.setSizename(rs.getString("sname"));
					vo.setMainfile(rs.getString("mfile"));
					vo.setPrdname(rs.getString("cloname"));
					vo.setPrdplus(rs.getInt("cplus"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 환불 적립금 가져오기
	@Override
	public int getrefundplus(String attribute) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null; 
		int price = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select useplus FROM mvc_order_tbl where gid = ? "
					+ "and (state = '주문취소' "
					+ "or state = '환불확인')"; // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, attribute);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				do {
					price += rs.getInt("useplus");
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return price;
	}


	// 주문 상태별 갯수 가져오기
	@Override
	public int selectstateCnt(String string, String memId) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_order_tbl where state = ? and gid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, string);
			pstmt.setString(2, memId);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 관리자 전용 주문 상태 변경
	@Override
	public int h_updatestate(int num, String statename, String id, int prdnum) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "update mvc_order_tbl"
					+ " set state = ? " + 
					"where num = ?";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, statename);
			pstmt.setInt(2, num);
			
			updateCnt = pstmt.executeUpdate();
			
			pstmt.close();
			sql = "select plus from mvc_cloths_tbl where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, prdnum);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				if(statename.equals("배송완료")) {
					sql = "update mvc_guest_tbl set plus = plus + ?" + 
							"where id = ?";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("plus"));
					pstmt.setString(2, id);
					
					
					pstmt.executeUpdate();
				} else if(statename.equals("환불확인")) {
					sql = "update mvc_guest_tbl set plus = plus - ?" + 
							"where id = ?";
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, rs.getInt("plus"));
					pstmt.setString(2, id);
					
					
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return updateCnt;
	}
	
	// 방문횟수 증가
	@Override
	public int visit(String strid) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "update mvc_guest_tbl set visitcnt = visitcnt + 1 where id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return updateCnt;
	}


	// 회원 목록 리스트
	@Override
	public List getMemberlist(int start, int end) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MemberVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select * from " + 
					"(select id, pwd, name, address, address1, address2, homephone, plus, hp, "
					+ "email, birth, birthtype, acchost, bank, acc, reg_date, visitcnt, key, auth, hostmemo, rownum rNum " + 
						"from(SELECT * FROM mvc_guest_tbl ";
				sql += ("order by reg_date desc" +  // 최신글부터 select
						")" + 
				")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
				" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, start);
			pstmt.setInt(2, end);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					MemberVO vo = new MemberVO();
					vo.setId(rs.getString("id"));
					vo.setPwd(rs.getString("pwd"));
					vo.setName(rs.getString("name"));
					vo.setAddress(rs.getString("address"));
					vo.setAddress1(rs.getString("address1"));
					vo.setAddress2(rs.getString("address2"));
					vo.setHomephone(rs.getString("homephone"));
					vo.setHp(rs.getString("hp"));
					/*
					 * String email = req.getParameter("email1") + "@" + req.getParameter("email2");
					 * vo.setEmail(email);
					 */
					vo.setEmail(rs.getString("email"));
					vo.setBirth(rs.getDate("birth"));
					vo.setBirthtype(rs.getString("birthtype"));
					vo.setAcchost(rs.getString("acchost"));
					vo.setBank(rs.getString("bank"));
					vo.setAcc(rs.getString("acc"));
					// reg_date
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setPlus(rs.getInt("plus"));
					vo.setVisitcnt(rs.getInt("visitcnt"));
					vo.setAuth(rs.getInt("auth"));
					vo.setKey(rs.getString("key"));
					vo.setHostmemo(rs.getString("hostmemo"));
					System.out.println(vo.getReg_date());
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 회원갯수
	@Override
	public int getmemberCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_guest_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 회원검색 갯수
	@Override
	public int getSelectmemberCnt(String searchText, int searchType, Date firstday, Date lastday, String month,
			int pluspay) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_guest_tbl";
			sql += " where plus >= ?";
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					sql += " and reg_date between ? and ?+1 "
							+ "and (name like '%' || ? || '%' or id like '%' || ? || '%')";
				} else if(searchType == 1) {
					sql += " and reg_date between ? and ?+1 "
							+ "and name like '%' || ? || '%'";
				} else {
					sql += " and reg_date between ? and ?+1 "
							+ "and name like '%' || ? || '%'";
				}
			}
			pstmt = conn.prepareStatement(sql);
			if(firstday != null && lastday != null) {
				if(searchType == 0) {
					pstmt.setInt(1, pluspay);
					pstmt.setDate(2, firstday);
					pstmt.setDate(3, lastday);
					pstmt.setString(4, searchText);
					pstmt.setString(5, searchText);
				} else {
					pstmt.setInt(1, pluspay);
					pstmt.setDate(2, firstday);
					pstmt.setDate(3, lastday);
					pstmt.setString(4, searchText);
				}
			} else {
				pstmt.setInt(1, pluspay);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 회원 관리자 메모 수정
	@Override
	public int updatehostmemoMember(String hostmemo, String strid) {
		int updateCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "update mvc_guest_tbl set hostmemo = ? where id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, hostmemo);
			pstmt.setString(2, strid);
			
			updateCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return updateCnt;
	}


	// 방문자 토탈
	@Override
	public int[] getclicktotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[12]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select count(*) as cnt from mvc_clicktotal_tbl where TO_CHAR(reg_date, 'MM') = ? "
						+ "and TO_CHAR(reg_date, 'YYYY') = ?";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 신규 멤버 토탈
	@Override
	public int[] getnewmembertotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[12]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select count(*) as cnt from mvc_guest_tbl where TO_CHAR(reg_date, 'MM') = ? "
						+ "and TO_CHAR(reg_date, 'YYYY') = ?";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 주문 자 토탈
	@Override
	public int[] getordermembertotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[13]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select count(*) as cnt from (select DISTINCT gid from mvc_order_tbl"
						+ " where TO_CHAR(reg_date, 'MM') = ? and TO_CHAR(reg_date, 'YYYY') = ?)";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) from (select DISTINCT gid from mvc_order_tbl)";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[12] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 주문 건수 토탈
	@Override
	public int[] getorderCnttotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[13]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select count(*) as cnt from mvc_order_tbl where TO_CHAR(reg_date, 'MM') = ? "
						+ "and TO_CHAR(reg_date, 'YYYY') = ?";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_order_tbl";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[12] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 주문 금액 토탈
	@Override
	public int[] getorderpricetotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[13]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select sum(price) as cnt from mvc_order_tbl where TO_CHAR(reg_date, 'MM') = ? "
						+ "and TO_CHAR(reg_date, 'YYYY') = ?";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select sum(price) as cnt from mvc_order_tbl";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[12] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 주문 환불 토탈
	@Override
	public int[] getorderrefundtotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[13]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select sum(price) as cnt from mvc_order_tbl where TO_CHAR(reg_date, 'MM') = ? "
						+ "and TO_CHAR(reg_date, 'YYYY') = ? and state = '환불확인'";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select sum(price) as cnt from mvc_order_tbl where state = '환불확인'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[12] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 주문 실금액 토탈
	@Override
	public int[] getorderrealpricetotal(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[13]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 12; i++) {
				sql = "select sum(realprice) as cnt from mvc_order_tbl where TO_CHAR(reg_date, 'MM') = ? "
						+ "and TO_CHAR(reg_date, 'YYYY') = ?";
				pstmt = conn.prepareStatement(sql);
				if(i < 10) {
					pstmt.setString(1, "0" + String.valueOf(i+1));
				} else {
					pstmt.setString(1, String.valueOf(i+1));
				}
				pstmt.setString(2, year);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select sum(realprice) as cnt from mvc_order_tbl";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[12] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 카테고리 별 순위
	@Override
	public List getCategoryrank(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<CategoryVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select * from(select m.mediumname mname, b.bigname bname, c.cnt, c.price "
					+ "from (SELECT clo.mediumcode as medicode, count(*) as cnt , sum(o.price) as price "
                    + "FROM mvc_order_tbl o, mvc_cloths_tbl clo "
                    + "where o.prdnum = clo.num and o.state != '환불확인' and TO_CHAR(o.reg_date, 'YYYY') = ? "
                    + "group by clo.mediumcode "
                    + "order by cnt desc) c, mediumpart m, bigpart b where c.medicode = m.mediumcode and m.bigcode = b.bigcode and rownum >= ? and rownum <= ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, year);
			pstmt.setInt(2, 1);
			pstmt.setInt(3, 10);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(10);
				
				do {
					// 3. 작은 바구니 생성
					CategoryVO vo = new CategoryVO();
					vo.setBigname(rs.getString("bname"));
					vo.setMediname(rs.getString("mname"));
					vo.setCnttotal(rs.getInt("cnt"));
					vo.setPricetotal(rs.getInt("price"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 판매순위
	@Override
	public List getSalerank(String year) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<SalerankVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select * from(select clo.name prdname, sum(s.count) stock, count(c.cnt) cnt, sum(c.price) price" + 
					" from (SELECT prdnum as prdnum, count(*) as cnt , sum(price) as price" + 
					" FROM mvc_order_tbl" + 
					" where state != '환불확인' and TO_CHAR(reg_date, 'YYYY') = ?" + 
					" group by prdnum" + 
					" order by cnt desc) c, mvc_cloths_tbl clo, mvc_stock_tbl s where c.prdnum = clo.num "
					+ "and c.prdnum = s.prdnum and rownum >= ? and rownum <= ? group by clo.name)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, year);
			pstmt.setInt(2, 1);
			pstmt.setInt(3, 10);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(10);
				
				do {
					// 3. 작은 바구니 생성
					SalerankVO vo = new SalerankVO();
					vo.setPrdname(rs.getString("prdname"));
					vo.setStock(rs.getInt("stock"));
					vo.setCnttotal(rs.getInt("cnt"));
					vo.setPricetotal(rs.getInt("price"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 적립금 순위
	@Override
	public List getPluspay() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<PluspayVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select * from(select g.name gname, g.id gid, c.cnt, c.plus, g.plus gplus " + 
					"from (SELECT gid, count(*) as cnt , sum(useplus) as plus " + 
					"FROM mvc_order_tbl " + 
					"where state != '환불확인' " + 
					"group by gid) c, mvc_guest_tbl g where c.gid = g.id and rownum >= 1 and rownum <= 10)";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(10);
				
				do {
					// 3. 작은 바구니 생성
					PluspayVO vo = new PluspayVO();
					vo.setGid(rs.getString("gid"));
					vo.setGname(rs.getString("gname"));
					vo.setCnt(rs.getInt("cnt"));
					vo.setUseplus(rs.getInt("plus"));
					vo.setMyplus(rs.getInt("gplus"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 방문하면 토탈 올라가기
	@Override
	public void visitplus(String strid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = datasource.getConnection();
			String sql = "insert into mvc_clicktotal_tbl values(clicktotal_seq.nextval, ?, ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			pstmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 이번달 주문 건수
	@Override
	public int getmonthorderCnttotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select count(*) as cnt from mvc_order_tbl "
					+ "where to_char(reg_date, 'YYYY-MM') = to_char(sysdate, 'YYYY-MM')";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 이번달 매출액
	@Override
	public int getmonthorderrealpricetotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select sum(price) as cnt from mvc_order_tbl "
					+ "where to_char(reg_date, 'YYYY-MM') = to_char(sysdate, 'YYYY-MM') and state = '배송완료'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 오늘 통계 리스트
	@Override
	public int getdayorderrealpricetotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select sum(price) as cnt from mvc_order_tbl "
					+ "where to_char(reg_date, 'YYYY-MM-DD') = to_char(sysdate, 'YYYY-MM-DD') and state = '배송완료'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int getdayorderCnttotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select count(*) as cnt from mvc_order_tbl "
					+ "where to_char(reg_date, 'YYYY-MM-DD') = to_char(sysdate, 'YYYY-MM-DD') and state = '배송완료'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int getdayclicktotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select count(*) as cnt from mvc_clicktotal_tbl "
					+ "where to_char(reg_date, 'YYYY-MM-DD') = to_char(sysdate, 'YYYY-MM-DD')";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int selectQnACnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select count(*) as cnt from mvc_QnA_tbl "
					+ "where to_char(reg_date, 'YYYY-MM-DD') = to_char(sysdate, 'YYYY-MM-DD')";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int selectnewmember() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select count(*) as cnt from mvc_review_tbl "
					+ "where to_char(reg_date, 'YYYY-MM-DD') = to_char(sysdate, 'YYYY-MM-DD')";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int getdaycancelCnttotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int list = 0; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			sql = "select count(*) as cnt from mvc_order_tbl "
					+ "where to_char(reg_date, 'YYYY-MM-DD') = to_char(sysdate, 'YYYY-MM-DD') and state = '주문취소'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 7일간 통계
	@Override
	public int[] selectPayendCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select count(*) as cnt from mvc_order_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')"
						+ " and state in ('배송준비중','배송중','배송완료')";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_order_tbl"
					+ " where reg_date between sysdate - 6 and sysdate + 1"
					+ " and state in ('배송준비중','배송중','배송완료')";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int[] selectDeliStartCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select count(*) as cnt from mvc_order_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')"
						+ " and state = '배송준비중'";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_order_tbl"
					+ " where reg_date between sysdate - 6 and sysdate + 1"
					+ " and state = '배송준비중'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int[] selectDeliingCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select count(*) as cnt from mvc_order_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')"
						+ " and state = '배송중'";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_order_tbl"
					+ " where reg_date between sysdate - 6 and sysdate + 1"
					+ " and state = '배송중'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int[] selectDeliEndCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select count(*) as cnt from mvc_order_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')"
						+ " and state = '배송완료'";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_order_tbl"
					+ " where reg_date between sysdate - 6 and sysdate + 1"
					+ " and state = '배송완료'";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int[] selectCancelCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select count(*) as cnt from mvc_order_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')"
						+ " and state in ('주문취소','환불완료')";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_order_tbl"
					+ " where reg_date between sysdate - 6 and sysdate + 1"
					+ " and state in ('주문취소','환불완료')";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	@Override
	public int[] selectReviewCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select count(*) as cnt from mvc_review_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select count(*) as cnt from mvc_review_tbl "
					+ "where reg_date between sysdate - 6 and sysdate + 1";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	@Override
	public int[] getweekpricetotal() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int[] list = new int[8]; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			for(int i = 0; i < 7; i++) {
				sql = "select sum(price) as cnt from mvc_order_tbl "
						+ "where TO_CHAR(reg_date, 'YYYY-MM-DD') = TO_CHAR(sysdate-?,'YYYY-MM-DD')";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, i);
				
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					// 2. 큰바구니 생성
					list[i] = rs.getInt("cnt");
				}
				rs.close();
				pstmt.close();
			}
			sql = "select sum(price) as cnt from mvc_order_tbl"
					+ " where reg_date between sysdate - 6 and sysdate + 1";
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list[7] = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 내 게시물 리스트
	@Override
	public List myboard(int start, int end, String searchText, int searchType, String strid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MyBoardVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from(select * from( " + 
					"select num, id, '문의글' state, subject, 0 readcnt, file1, texttype, reg_date, writestate, fwnum, nextnum from mvc_QnA_tbl where id = ? " + 
					"union all " + 
					"select num, id, '후기글' state, subject, readcnt, file1, null texttype, reg_date, null writestate, fwnum, nextnum from mvc_review_tbl where id = ?) ";
			if(searchType == 0) {
				sql += ("order by reg_date desc) " +
						"where subject like '%' || ? || '%' " +
						"and rownum >= ? and rownum <= ?");
			} else {
				sql += ("order by reg_date desc) " + 
						"where rownum >= ? and rownum <= ?"); 
			}
			pstmt = conn.prepareStatement(sql);
			
			if(searchType == 0) {
				pstmt.setString(1, strid);
				pstmt.setString(2, strid);
				pstmt.setString(3, searchText);
				pstmt.setInt(4, start);
				pstmt.setInt(5, end);
			} else {
				pstmt.setString(1, strid);
				pstmt.setString(2, strid);
				pstmt.setInt(3, start);
				pstmt.setInt(4, end);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					MyBoardVO vo = new MyBoardVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setWriter(rs.getString("id"));
					vo.setState(rs.getString("state"));
					vo.setSubject(rs.getString("subject"));
					vo.setReadCnt(rs.getInt("readcnt"));
					vo.setFile1(rs.getString("file1"));
					vo.setTexttype(rs.getString("texttype"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setWritestate(rs.getString("writestate"));
					vo.setFwnum(rs.getInt("fwnum"));
					vo.setNextnum(rs.getInt("nextnum"));
					
					System.out.println(vo.getNum());
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 게시물 검색 갯수
	@Override
	public int getSelectMyBoardCnt(String searchText, int searchType, String strid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select count(*) as cnt from( " + 
					"select num, id, '문의글' state, subject, 0 readcnt, file1, texttype, reg_date, writestate, fwnum, nextnum from mvc_QnA_tbl where id = ? " + 
					"union all " + 
					"select num, id, '후기글' state, subject, readcnt, file1, null texttype, reg_date, null writestate, fwnum, nextnum from mvc_review_tbl where id = ?)";
			if(searchType == 0) {
				sql += " where subject like '%' || ? || '%'";
			}
			pstmt = conn.prepareStatement(sql);
			
			if(searchType == 0) {
				pstmt.setString(1, strid);
				pstmt.setString(2, strid);
				pstmt.setString(3, searchText);
			} else {
				pstmt.setString(1, strid);
				pstmt.setString(2, strid);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				selectCnt = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 게시물 갯수
	@Override
	public int getMyBoardCnt(String strid) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select count(*) as cnt from( " + 
					"select num, id, '문의글' state, subject, 0 readcnt, file1, texttype, reg_date, writestate, fwnum, nextnum from mvc_QnA_tbl where id = ? " + 
					"union all " + 
					"select num, id, '후기글' state, subject, readcnt, file1, null texttype, reg_date, null writestate, fwnum, nextnum from mvc_review_tbl where id = ?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			pstmt.setString(2, strid);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				selectCnt = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// wish 추가
	@Override
	public int insertwish(wishVO vo) {
		int insertCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 id에 해당하는 데이터가 있고
			sql = "insert into mvc_wishlist_tbl(num, gid, prdnum, colorcode, sizecode, count, price, reg_date) " + 
					"values (wishlist_seq.nextval, ?, ?, ?, ?, ?, ?, ?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, vo.getGid());
			pstmt.setInt(2, vo.getPrdnum());
			pstmt.setInt(3, vo.getColorcode());
			pstmt.setInt(4, vo.getSizecode());
			pstmt.setInt(5, vo.getCount());
			pstmt.setInt(6, vo.getPrice());
			pstmt.setTimestamp(7, vo.getReg_date());
			insertCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				
			}
		}
		return insertCnt;
	}

	// wish 검색 갯수
	@Override
	public int getSelectWishCnt(String string) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_wishlist_tbl where gid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, string);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}
	
	// wish 갯수
	@Override
	public int getWishCnt() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			sql = "select count(*) as cnt from mvc_wish_tbl";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}
	
	// wish 목록 리스트
	@Override
	public List getwishList(int start, int end, String attribute) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<cartVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, gid, prdnum, colorcode, sizecode, count, price, " + 
				"reg_date, cname, sname, cloname, mfile, cprice, gplus, cplus, rownum rNum " + 
					"from(SELECT o.*, c.colorname cname, s.sizename sname, clo.name cloname, "
					+ "clo.mainfile mfile, clo.deliprice cprice, g.plus gplus, clo.plus cplus "
					+ "FROM mvc_wishlist_tbl o, color_tbl c, size_tbl s, mvc_cloths_tbl clo, mvc_guest_tbl g "
					+ "where o.colorcode = c.colorcode "
					+ "and o.sizecode = s.sizecode "
					+ "and o.prdnum = clo.num "
					+ "and o.gid = g.id "
					+ "and g.id = ?";
			sql += (" order by o.num desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, attribute);
			pstmt.setInt(2, start);
			pstmt.setInt(3, end);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					cartVO vo = new cartVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setGid(rs.getString("gid"));
					vo.setPrdnum(rs.getInt("prdnum"));
					vo.setColorcode(rs.getInt("colorcode"));
					vo.setSizecode(rs.getInt("sizecode"));
					vo.setCount(rs.getInt("count"));
					vo.setPrice(rs.getInt("price"));
					vo.setReg_date(rs.getTimestamp("reg_date"));
					vo.setColorname(rs.getString("cname"));
					vo.setSizename(rs.getString("sname"));
					vo.setMainfile(rs.getString("mfile"));
					vo.setPrdname(rs.getString("cloname"));
					vo.setDelipay(rs.getInt("cprice"));
					vo.setUserplus(rs.getInt("gplus"));
					vo.setPluspay(rs.getInt("cplus"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 찜하기 삭제
	@Override
	public int deletewish(int checked) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			sql = "delete mvc_wishlist_tbl where num = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, checked);
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// 찜하기 모두 삭제
	@Override
	public int deleteallwish(String attribute) {
		int deleteCnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		
		try {
			conn = datasource.getConnection();
			sql = "delete mvc_wishlist_tbl where gid = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, attribute);
			
			deleteCnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return deleteCnt;
	}

	// 검색 리스트 개수
	@Override
	public int getSelectSearchCnt(String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int selectCnt = 0;
		
		try {
			conn = datasource.getConnection();
			String sql = "select count(*) as cnt from mvc_cloths_tbl";
			if(searchType == 0) {
				sql += " where name like '%' || ? || '%'";
			}
			pstmt = conn.prepareStatement(sql);
			if(searchType == 0) {
				pstmt.setString(1, searchText);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) { // 존재한다면
				selectCnt = rs.getInt("cnt"); // cnt 대신 컬럼 index에 1이 올 수 있다. -> rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return selectCnt;
	}

	// 검색 리스트 조회
	@Override
	public List getSearchList(int start, int end, String searchText, int searchType) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<clothVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, name, icon, saleprice, mainfile, rownum rNum " + 
					"from (SELECT *" + 
					" FROM mvc_cloths_tbl";
			if(searchType == 0) {
				sql += " where name like '%' || ? || '%'";
			}
			sql += (" order by 1 desc" +  // 최신글부터 select
					")" + 
			")" + // 2. 최신글부터 select한 레코드에 rowNum을 추가한다.(삭제 데이터를 제외한 실제데이터를 최신글부터 넘버링
			" where rNum >= ? and rNum <= ?"); // 3. 넘겨받은 start값과 end값으로 rowNum을 조회
											  // 30건 기준 (실제 6페이지) => 최신 1페이지 1~5
											  // 30건 기준 (실제 5페이지) => 최신 2페이지 6~10
			pstmt = conn.prepareStatement(sql);
			if(searchType == 0) {
				pstmt.setString(1, searchText);
				pstmt.setInt(2, start);
				pstmt.setInt(3, end);
			} else {
				pstmt.setInt(1, start);
				pstmt.setInt(2, end);
			}
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>(end-start+1);
				
				do {
					// 3. 작은 바구니 생성
					clothVO vo = new clothVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setName(rs.getString("name"));
					vo.setIcon(rs.getString("icon"));
					vo.setSaleprice(rs.getInt("saleprice"));
					vo.setMainfile(rs.getString("mainfile"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}


	// 아이디 찾기
	@Override
	public List<MemberVO> findid(String name, String email, int type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<MemberVO> list = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 화면에서 입력받은 id와 일치한 데이터가 있는지 확인
			String sql = "";
			if(type == 0) {
				sql = "select * from mvc_guest_tbl where name = ? and email = ?";
			} else {
				sql = "select * from mvc_host_tbl where name = ? and email = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			
			rs = pstmt.executeQuery();
			// 2. resultSet에 id에 해당하는 1사람의 회원정보가 존재하면
			if(rs.next()) {
				// 2-1. resultSet을 읽어서 바구니에 담는다.
				// vo 바구니를 생성한다.
				list = new ArrayList<MemberVO>();
				do {
					MemberVO vo = new MemberVO();
					vo.setId(rs.getString("id"));
					vo.setPwd(rs.getString("pwd"));
					vo.setName(rs.getString("name"));
					if(type == 0) {
						vo.setReg_date(rs.getTimestamp("reg_date"));
					}
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	// 비밀번호 찾기
	@Override
	public MemberVO findpwd(String strid, String email, int type) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		MemberVO vo = null;
		
		try {
			conn = datasource.getConnection();
			// 로그인한 화면에서 입력받은 id와 일치한 데이터가 있는지 확인
			String sql = "";
			if(type == 0) {
				sql = "select * from mvc_guest_tbl where id = ? and email = ?";
			} else {
				sql = "select * from mvc_host_tbl where id = ? and email = ?";
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, strid);
			pstmt.setString(2, email);
			
			rs = pstmt.executeQuery();
			// 2. resultSet에 id에 해당하는 1사람의 회원정보가 존재하면
			if(rs.next()) {
				// 2-1. resultSet을 읽어서 바구니에 담는다.
				vo = new MemberVO();
				vo.setId(rs.getString("id"));
				vo.setPwd(rs.getString("pwd"));
				vo.setName(rs.getString("name"));
				if(type == 0) {
					vo.setReg_date(rs.getTimestamp("reg_date"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return vo;
	}

	// 검색 리스트 개수

	// 상품 리스트
	@Override
	public List productlist() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<clothVO> list = null; 
		
		try {
			conn = datasource.getConnection();
			String sql = "";
			// notice 일 때
			sql = "select * from " + 
				"(select num, name, icon, saleprice, mainfile, rownum rNum " + 
					"from (SELECT *" + 
					" FROM mvc_cloths_tbl)";
			sql += ("order by 1 desc" +  // 최신글부터 select
					")");
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				// 2. 큰바구니 생성
				list = new ArrayList<>();
				
				do {
					// 3. 작은 바구니 생성
					clothVO vo = new clothVO();
					// 4. 게시글 1건에 읽어서 rs를 작은 바구니에 담아라
					vo.setNum(rs.getInt("num"));
					vo.setName(rs.getString("name"));
					vo.setIcon(rs.getString("icon"));
					vo.setSaleprice(rs.getInt("saleprice"));
					vo.setMainfile(rs.getString("mainfile"));
					// 5. 큰바구니에 작은바구니를 추가한다.
					list.add(vo);
				} while(rs.next());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

}