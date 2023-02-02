package mvc.member.vo;


public class SalerankVO {
	// 멤버변수
	private String prdname;     
    private int stock;
    private int cnttotal;
    private int pricetotal;
    
    // 생성자
    public SalerankVO() {}

    // setter/getter

	public int getCnttotal() {
		return cnttotal;
	}

	public String getPrdname() {
		return prdname;
	}

	public void setPrdname(String prdname) {
		this.prdname = prdname;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
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
