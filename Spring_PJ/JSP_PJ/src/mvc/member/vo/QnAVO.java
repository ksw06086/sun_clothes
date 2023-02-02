package mvc.member.vo;

import java.sql.Timestamp;

public class QnAVO {
	// 멤버변수
	private int num;     
    private String writer;
    private String state;
    private String pwd;
    private String subject;
    private String content;
    private Timestamp reg_date;
    private String file1;
    private int ref;
    private int ref_step;
    private int ref_level;
    private String ip;
    private int nextnum;
    private int fwnum;
    private String nextsubject = null;
    private String fwsubject = null;
    private String nexttextType = null;
    private String fwtextType = null;
    private String writestate;
    private String textType;
   
	// 생성자
    public QnAVO() {}

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
    
	public String getFile1() {
		return file1;
	}
	
	public void setFile1(String file1) {
		this.file1 = file1;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public int getRef() {
		return ref;
	}

	public void setRef(int ref) {
		this.ref = ref;
	}

	public int getRef_step() {
		return ref_step;
	}

	public void setRef_step(int ref_step) {
		this.ref_step = ref_step;
	}

	public int getRef_level() {
		return ref_level;
	}

	public void setRef_level(int ref_level) {
		this.ref_level = ref_level;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTextType() {
		return textType;
	}

	public void setTextType(String textType) {
		this.textType = textType;
	}

	public String getNexttextType() {
		return nexttextType;
	}

	public void setNexttextType(String nexttextType) {
		this.nexttextType = nexttextType;
	}

	public String getFwtextType() {
		return fwtextType;
	}

	public void setFwtextType(String fwtextType) {
		this.fwtextType = fwtextType;
	}

	public String getWritestate() {
		return writestate;
	}

	public void setWritestate(String writestate) {
		this.writestate = writestate;
	}

	
	
}
