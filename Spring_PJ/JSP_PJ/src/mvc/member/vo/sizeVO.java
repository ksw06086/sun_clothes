package mvc.member.vo;


public class sizeVO {
	// 멤버변수
	private int sizecode;     
    private String sizename;
    
    // 생성자
    public sizeVO() {}

    
    // setter/getter
	public int getSizecode() {
		return sizecode;
	}

	public void setSizecode(int sizecode) {
		this.sizecode = sizecode;
	}

	public String getSizename() {
		return sizename;
	}

	public void setSizename(String sizename) {
		this.sizename = sizename;
	}
    
}
