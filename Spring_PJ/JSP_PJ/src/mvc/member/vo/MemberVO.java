package mvc.member.vo;

import java.sql.Timestamp;
import java.util.Random;
import java.sql.Date;

public class MemberVO {
	
	// 멤버변수
	private String id = null;
	private String pwd = null;
	private String name = null;
	private String address = null;
	private String address1 = null;
	private String address2 = null;
	private String homephone = null;
	private String hp = null;
	private String email = null;
	private Date birth = null;
	private String birthtype = null;
	private String acchost = null;
	private String bank = null;
	private String acc = null;
	private Timestamp reg_date = null;
	private int plus = 0;
	private int visitcnt = 0;
	private int auth;
	private String key;
	private String hostmemo;
	
	// 생성자
	public MemberVO() {}

	
	// setter/getter
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	public String getHomephone() {
		return homephone;
	}

	public void setHomephone(String homephone) {
		this.homephone = homephone;
	}

	public String getHp() {
		return hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth2) {
		this.birth = birth2;
	}

	public String getBirthtype() {
		return birthtype;
	}

	public void setBirthtype(String birthtype) {
		this.birthtype = birthtype;
	}

	public String getAcchost() {
		return acchost;
	}

	public void setAcchost(String acchost) {
		this.acchost = acchost;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public Timestamp getReg_date() {
		return reg_date;
	}


	public void setReg_date(Timestamp reg_date) {
		this.reg_date = reg_date;
	}


	public int getPlus() {
		return plus;
	}


	public void setPlus(int plus) {
		this.plus = plus;
	}


	public int getAuth() {
		return auth;
	}


	public void setAuth(int auth) {
		this.auth = auth;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public int getVisitcnt() {
		return visitcnt;
	}


	public void setVisitcnt(int visitcnt) {
		this.visitcnt = visitcnt;
	}


	public String getHostmemo() {
		return hostmemo;
	}


	public void setHostmemo(String hostmemo) {
		this.hostmemo = hostmemo;
	}
	
	
	
}
