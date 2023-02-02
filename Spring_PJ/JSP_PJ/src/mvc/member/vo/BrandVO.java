package mvc.member.vo;

import java.sql.Timestamp;

public class BrandVO {
	// 멤버변수
	private int num;     
    private String name;
    private Timestamp reg_date;
    private String hp = "";
    
    // 생성자
    public BrandVO() {}

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

	public String getHp() {
		return hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

	
    
}
