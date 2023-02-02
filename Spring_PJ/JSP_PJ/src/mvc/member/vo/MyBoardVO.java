package mvc.member.vo;

import java.sql.Timestamp;

public class MyBoardVO {
	// 멤버변수
	private int num;     
    private String writer;
    private String state;
    private String subject;
    private int readCnt; 
    private String file1;
    private String texttype;
    private String writestate;
    private Timestamp reg_date;
    private int fwnum;
    private int nextnum;
    
    // 생성자
    public MyBoardVO() {}

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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getReadCnt() {
		return readCnt;
	}

	public void setReadCnt(int readCnt) {
		this.readCnt = readCnt;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getTexttype() {
		return texttype;
	}

	public void setTexttype(String texttype) {
		this.texttype = texttype;
	}

	public String getWritestate() {
		return writestate;
	}

	public void setWritestate(String writestate) {
		this.writestate = writestate;
	}

	public Timestamp getReg_date() {
		return reg_date;
	}

	public void setReg_date(Timestamp reg_date) {
		this.reg_date = reg_date;
	}

	public int getFwnum() {
		return fwnum;
	}

	public void setFwnum(int fwnum) {
		this.fwnum = fwnum;
	}

	public int getNextnum() {
		return nextnum;
	}

	public void setNextnum(int nextnum) {
		this.nextnum = nextnum;
	}
	
	
    
    
}
