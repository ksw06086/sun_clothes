package mvc.member.vo;

import java.sql.Timestamp;

public class clothVO {
	// 멤버변수
	private int num;     
    private String name;
    private String content;
    private int mediumcode;
    private String tex;
    private int brandnum;
    private String icon;
    private int plus;
    private int saleprice;
    private int buyprice;
    private int deliday;
    private int deliprice;
    private String mainfile = null;
    private String file1 = null;
    private String file2 = null;
    private String file3 = null;
    private String file4 = null;
    private String file5 = null;
    private int withprdnum = 0;
    private Timestamp reg_date;
    private String bigpartname;
    private String mediumpartname;
    private String brandname;
    
    // 생성자
    public clothVO() {}

    // setter/getter
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Timestamp getReg_date() {
		return reg_date;
	}

	public void setReg_date(Timestamp reg_date) {
		this.reg_date = reg_date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getMediumcode() {
		return mediumcode;
	}

	public void setMediumcode(int mediumcode) {
		this.mediumcode = mediumcode;
	}

	public String getTex() {
		return tex;
	}

	public void setTex(String tex) {
		this.tex = tex;
	}

	public int getBrandnum() {
		return brandnum;
	}

	public void setBrandnum(int brandnum) {
		this.brandnum = brandnum;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getPlus() {
		return plus;
	}

	public void setPlus(int plus) {
		this.plus = plus;
	}

	public int getSaleprice() {
		return saleprice;
	}

	public void setSaleprice(int saleprice) {
		this.saleprice = saleprice;
	}

	public int getBuyprice() {
		return buyprice;
	}

	public void setBuyprice(int buyprice) {
		this.buyprice = buyprice;
	}

	public int getDeliday() {
		return deliday;
	}

	public void setDeliday(int deliday) {
		this.deliday = deliday;
	}

	public int getDeliprice() {
		return deliprice;
	}

	public void setDeliprice(int deliprice) {
		this.deliprice = deliprice;
	}

	public String getMainfile() {
		return mainfile;
	}

	public void setMainfile(String mainfile) {
		this.mainfile = mainfile;
	}

	public String getFile1() {
		return file1;
	}

	public void setFile1(String file1) {
		this.file1 = file1;
	}

	public String getFile2() {
		return file2;
	}

	public void setFile2(String file2) {
		this.file2 = file2;
	}

	public String getFile3() {
		return file3;
	}

	public void setFile3(String file3) {
		this.file3 = file3;
	}

	public String getFile4() {
		return file4;
	}

	public void setFile4(String file4) {
		this.file4 = file4;
	}

	public String getFile5() {
		return file5;
	}

	public void setFile5(String file5) {
		this.file5 = file5;
	}

	public int getWithprdnum() {
		return withprdnum;
	}

	public void setWithprdnum(int withprdnum) {
		this.withprdnum = withprdnum;
	}

	public String getBigpartname() {
		return bigpartname;
	}

	public void setBigpartname(String bigpartname) {
		this.bigpartname = bigpartname;
	}

	public String getMediumpartname() {
		return mediumpartname;
	}

	public void setMediumpartname(String mediumpartname) {
		this.mediumpartname = mediumpartname;
	}

	public String getBrandname() {
		return brandname;
	}

	public void setBrandname(String brandname) {
		this.brandname = brandname;
	}

	
    
}
