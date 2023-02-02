package mvc.member.vo;

import java.sql.Timestamp;

public class stockVO {
	// 멤버변수
	private int num;     
    private int prdnum;
    private int colorcode;
    private int sizecode;
    private String state;
    private int maxcount;
    private int count;
    private String colorname;
    private String sizename;
    
    // 생성자
    public stockVO() {}

    // setter/getter
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getPrdnum() {
		return prdnum;
	}

	public void setPrdnum(int prdnum) {
		this.prdnum = prdnum;
	}

	public int getColorcode() {
		return colorcode;
	}

	public void setColorcode(int colorcode) {
		this.colorcode = colorcode;
	}

	public int getSizecode() {
		return sizecode;
	}

	public void setSizecode(int sizecode) {
		this.sizecode = sizecode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getMaxcount() {
		return maxcount;
	}

	public void setMaxcount(int maxcount) {
		this.maxcount = maxcount;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getColorname() {
		return colorname;
	}

	public void setColorname(String colorname) {
		this.colorname = colorname;
	}

	public String getSizename() {
		return sizename;
	}

	public void setSizename(String sizename) {
		this.sizename = sizename;
	}

	

	
    
}
