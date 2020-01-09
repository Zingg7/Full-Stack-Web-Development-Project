package cn.tedu.store.entity;

/**
 *  用于封装购物车列表中
 *  一条记录数据的实体类
*/
public class CartVO {
	
	private Integer cid;
	private Integer pid;
	private String image;
	private String title;
	private Long realPrice; // 商品表中真实价格
	private Long price; // 添加购物车时的商品价格
	private Integer num;
	public CartVO() {
		super();
	}
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Long getRealPrice() {
		return realPrice;
	}
	public void setRealPrice(Long realPrice) {
		this.realPrice = realPrice;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	@Override
	public String toString() {
		return "CartVO [cid=" + cid + ", pid=" + pid + ", image=" + image + ", title=" + title + ", realPrice="
				+ realPrice + ", price=" + price + ", num=" + num + "]";
	}

}
