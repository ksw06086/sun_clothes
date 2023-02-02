package mvc.member.vo;


public class CategoryVO {
	// 멤버변수
	private String mediname;     
    private String bigname;
    private int cnttotal;
    private int pricetotal;
    
    // 생성자
    public CategoryVO() {}

    // setter/getter
	public String getMediname() {
		return mediname;
	}

	public void setMediname(String mediname) {
		this.mediname = mediname;
	}

	public String getBigname() {
		return bigname;
	}

	public void setBigname(String bigname) {
		this.bigname = bigname;
	}

	public int getCnttotal() {
		return cnttotal;
	}

	public void setCnttotal(int cnttotal) {
		this.cnttotal = cnttotal;
	}

	public int getPricetotal() {
		return pricetotal;
	}

	public void setPricetotal(int pricetotal) {
		this.pricetotal = pricetotal;
	}
    
    
    
}
