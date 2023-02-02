package mvc.member.vo;

import java.sql.Timestamp;

public class orderVO {
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
    private String bankname;
    private String pay_option;
    private String colorname;
    private String sizename;
    private String state;
    private int useplus;
    private int realprice;
    private String mainfile;
    private String depositname;
    private String usermessege;
    private int prdplus;

	// 생성자
    public orderVO() {}

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

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getPay_option() {
		return pay_option;
	}

	public void setPay_option(String pay_option) {
		this.pay_option = pay_option;
	}

	public int getUseplus() {
		return useplus;
	}

	public void setUseplus(int useplus) {
		this.useplus = useplus;
	}

	public int getRealprice() {
		return realprice;
	}

	public void setRealprice(int realprice) {
		this.realprice = realprice;
	}

	public String getPrdname() {
		return prdname;
	}

	public void setPrdname(String prdname) {
		this.prdname = prdname;
	}

	public String getMainfile() {
		return mainfile;
	}

	public void setMainfile(String mainfile) {
		this.mainfile = mainfile;
	}

    public String getUsermessege() {
		return usermessege;
	}

	public void setUsermessege(String usermessege) {
		this.usermessege = usermessege;
	}

	public String getDepositname() {
		return depositname;
	}

	public void setDepositname(String depositname) {
		this.depositname = depositname;
	}

	public int getPrdplus() {
		return prdplus;
	}

	public void setPrdplus(int prdplus) {
		this.prdplus = prdplus;
	}

	
    
}
