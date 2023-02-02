package mvc.member.vo;

import java.sql.Timestamp;

public class cartVO {
	// 멤버변수
	private int num;   
    private String prdname;
	private String gid;
    private int prdnum;
    private int colorcode;
    private int sizecode;
    private int count;
    private int price;
    private Timestamp reg_date;
    private String colorname;
    private String sizename;
    private int delipay;
    private int pluspay;
    private int userplus;
    private String mainfile;
    

	// 생성자
    public cartVO() {}

    // setter/getter
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getMainfile() {
		return mainfile;
	}

	public void setMainfile(String mainfile) {
		this.mainfile = mainfile;
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

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Timestamp getReg_date() {
		return reg_date;
	}

	public void setReg_date(Timestamp reg_date) {
		this.reg_date = reg_date;
	}

	public String getPrdname() {
		return prdname;
	}

	public void setPrdname(String prdname) {
		this.prdname = prdname;
	}

	public int getDelipay() {
		return delipay;
	}

	public void setDelipay(int delipay) {
		this.delipay = delipay;
	}

	public int getPluspay() {
		return pluspay;
	}

	public void setPluspay(int pluspay) {
		this.pluspay = pluspay;
	}

	public int getUserplus() {
		return userplus;
	}

	public void setUserplus(int userplus) {
		this.userplus = userplus;
	}

	
    
}
