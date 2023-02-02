package mvc.member.vo;

import java.sql.Timestamp;

public class FAQVO {
	// 멤버변수
	private int num;     
    private String writer;
    private String state;
    private String subject;
    private String content;
    private Timestamp reg_date;
    private int nextnum;
    private int fwnum;
    private String nextsubject = null;
    private String fwsubject = null;
   
	// 생성자
    public FAQVO() {}

    // setter/getter
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getReg_date() {
		return reg_date;
	}

	public void setReg_date(Timestamp reg_date) {
		this.reg_date = reg_date;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getNextnum() {
		return nextnum;
	}

	public void setNextnum(int nextnum) {
		this.nextnum = nextnum;
	}

	public int getFwnum() {
		return fwnum;
	}

	public void setFwnum(int fwnum) {
		this.fwnum = fwnum;
	}

	public String getNextsubject() {
		return nextsubject;
	}

	public void setNextsubject(String nextsubject) {
		this.nextsubject = nextsubject;
	}

	public String getFwsubject() {
		return fwsubject;
	}

	public void setFwsubject(String fwsubject) {
		this.fwsubject = fwsubject;
	}
    
	

}
